package com.example.ledapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.mokwon.ice.ledapplication.AceBluetoothSerialService;
import kr.ac.mokwon.ice.ledapplication.BluetoothRx;
import kr.ac.mokwon.ice.ledapplication.R;

public class MainActivity extends AppCompatActivity {
    private static final int BTH_ENABLE_CODE = 1234;
    protected BluetoothAdapter bthAdapter;
    protected BluetoothRx bthRx;
    protected AceBluetoothSerialService bthService;
    protected Button bthFind, bthFindable, bthConnect;
    protected Button  btnRead;
    protected TextView txRead;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BTH_ENABLE_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_LONG).show();         //블루투스 연결 성공 알림
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bthAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bthAdapter == null) {
            Toast.makeText(this, "Bluetooth isn't supported", Toast.LENGTH_LONG).show();    //블루투스 연결 실패 알림
            return;
        }
        if (!bthAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BTH_ENABLE_CODE);
        }
        if (!bthAdapter.isEnabled()) {
            return;
        }

        bthFindable = (Button) findViewById(R.id.btnFindable);
        bthFindable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivity(intent);
            }
        });

        bthFind = (Button) findViewById(R.id.btnFind);
        bthFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (bthAdapter.isDiscovering()) {
                    bthAdapter.cancelDiscovery();
                }
                bthAdapter.startDiscovery();
            }
        });

        bthConnect = (Button) findViewById(R.id.btnConnect);
        bthConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!bthRx.sDeviceAddress.isEmpty()) {
                    BluetoothDevice device = bthAdapter.getRemoteDevice(bthRx.sDeviceAddress);
                    bthService.connect(device);
                }
            }
        });

        txRead = (TextView) findViewById(R.id.txRead);
        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                txRead.setText(null);
                String str = bthService.sReadBuffer;
                bthService.sReadBuffer = " ";
                txRead.append(str);
            }
        });

        bthService = new AceBluetoothSerialService(this, bthAdapter);

        bthRx = new BluetoothRx("YEEUN");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);             //블루투스 디바이스를 가져오기 위한 함수
        registerReceiver(bthRx, intentFilter);
    }
}

