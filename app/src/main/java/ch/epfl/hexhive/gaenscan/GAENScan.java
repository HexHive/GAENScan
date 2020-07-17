package ch.epfl.hexhive.gaenscan;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class GAENScan {
    private SortedMap<String, GAENDevice> active_devices;
    private SortedMap<String, GAENDevice> passive_devices;

    public GAENScan() {
        this.active_devices = new TreeMap<>();
        this.passive_devices = new TreeMap<>();
    }

    public void addObservation(String addr, int rssi, long timestamp) {
        // Passive device becomes active again (after missing beacons)
        GAENDevice dev = this.passive_devices.get(addr);
        if (dev != null) {
            this.passive_devices.remove(addr);
            this.active_devices.put(addr, dev);
        }
        // Active device in our list
        dev = this.active_devices.get(addr);
        if (dev != null) {
            dev.addMeasurement(rssi, timestamp);
        } else {
            // new device
            GAENDevice device = new GAENDevice(addr, rssi, timestamp);
            this.active_devices.put(addr, device);
        }
    }

    @Override @NonNull
    public String toString() {

        StringBuilder entries = new StringBuilder();
        final ArrayList<GAENDevice> new_passive = new ArrayList<>();

        for (GAENDevice entry : this.active_devices.values()) {
            // check if device is still active, otherwise skip
            if (!entry.isActive()) {
                new_passive.add(entry);
                continue;
            }

            String entry_str = String.format(Locale.US,
                    "%s (avg %d, max %d, cur %d, loss %.02f)\n",
                    entry.getMAC(),
                    Math.round(entry.getAverage()),
                    entry.getMax(),
                    entry.getLast(),
                    entry.getPacketLoss()
            );
            entries.append(entry_str);
        }

        for (GAENDevice entry : new_passive) {
            this.active_devices.remove(entry.getMAC());
            this.passive_devices.put(entry.getMAC(), entry);
        }

        return String.format(Locale.US,
                "Number of active devices: %d (total: %d)\n%s",
                active_devices.size(),
                (active_devices.size() + passive_devices.size()),
                entries.toString()
        );
    }
}
