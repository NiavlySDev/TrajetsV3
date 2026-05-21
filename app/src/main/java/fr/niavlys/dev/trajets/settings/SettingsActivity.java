package fr.niavlys.dev.trajets.settings;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.niavlys.dev.trajets.config.AppConfig;
import fr.niavlys.dev.trajets.config.ConfigItem;
import fr.niavlys.dev.trajets.config.ConfigRepository;
import fr.niavlys.dev.trajets.config.ContactItem;

public class SettingsActivity extends AppCompatActivity {
    private ConfigRepository repository;
    private AppConfig config;
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new ConfigRepository(this);
        config = repository.load();
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(245, 247, 251));

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(24));
        scrollView.addView(content);
        setContentView(scrollView);
        render();
    }

    private void render() {
        content.removeAllViews();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(header, matchWrap());

        TextView title = title("Paramètres");
        header.addView(title, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button close = secondaryButton("Retour");
        close.setOnClickListener(view -> finish());
        header.addView(close);

        addConfigSection("Modes de transport", "Nom affiché", "Texte ajouté au message", config.getTransports());
        addConfigSection("Gares", "Nom affiché", "Texte ajouté au message", config.getStations());
        addContactsSection();

        Button reset = dangerButton("Réinitialiser les données");
        reset.setOnClickListener(view -> confirmReset());
        LinearLayout.LayoutParams resetParams = matchWrap();
        resetParams.setMargins(0, dp(18), 0, 0);
        content.addView(reset, resetParams);
    }

    private void addConfigSection(String title, String labelHint, String phraseHint, List<ConfigItem> items) {
        content.addView(sectionTitle(title));

        LinearLayout card = card();
        content.addView(card, matchWrap());

        for (ConfigItem item : items) {
            LinearLayout row = row();
            TextView text = itemText(item.getLabel(), item.getPhrase());
            row.addView(text, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button edit = smallButton("Modifier");
            edit.setOnClickListener(view -> showConfigDialog(title, labelHint, phraseHint, items, item));
            row.addView(edit);

            Button delete = smallDangerButton("Supprimer");
            delete.setOnClickListener(view -> {
                items.remove(item);
                saveAndRender();
            });
            row.addView(delete);
            card.addView(row, matchWrap());
        }

        Button add = primaryButton("Ajouter");
        add.setOnClickListener(view -> showConfigDialog(title, labelHint, phraseHint, items, null));
        card.addView(add, matchWrap());
    }

    private void addContactsSection() {
        content.addView(sectionTitle("Numéros de téléphone"));
        LinearLayout card = card();
        content.addView(card, matchWrap());

        for (ContactItem contact : config.getContacts()) {
            LinearLayout row = row();
            TextView text = itemText(contact.getName(), contact.getPhone());
            row.addView(text, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button edit = smallButton("Modifier");
            edit.setOnClickListener(view -> showContactDialog(contact));
            row.addView(edit);

            Button delete = smallDangerButton("Supprimer");
            delete.setOnClickListener(view -> {
                config.getContacts().remove(contact);
                saveAndRender();
            });
            row.addView(delete);
            card.addView(row, matchWrap());
        }

        Button add = primaryButton("Ajouter");
        add.setOnClickListener(view -> showContactDialog(null));
        card.addView(add, matchWrap());
    }

    private void showConfigDialog(String title, String labelHint, String phraseHint, List<ConfigItem> items, ConfigItem item) {
        LinearLayout form = dialogForm();
        EditText label = input(labelHint);
        EditText phrase = input(phraseHint);
        if (item != null) {
            label.setText(item.getLabel());
            phrase.setText(item.getPhrase());
        }
        form.addView(label);
        form.addView(phrase);

        new AlertDialog.Builder(this)
                .setTitle(item == null ? "Ajouter - " + title : "Modifier - " + title)
                .setView(form)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String labelValue = label.getText().toString().trim();
                    String phraseValue = phrase.getText().toString().trim();
                    if (labelValue.isEmpty() || phraseValue.isEmpty()) {
                        return;
                    }
                    if (item == null) {
                        items.add(new ConfigItem(repository.newId(), labelValue, phraseValue));
                    } else {
                        item.setLabel(labelValue);
                        item.setPhrase(phraseValue);
                    }
                    saveAndRender();
                })
                .show();
    }

    private void showContactDialog(ContactItem contact) {
        LinearLayout form = dialogForm();
        EditText name = input("Nom");
        EditText phone = input("Numéro");
        if (contact != null) {
            name.setText(contact.getName());
            phone.setText(contact.getPhone());
        }
        form.addView(name);
        form.addView(phone);

        new AlertDialog.Builder(this)
                .setTitle(contact == null ? "Ajouter un numéro" : "Modifier un numéro")
                .setView(form)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String nameValue = name.getText().toString().trim();
                    String phoneValue = phone.getText().toString().trim();
                    if (nameValue.isEmpty() || phoneValue.isEmpty()) {
                        return;
                    }
                    if (contact == null) {
                        config.getContacts().add(new ContactItem(repository.newId(), nameValue, phoneValue));
                    } else {
                        contact.setName(nameValue);
                        contact.setPhone(phoneValue);
                    }
                    saveAndRender();
                })
                .show();
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle("Réinitialiser")
                .setMessage("Remettre les gares, transports et numéros par défaut ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Réinitialiser", (dialog, which) -> {
                    repository.resetToDefaults();
                    config = repository.load();
                    render();
                })
                .show();
    }

    private void saveAndRender() {
        repository.save(config);
        render();
    }

    private LinearLayout dialogForm() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(8), dp(8), dp(8), 0);
        return form;
    }

    private EditText input(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        return input;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setBackground(cardBackground(Color.WHITE));
        return card;
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(6), 0, dp(6));
        return row;
    }

    private TextView title(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(28);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(Color.rgb(21, 32, 43));
        return view;
    }

    private TextView sectionTitle(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(16);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(Color.rgb(71, 85, 105));
        view.setPadding(0, dp(18), 0, dp(8));
        return view;
    }

    private TextView itemText(String title, String subtitle) {
        TextView view = new TextView(this);
        view.setText(title + "\n" + subtitle);
        view.setTextSize(15);
        view.setTextColor(Color.rgb(21, 32, 43));
        return view;
    }

    private Button primaryButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.WHITE);
        button.setBackground(rounded(Color.rgb(37, 99, 235), dp(12)));
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.rgb(21, 32, 43));
        button.setBackground(rounded(Color.WHITE, dp(12)));
        return button;
    }

    private Button dangerButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.WHITE);
        button.setBackground(rounded(Color.rgb(220, 38, 38), dp(12)));
        return button;
    }

    private Button smallButton(String text) {
        Button button = secondaryButton(text);
        button.setTextSize(12);
        return button;
    }

    private Button smallDangerButton(String text) {
        Button button = dangerButton(text);
        button.setTextSize(12);
        return button;
    }

    private Button baseButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setMinHeight(dp(42));
        button.setPadding(dp(10), dp(6), dp(10), dp(6));
        return button;
    }

    private GradientDrawable cardBackground(int color) {
        GradientDrawable drawable = rounded(color, dp(18));
        drawable.setStroke(dp(1), Color.rgb(226, 232, 240));
        return drawable;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
