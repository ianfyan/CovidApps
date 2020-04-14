package com.fs.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private TextView text;
    FirebaseAuth firebaseAuth;
    BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image=(ImageView) findViewById(R.id.splash_image);
        text=(TextView) findViewById(R.id.splash_name);
        firebaseAuth = FirebaseAuth.getInstance();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Animation anim= AnimationUtils.loadAnimation(this, R
                .anim.mytransition);
        image.startAnimation(anim);
        text.startAnimation(anim);
        final Intent intent=new Intent(this, NewActivity.class);
        final Intent intentHome=new Intent(this, HomeActivity.class);
        Thread timer=new Thread(){
            public void run(){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if (firebaseAuth.getCurrentUser() == null){
                        startActivity(intent);
                        finish();
                    }
                    if (firebaseAuth.getCurrentUser() != null){
                        if (bluetoothAdapter.isEnabled()){
                            startActivity(intentHome);
                            finish();
                        }else{
                            startActivity(new Intent(MainActivity.this, BluetoothStartActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                        }
                    }
                }
            }
        };
        timer.start();
    }
}
