package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignupActivity extends BaseActivity {
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
    }

    private void setVariable(){
        binding.SignUpBtn.setOnClickListener(v -> {
            String email=binding.userEdit.getText().toString();
            String password=binding.passEdit.getText().toString();

            if(password.length()<6) {
                Toast.makeText(SignupActivity.this, "your password must be 6 character", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignupActivity.this, task -> {
                if(task.isSuccessful()){
                    Log.i(TAG, "onComplete: ");
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                }else {
                    Log.i(TAG, "failure: "+task.getException());
                    Toast.makeText(SignupActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}