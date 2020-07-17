package ch.epfl.hexhive.gaenscan;

import android.os.SystemClock;

import java.util.ArrayList;

public class GAENDevice {
    private String mac;
    private ArrayList<BLEAdvertisement> measurements;
    private int max;
    private long sum;

    private static final int ACTIVE_THRESHOLD = 5; // s
    private static final int GAEN_CADENCE = 250; // ms

    private static final long NSINS = 1000000000;
    private static final long NSINMS = 1000000;

    public GAENDevice(String mac, int rssi, long timestamp) {
        this.mac = mac;
        this.measurements = new ArrayList<>();
        this.max = rssi;
        this.sum = 0;
        this.addMeasurement(rssi, timestamp);
    }

    public void addMeasurement(int rssi, long timestamp) {
        this.measurements.add(new BLEAdvertisement(rssi, timestamp));
        this.sum = this.sum + rssi;
        if (this.max < rssi)
            this.max = rssi;
    }

    public boolean isActive() {
        BLEAdvertisement last = this.measurements.get(this.measurements.size()-1);
        long diff = Math.abs(SystemClock.elapsedRealtimeNanos() - last.getTimestamp());
        return (diff < ACTIVE_THRESHOLD*NSINS);
    }

    public String getMAC() {
        return this.mac;
    }

    public int getMax() {
        return this.max;
    }

    public int getLast() {
        return this.measurements.get(this.measurements.size()-1).getRSSI();
    }

    public float getAverage() {
        return ((float)this.sum)/measurements.size();
    }

    public float getPacketLoss() {
        long first = this.measurements.get(0).getTimestamp();
        long last = this.measurements.get(this.measurements.size()-1).getTimestamp();
        long expected = (last-first)/(GAEN_CADENCE*NSINMS)+1;
        return 1-((float)this.measurements.size())/expected;
    }

}
