package com.example.havel.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnSearch)
    Button btnSearch;


    @BindView(R.id.tbtnSwitch)
    ToggleButton tbtnSwitch;
    @BindView(R.id.lvDevices)
    ListView lvDevices;
    public ArrayAdapter<String> adapter;


    @BindView(R.id.linearlaout1)
    LinearLayout linearlaout1;
    @BindView(R.id.btnExit)
    Button btnExit;
    private BluetoothAdapter bluetoothAdapter;

    public Context context;
    private boolean btIsOpen = false;
    public static final int REQUEST_OPEN = 0X01;
    public static final String TAG = "BLUETOOTH";
    private ArrayList<String> list = new ArrayList<String>();
    private Set<BluetoothDevice> bondDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        lvDevices.setAdapter(adapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(searchReceiver, intent);
        if (bluetoothAdapter == null) {
            showToast("该设备不支持蓝牙功能");
            return;
        }
    }

    @OnClick({R.id.btnSearch, R.id.tbtnSwitch,R.id.btnExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch: {
                checkBondedDevices();

            }
            break;

            //查找已配对的设备


            case R.id.btnExit:
                MainActivity.this.finish();
                break;
            case R.id.tbtnSwitch: {
                if (tbtnSwitch.isChecked() == true) {
                    Intent openBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(openBlueTooth, REQUEST_OPEN);
                } else if (tbtnSwitch.isChecked() == false) {
                    bluetoothAdapter.disable();
                }
            }
            break;
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN) {
            if (requestCode == RESULT_CANCELED)
                showToast("请求失败");
            else {
                showToast("请求成功");


            }
        }

    }

    //以上是打开蓝牙操作
    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

        }
    }

    public void checkBondedDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        list.clear();
        bondDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondDevices) {
            String str = "已配对完成 " + device.getName() + "" + device.getAddress();
            list.add(str);
            adapter.notifyDataSetChanged();
        }

        bluetoothAdapter.startDiscovery();

    }

    private final BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Toast.makeText(context, device.getName() + "", Toast.LENGTH_LONG).show();
                    String str = "未配对完成  " + "名称："+device.getName() + " MAC地址：" + device.getAddress();
                    if (list.indexOf(str) == -1)
                        list.add(str);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };




}

