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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Login_Page extends AppCompatActivity{
    TextView extraSignUpText;
    CardView loginWithGoogle;
    Button loginButton;
    TextInputEditText loginEmail, loginPassword;
    ProgressBar loginProBar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginMotherLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth= FirebaseAuth.getInstance();

        loginWithGoogle= findViewById(R.id.loginWithGoogle);
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
//======================SignIn with google========================

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(Login_Page.this, gso);


        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
//======================SignIn with google========================

        extraSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Page.this, Signup_Page.class));
                finish();
            }
        });



    }

//======================SignIn with google Methods========================

    private void signIn(){
        Intent signInIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN){
            Task <GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                loginEmail.setText(""+e);
                Toast.makeText(this, "Login Failed: "+e, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void firebaseAuth(String idToken){

        AuthCredential credential= GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(Login_Page.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Login_Page.this, "Logd In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login_Page.this, Dash_Board.class));
                    finish();
                }
                else {
                    Toast.makeText(Login_Page.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //======================SignIn with google Methods=====================================




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
//======================================================
}