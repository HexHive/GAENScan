package ch.epfl.hexhive.gaenscan;

public class BLEAdvertisement {
    private int rssi;
    private long timestamp;

    public BLEAdvertisement(int rssi, long timestamp) {
        this.rssi = rssi;
        this.timestamp = timestamp;
    }

    public int getRSSI() {
        return rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
