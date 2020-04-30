package com.fs.covid_19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fs.covid_19.item.Member;
import com.fs.covid_19.item.Rekam;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class RekamActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonScan, buttonPaired;
    private ListView devicesList;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private Calendar calendar;
    Member member;
    Rekam rekam;
    DatabaseReference reff;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam);

        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonPaired= (Button) findViewById(R.id.buttonPaired);
        devicesList = (ListView)findViewById(R.id.devicesList);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        devicesList.setAdapter(listAdapter);
        checkBluetoothState();
        rekam=new Rekam();
        reff = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        calendar=Calendar.getInstance();

        registerReceiver(devicesFoundReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        checkCoarseLocationPermission();

        buttonScan.setOnClickListener(this);
        buttonPaired.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == buttonPaired){
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device: devices){
                listAdapter.clear();
                listAdapter.add(device.getName()+"\n"+device.getAddress());
                listAdapter.notifyDataSetChanged();
            }
            //Intent dis = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //dis.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            //startActivity(dis);
        }
        if (view == buttonScan){
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
                listAdapter.clear();
                bluetoothAdapter.startDiscovery();
            }else {
                checkBluetoothState();
            }
        }
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

    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                Toast.makeText(context, "Devices Found", Toast.LENGTH_SHORT).show();
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listAdapter.add(device.getName()+"\n"+device.getAddress());
                listAdapter.notifyDataSetChanged();
                Query query= reff.child("Member").orderByChild("macAddress").equalTo(device.getAddress());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                member = snapshot.getValue(Member.class);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
                                String tanggal = sdf.format(calendar.getTime());

                                rekam.setMacAddress(member.getMacAddress());
                                rekam.setName(member.getName());
                                rekam.setDate(tanggal);
                                reff.child("Rekam").child("cobacoba").child(snapshot.getKey()).setValue(rekam);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "MAC Address tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                buttonScan.setText("Scanning Bluetooth Devices");
            }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                buttonScan.setText("Scanning in Progress ...");
            }
        }
    };

    private void checkBluetoothState() {
        if (bluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth is Not Supported oOn Your Devices", Toast.LENGTH_SHORT).show();
        }else {
            if (bluetoothAdapter.isEnabled()){
                if (bluetoothAdapter.isDiscovering()){
                    Toast.makeText(this, "Devices Discovering Process ...", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Bluetooth is Enabled", Toast.LENGTH_SHORT).show();
                    buttonScan.setEnabled(true);
                }
            }else {
                Toast.makeText(this, "You Need to Enabled Your Bluetooth Devices", Toast.LENGTH_SHORT).show();
                Intent ena=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ena,REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            checkBluetoothState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(devicesFoundReceiver);
    }
}
