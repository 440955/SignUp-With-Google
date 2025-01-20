package com.dbjon.signupwithgoogle;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class Signup_Page extends AppCompatActivity {

    TextInputEditText signUpEmail, signUpPin, signUpConfirmPin;
    TextView extraLoginText;
    Button signUpButton;

    CardView signUpWithGoogle;
    ProgressBar proBar;
    private FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    private int REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signMotherLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signUpWithGoogle= findViewById(R.id.signUpWithGoogle);
        proBar= findViewById(R.id.proBar);
        signUpEmail= findViewById(R.id.signUpEmail);
        signUpPin= findViewById(R.id.signUpPin);
        signUpConfirmPin= findViewById(R.id.signUpConfirmPin);
        extraLoginText= findViewById(R.id.extraLoginText);
        signUpButton= findViewById(R.id.signUpButton);

        firebaseAuth= FirebaseAuth.getInstance();

        extraLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup_Page.this, Login_Page.class));
                finish();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email= signUpEmail.getText().toString();
                String Pin= signUpPin.getText().toString();
                String ConfirmPin= signUpConfirmPin.getText().toString();

                if (email.isEmpty()){
                    signUpEmail.requestFocus();
                    return;
                } if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signUpEmail.requestFocus();
                    return;
                }if (Pin.isEmpty()) {
                    signUpPin.requestFocus();
                }if (ConfirmPin.isEmpty()) {
                    signUpConfirmPin.requestFocus();
                    return;
                }if (!Pin.contains(ConfirmPin) && !ConfirmPin.contains(Pin)) {
                    Toast.makeText(Signup_Page.this, "Pin not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    SignUp(email, ConfirmPin);
                }



            }
        });
//==================step-1 Google signin potions===================

        googleSignInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(Signup_Page.this, googleSignInOptions);

//==================step-1 Google signin potions===================

//==================step-2 Google signin potions===================
        signUpWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent= googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE);
            }
        });
//==================step-2 Google signin potions===================
    }

//==================step-3 Google signin potions===================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebase(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebase(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Signup_Page.this, "SignIn Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signup_Page.this, Dash_Board.class));
                    finish();
                }
                else {
                    Toast.makeText(Signup_Page.this, "SignUp Faill!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//==================step-3 Google signin potions===================



    private void SignUp(String Email, String Password){

        proBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                proBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(Signup_Page.this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signup_Page.this, Dash_Board.class));
                    finish();
                }
                else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(Signup_Page.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



}