package ch.epfl.hexhive.gaenscan;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.text.method.ScrollingMovementMethod;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int REQUEST_ENABLE_BLE = 1;
    private static final String GAEN_UUID = "0000fd6f-0000-1000-8000-00805f9b34fb";

    TextView scan_results;
    BluetoothLeScanner ble_scanner;

    private GAENScan scan_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch scan_button = findViewById(R.id.scanswitch);
        scan_results = findViewById(R.id.scan_results);
        scan_results.setMovementMethod(new ScrollingMovementMethod());

        scan_data = new GAENScan();

        // Toggle scanning
        scan_button.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                AsyncTask.execute(() -> {
                    ScanSettings ble_scan_settings = new ScanSettings.Builder().
                            setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).
                            setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).
                            setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT).
                            setReportDelay(0L).
                            build();
                    ScanFilter ble_filter = new ScanFilter.Builder().
                            setServiceUuid(ParcelUuid.fromString(GAEN_UUID)).
                            build();
                    List<ScanFilter> ble_filters = new ArrayList<>();
                    ble_filters.add(ble_filter);

                    ble_scanner.startScan(ble_filters, ble_scan_settings, bleScanCallback);

                    System.out.println("Start scanning...");
                });
            } else {
                AsyncTask.execute(() -> {
                    ble_scanner.stopScan(bleScanCallback);
                    System.out.println("Stop scanning...");
                });
            }
        });

        BluetoothManager bt_manager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bt_adapter = bt_manager.getAdapter();

        // Enable BT / BLE if not already running
        if (!bt_adapter.isEnabled()) {
            Intent enableBLE = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBLE,REQUEST_ENABLE_BLE);
        }

        ble_scanner = bt_adapter.getBluetoothLeScanner();

        // Check that we have sufficient permissions to access low end BLE scans
        if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        }

    }

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String mac = result.getDevice().getAddress();
            //byte[] adv = result.getScanRecord().getBytes();
            int rssi = result.getRssi();
            long timestamp = result.getTimestampNanos();
            //scan_results.append("Addr: " + addr + " RSSI: " + rssi + "\n");
            scan_data.addObservation(mac, rssi, timestamp);
            scan_results.setText(scan_data.toString());
            System.out.println("Addr: " + mac + " RSSI: " + rssi);
        }
    };

}