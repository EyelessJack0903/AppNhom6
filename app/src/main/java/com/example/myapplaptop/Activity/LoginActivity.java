package com.example.myapplaptop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/*facebook*/
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private ImageButton imageButton;
    private FirebaseDatabase firebaseDatabase;
    private GoogleSignInClient mgoogleSignInClient;
    private ProgressDialog progressDialog;
    int RC_SIGN_IN = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("we are creating your account");

            /*signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(GoogleIdTokenRequestOptions.builder()
        .setSupported(true)
        .setServerClientId(getString(R.string.default_web_client_id))
        .setFilterByAuthorizedAccounts(true)
        .build())
           .build();*/


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //truyền server client ID
                .requestIdToken(getString(R.string.default_web_client_id))
                //show email người dùng
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this, gso);

        imageButton = findViewById(R.id.gmailLogin);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        setVariables();
    }

    private void signIn() {
        Intent intent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            gmailUser users = new gmailUser();
                            users.setUserId(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setProfile(user.getPhotoUrl().toString());

                            firebaseDatabase.getReference().child("gmailUser").child(user.getUid()).setValue(users);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Check if email is verified
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                // Login successful and email is verified, navigate to MainActivity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // Close LoginActivity
                            } else {
                                // Email is not verified, sign out the user and show a message
                                mAuth.signOut();
                                Toast.makeText(LoginActivity.this, "Vui lòng xác minh email của bạn trước khi đăng nhập!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Login failed, display error message
                            Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu, vui lòng thử lại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Set click listener for Forget Password TextView
        binding.forgetPass.setOnClickListener(view -> {
            String email = binding.userEdit.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập địa chỉ email của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
