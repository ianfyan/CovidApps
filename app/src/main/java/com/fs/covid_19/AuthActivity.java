package com.fs.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_MEMBER = "extra_member";
    private EditText editTextAuth;
    private TextView textAuthProgress;
    private Button cirAuthButton;
    private String codeSent;
    DatabaseReference reff;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        editTextAuth = (EditText) findViewById(R.id.editTextAuth);
        textAuthProgress = (TextView) findViewById(R.id.textAuthProgress);
        cirAuthButton = (Button) findViewById(R.id.cirAuthButton);
        reff = FirebaseDatabase.getInstance().getReference().child("Member");
        mAuth = FirebaseAuth.getInstance();
        member = getIntent().getParcelableExtra(EXTRA_MEMBER);

        sendVerificationCode(member.getPhoneNumber());
        cirAuthButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        verifyCode();
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            textAuthProgress.setText(R.string.sended);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d("Error",e.getMessage());
            Toast.makeText(AuthActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    private void verifyCode() {
        String code = editTextAuth.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(member.getName())
                                    .build();

                            user = mAuth.getCurrentUser();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("dialog", "User profile updated.");
                                            }
                                        }
                                    });
                            user.updateEmail(member.getEmail())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("dialog", "User email address updated.");
                                            }
                                        }
                                    });
                            user.updatePassword(member.getPassword())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("dialog", "User password updated.");
                                            }
                                        }
                                    });
                            reff.child(user.getUid()).setValue(member);
                            Toast.makeText(AuthActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AuthActivity.this, BluetoothStartActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(AuthActivity.this, "Kode Salah", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
