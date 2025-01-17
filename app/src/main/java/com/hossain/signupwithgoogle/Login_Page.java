package com.hossain.signupwithgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


public class Login_Page extends AppCompatActivity{
    TextView extraSignUpText;
    Button loginButton;
    TextInputEditText loginEmail, loginPassword;
    ProgressBar loginProBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginMotherLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth= FirebaseAuth.getInstance();

        loginButton= findViewById(R.id.loginButton);
        loginEmail= findViewById(R.id.loninEmail);
        loginPassword= findViewById(R.id.loginPassword);
        extraSignUpText= findViewById(R.id.extraSignUpText);
        loginProBar= findViewById(R.id.loginProBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= loginEmail.getText().toString();
                String password= loginPassword.getText().toString();
                if (email.isEmpty()){
                    loginEmail.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    loginEmail.requestFocus();
                    return;
                }
                else if (password.isEmpty() || password.length()<5){
                    loginPassword.requestFocus();
                    return;
                }
                else{
                    Login(email, password);
                }
            }
        });

        extraSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Page.this, Signup_Page.class));
                finish();
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Login_Page.this, Dash_Board.class));
            finish();
        }
    }

    private void Login(String email, String password){
        loginProBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loginProBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    startActivity(new Intent(Login_Page.this, Dash_Board.class));
                    finish();
                }
                else {
                    Toast.makeText(Login_Page.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}