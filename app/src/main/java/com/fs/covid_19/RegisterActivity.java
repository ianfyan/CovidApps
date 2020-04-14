package com.fs.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.covid_19.item.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextName,editTextEmail,editTextMobile,editTextSandi;
    private Button cirRegisterButton;
    private TextView textLogin;
    Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        editTextSandi = (EditText) findViewById(R.id.editTextSandi);
        cirRegisterButton = (Button) findViewById(R.id.cirRegisterButton);
        textLogin = (TextView) findViewById(R.id.textLogin);

        member = new Member();

        cirRegisterButton.setOnClickListener(this);
        textLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == cirRegisterButton){
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String number = editTextMobile.getText().toString().trim();
            String sandi = editTextSandi.getText().toString().trim();

            if (name.isEmpty()){
                editTextName.setError("Name Required");
                editTextName.requestFocus();
                return;
            }
            if (email.isEmpty()){
                editTextEmail.setError("Email Required");
                editTextEmail.requestFocus();
                return;
            }
            if (number.isEmpty()){
                editTextMobile.setError("Phone Number Required");
                editTextMobile.requestFocus();
                return;
            }
            if (sandi.isEmpty()){
                editTextSandi.setError("Password Required");
                editTextSandi.requestFocus();
                return;
            }

            member.setName(name);
            member.setEmail(email);
            member.setPhoneNumber(number);
            member.setPassword(sandi);

            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra(AuthActivity.EXTRA_MEMBER, member);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

        }

        if (view == textLogin){
            startActivity(new Intent(this,NewActivity.class));
            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }

}
