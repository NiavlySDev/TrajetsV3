package fr.niavlys.dev.trajets.config;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigItem {
    private final String id;
    private String label;
    private String phrase;

    public ConfigItem(String id, String label, String phrase) {
        this.id = id;
        this.label = label;
        this.phrase = phrase;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("label", label);
        object.put("phrase", phrase);
        return object;
    }

    public static ConfigItem fromJson(JSONObject object) throws JSONException {
        return new ConfigItem(
                object.getString("id"),
                object.getString("label"),
                object.getString("phrase")
        );
    }
}
