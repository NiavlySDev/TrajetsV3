package fr.niavlys.dev.trajets.config;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class ConfigRepository {
    private static final String PREFS = "trajets_config";
    private static final String KEY_CONFIG = "config";

    private final SharedPreferences preferences;

    public ConfigRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public AppConfig load() {
        String raw = preferences.getString(KEY_CONFIG, null);
        if (raw == null) {
            AppConfig config = defaults();
            save(config);
            return config;
        }

        try {
            JSONObject root = new JSONObject(raw);
            AppConfig config = new AppConfig();
            readConfigItems(root.getJSONArray("transports"), config.getTransports());
            readConfigItems(root.getJSONArray("stations"), config.getStations());
            readContacts(root.getJSONArray("contacts"), config.getContacts());
            return config;
        } catch (JSONException exception) {
            AppConfig config = defaults();
            save(config);
            return config;
        }
    }

    public void save(AppConfig config) {
        try {
            JSONObject root = new JSONObject();
            root.put("transports", writeConfigItems(config.getTransports()));
            root.put("stations", writeConfigItems(config.getStations()));
            root.put("contacts", writeContacts(config.getContacts()));
            preferences.edit().putString(KEY_CONFIG, root.toString()).apply();
        } catch (JSONException ignored) {
        }
    }

    public String newId() {
        return UUID.randomUUID().toString();
    }

    public void resetToDefaults() {
        save(defaults());
    }

    private AppConfig defaults() {
        AppConfig config = new AppConfig();

        add(config.getTransports(), "Tramway 4", "Tramway 4");
        add(config.getTransports(), "Transilien", "Transilien");
        add(config.getTransports(), "Nomad", "Nomad");
        add(config.getTransports(), "Métro 4", "Métro 4");
        add(config.getTransports(), "Métro 14", "Métro 14");
        add(config.getTransports(), "RER C", "RER C");
        add(config.getTransports(), "RER D", "RER D");
        add(config.getTransports(), "Bus 13", "Bus 13");
        add(config.getTransports(), "Bus 16", "Bus 16");
        add(config.getTransports(), "Bus 17", "Bus 17");
        add(config.getTransports(), "Bus 305", "Bus 305");

        add(config.getStations(), "Evreux", "la Gare D'Evreux");
        add(config.getStations(), "Saint Lazare", "Saint Lazare");
        add(config.getStations(), "BFM", "Bibliotheque François Miterrand");
        add(config.getStations(), "Juvisy", "Juvisy");
        add(config.getStations(), "Danton", "Danton");
        add(config.getStations(), "Saint Michel", "Saint Michel Notre Dame");
        add(config.getStations(), "Montparnasse", "Montparnasse");
        add(config.getStations(), "Houdan", "Houdan");
        add(config.getStations(), "V-Chantiers", "Versailles Chantiers");
        add(config.getStations(), "V-Chateau", "Versailles Chateau Rive Gauche");
        add(config.getStations(), "Austerlitz", "Paris Austerlitz");
        add(config.getStations(), "GDL", "Gare de Lyon");
        add(config.getStations(), "Bercy", "Bercy");

        config.getContacts().add(new ContactItem(newId(), "Maman", "0682377140"));
        config.getContacts().add(new ContactItem(newId(), "Papa", "0637038218"));
        config.getContacts().add(new ContactItem(newId(), "Patou", "0686525035"));
        return config;
    }

    private void add(List<ConfigItem> items, String label, String phrase) {
        items.add(new ConfigItem(newId(), label, phrase));
    }

    private void readConfigItems(JSONArray array, List<ConfigItem> items) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            items.add(ConfigItem.fromJson(array.getJSONObject(i)));
        }
    }

    private void readContacts(JSONArray array, List<ContactItem> contacts) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            contacts.add(ContactItem.fromJson(array.getJSONObject(i)));
        }
    }

    private JSONArray writeConfigItems(List<ConfigItem> items) throws JSONException {
        JSONArray array = new JSONArray();
        for (ConfigItem item : items) {
            array.put(item.toJson());
        }
        return array;
    }

    private JSONArray writeContacts(List<ContactItem> contacts) throws JSONException {
        JSONArray array = new JSONArray();
        for (ContactItem contact : contacts) {
            array.put(contact.toJson());
        }
        return array;
    }
}
