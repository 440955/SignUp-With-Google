package com.hossain.signupwithgoogle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dash_Board extends AppCompatActivity {

    Button logoutButton;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    CircleImageView proPic;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashBoardMotherLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName= findViewById(R.id.userName);
        proPic= findViewById(R.id.proPic);
        firebaseAuth= FirebaseAuth.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        logoutButton= findViewById(R.id.logOutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(Dash_Board.this, Login_Page.class));
                finish();
            }
        });

        if (user!=null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String imageLink= String.valueOf(user.getPhotoUrl());
            String uid = user.getUid();

            userName.setText(email);
            Picasso.get().load(imageLink)
                    .into(proPic);
        }

    }
}