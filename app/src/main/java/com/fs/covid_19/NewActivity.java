
package com.fs.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fs.covid_19.item.Member;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;
import java.util.List;

public class NewActivity extends AppCompatActivity implements View.OnClickListener{
    private int RC_SIGN_IN=1;
    private static final String TAG = "NewActivity";
    String phone,password;
    private EditText editTextPhone;
    private TextView textRegister;
    private Button cirLoginButton;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;
    Member member;
    FirebaseAuth fAuth;
    DatabaseReference reff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        textRegister = (TextView) findViewById(R.id.textRegister);
        cirLoginButton = (Button) findViewById(R.id.cirLoginButton);
        loginButton =  (LoginButton) findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.googleSignIn);
        callbackManager = CallbackManager.Factory.create();
        fAuth = FirebaseAuth.getInstance();
        reff = FirebaseDatabase.getInstance().getReference().child("Member");

        cirLoginButton.setOnClickListener(this);
        textRegister.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"Success"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "facebook:onError", e);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == cirLoginButton){
            phone = editTextPhone.getText().toString().trim();

            if (phone.isEmpty()){
                editTextPhone.setError("Phone Number Required");
                editTextPhone.requestFocus();
                return;
            }
            cekLogin();
        }

        if (view == textRegister){
            startActivity(new Intent(this,RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        }
        if (view == signInButton){
            signIn();
        }
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    member = snapshot.getValue(Member.class);
                }
                Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                i.putExtra(AuthActivity.EXTRA_MEMBER, member);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
            }else {
                Toast.makeText(getApplicationContext(),"Nomer Telepon Belum Terdaftar",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void cekLogin() {
        Query query= reff.orderByChild("phoneNumber").equalTo(phone);
        query.addValueEventListener(valueEventListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void handleFacebookToken(AccessToken accessToken) {
        Log.d("Facebook","HandleFacebookToken"+accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        fAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //LoginManager.getInstance().logInWithReadPermissions(getParent(), Arrays.asList("public_profile"));
                    startActivity(new Intent(getApplicationContext(), BluetoothStartActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(getApplicationContext(), BluetoothStartActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                            FirebaseUser user = fAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void homeActivity() {
        fAuth.signInWithEmailAndPassword(phone, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Member member=new Member();
                            Intent i = new Intent(NewActivity.this, HomeActivity.class);
                            //i.putExtra(AuthActivity.EXTRA_MEMBER, member);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
