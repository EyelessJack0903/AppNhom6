package com.example.myapplaptop.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplaptop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEdt, newPasswordEdt, confirmPasswordEdt;
    private Button updatePasswordBtn;
    private ImageView goBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        // Khởi tạo FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các View từ layout
        oldPasswordEdt = findViewById(R.id.oldPasswordEdt);
        newPasswordEdt = findViewById(R.id.newPassword);
        confirmPasswordEdt = findViewById(R.id.comfirmnewPassword);
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn);
        goBack = findViewById(R.id.goBack);

        // Xử lý sự kiện khi người dùng click vào nút cập nhật mật khẩu
        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        // Xử lý sự kiện khi người dùng click vào ImageView goBack
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Quay lại trang trước đó
            }
        });
    }

    private void updatePassword() {
        // Lấy thông tin từ EditText
        String oldPassword = oldPasswordEdt.getText().toString().trim();
        String newPassword = newPasswordEdt.getText().toString().trim();
        String confirmPassword = confirmPasswordEdt.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (oldPassword.isEmpty()) {
            oldPasswordEdt.setError("Enter old password");
            oldPasswordEdt.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            newPasswordEdt.setError("Enter new password");
            newPasswordEdt.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEdt.setError("Confirm new password");
            confirmPasswordEdt.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEdt.setError("Passwords do not match");
            confirmPasswordEdt.requestFocus();
            return;
        }

        // Lấy thông tin người dùng hiện tại
        FirebaseUser user = mAuth.getCurrentUser();

        // Cập nhật mật khẩu mới cho người dùng
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdatePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Kết thúc hoạt động và quay lại trang trước đó
                        } else {
                            Toast.makeText(UpdatePasswordActivity.this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
