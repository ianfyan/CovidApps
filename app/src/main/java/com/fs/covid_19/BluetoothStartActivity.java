package com.fs.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BluetoothStartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT=0;
    private TextView textStatus;
    private ImageView imageStatus;
    private Button nextButton;
    BluetoothAdapter bluetoothAdapter;
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
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_ENABLE_BT);
            startActivity(new Intent(BluetoothStartActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        }else {
            startActivity(new Intent(BluetoothStartActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        }
    }
}
