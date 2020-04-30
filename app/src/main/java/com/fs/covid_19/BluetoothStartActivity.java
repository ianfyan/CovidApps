package com.fs.covid_19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BluetoothStartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewActivity";
    public static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_ENABLE_BT=0;
    private TextView textStatus;
    private ImageView imageStatus;
    private Button nextButton;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_start);

        textStatus = (TextView) findViewById(R.id.textStatus);
        imageStatus = (ImageView) findViewById(R.id.imageStatus);
        nextButton = (Button) findViewById(R.id.nextButton);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()){
            textStatus.setText("Bluetooth Is Enabled");
            imageStatus.setImageResource(R.drawable.bluetoothactived);
        }else{
            textStatus.setText("Bluetooth Is Disabled");
            imageStatus.setImageResource(R.drawable.bluetooth);
        }
        checkCoarseLocationPermission();
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_ENABLE_BT);
        }else {
            startActivity(new Intent(BluetoothStartActivity.this, RekamActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    //String macAddress = android.provider.Settings.Secure.getString(this.getContentResolver(), "bluetooth_address");
                    Toast.makeText(this, getBluetoothMac(this), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BluetoothStartActivity.this, RekamActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                }else {
                    Toast.makeText(getApplicationContext(),"Bluetooth Disabled",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getBluetoothMac(final Context context) {

        String result = "tidak ada";
        if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Hardware ID are restricted in Android 6+
                // https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
                // Getting bluetooth mac via reflection for devices with Android 6+
                result = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        "bluetooth_address");
            } else {
                BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                result = bta != null ? bta.getAddress() : "";
            }
        }
        return result;
    }
    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Access Coarse Location Allowed", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Access Coarse Location Not Allowed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
