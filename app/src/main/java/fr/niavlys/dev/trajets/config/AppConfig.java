package fr.niavlys.dev.trajets.config;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private final List<ConfigItem> transports;
    private final List<ConfigItem> stations;
    private final List<ContactItem> contacts;

    public AppConfig() {
        transports = new ArrayList<>();
        stations = new ArrayList<>();
        contacts = new ArrayList<>();
    }

    public List<ConfigItem> getTransports() {
        return transports;
    }

    public List<ConfigItem> getStations() {
        return stations;
    }

    public List<ContactItem> getContacts() {
        return contacts;
    }
}
