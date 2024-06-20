// LoginActivity.java
package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplaptop.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        setVariables();
    }

    private void setVariables() {
        // Set click listener for Sign Up TextView
        binding.textView14.setOnClickListener(view -> {
            // Start SignupActivity
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        // Set click listener for Login Button
        binding.LoginBtn.setOnClickListener(view -> {
            String email = binding.userEdit.getText().toString().trim();
            String password = binding.passEdit.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Hãy điền đầy đủ các thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate user with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Login successful, navigate to MainActivity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Close LoginActivity
                        } else {
                            // Login failed, display error message
                            Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu, vui lòng thử lại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
