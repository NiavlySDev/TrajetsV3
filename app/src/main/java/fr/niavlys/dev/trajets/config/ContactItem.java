package fr.niavlys.dev.trajets.config;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactItem {
    private final String id;
    private String name;
    private String phone;

    public ContactItem(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("name", name);
        object.put("phone", phone);
        return object;
    }

    public static ContactItem fromJson(JSONObject object) throws JSONException {
        return new ContactItem(
                object.getString("id"),
                object.getString("name"),
                object.getString("phone")
        );
    }
}
