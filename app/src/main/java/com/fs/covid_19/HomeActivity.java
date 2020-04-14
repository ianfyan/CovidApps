package com.fs.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogout;
    TextView textView;
    FirebaseAuth firebaseAuth;
    String name, email, number, uid;
    boolean emailVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textView = (TextView) findViewById(R.id.textView);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        onStart();
    }

    @Override
    public void onClick(View view) {
        firebaseAuth.signOut();
        startActivity(new Intent(this,NewActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                uid = profile.getUid();

                // Name, email address, and profile photo Url
                name = profile.getDisplayName();
                email = profile.getEmail();
                emailVerified = user.isEmailVerified();
                number = user.getPhoneNumber();
                Uri photoUrl = profile.getPhotoUrl();
            }
        }
        /*if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            emailVerified = user.isEmailVerified();
            number = user.getPhoneNumber();
            uid = user.getUid();
        }*/
        textView.setText("name = "+name+"\nemail = "+email+"\nphone = "+number+"\nuid = "+uid+"\nverified = "+emailVerified);
    }
}
