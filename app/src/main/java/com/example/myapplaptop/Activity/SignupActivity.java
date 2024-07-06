package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        setVariables();
    }

    private void setVariables() {
        binding.SignUpBtn.setOnClickListener(v -> {
            String email = binding.userEdit.getText().toString().trim();
            String password = binding.passEdit.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignupActivity.this, "Hãy điền email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignupActivity.this, "Hãy điền password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password của bạn phải đủ 6 kí tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Đăng ký thành công");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(verifyTask -> {
                                    if (verifyTask.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Đăng ký thành công. Vui lòng xác minh email của bạn.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Log.e(TAG, "Gửi email xác minh thất bại", verifyTask.getException());
                                        Toast.makeText(SignupActivity.this, "Gửi email xác minh thất bại.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignupActivity.this, "Email đã có, vui lòng chọn email khác.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Lỗi: " + task.getException());
                        Toast.makeText(SignupActivity.this, "Lỗi đăng ký: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        // Handling click on "Are you a member? Login"
        binding.nextLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}
