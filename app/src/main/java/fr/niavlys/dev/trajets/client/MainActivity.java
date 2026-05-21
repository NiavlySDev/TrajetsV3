package fr.niavlys.dev.trajets.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import fr.niavlys.dev.trajets.config.AppConfig;
import fr.niavlys.dev.trajets.config.ConfigItem;
import fr.niavlys.dev.trajets.config.ConfigRepository;
import fr.niavlys.dev.trajets.config.ContactItem;
import fr.niavlys.dev.trajets.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SMS_PERMISSION = 1;

    private ConfigRepository repository;
    private AppConfig config;
    private TextView preview;
    private LinearLayout choicesContainer;
    private LinearLayout recipientsContainer;
    private final Set<String> selectedContactIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        repository = new ConfigRepository(this);
        checkAndRequestSmsPermission();
        buildContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (repository != null) {
            config = repository.load();
            renderRecipients();
            showEmptyChoices();
        }
    }

    private void buildContent() {
        config = repository.load();

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(245, 247, 251));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(24));
        scrollView.addView(root);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(header, matchWrap());

        TextView title = title("Trajets");
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        header.addView(title, titleParams);

        Button settings = secondaryButton("Paramètres");
        settings.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));
        header.addView(settings);

        preview = new TextView(this);
        preview.setText("");
        preview.setTextSize(20);
        preview.setTextColor(Color.rgb(21, 32, 43));
        preview.setMinHeight(dp(96));
        preview.setGravity(Gravity.CENTER_VERTICAL);
        preview.setPadding(dp(16), dp(14), dp(16), dp(14));
        preview.setBackground(cardBackground(Color.WHITE));
        LinearLayout.LayoutParams previewParams = matchWrap();
        previewParams.setMargins(0, dp(18), 0, dp(14));
        root.addView(preview, previewParams);

        root.addView(sectionTitle("Construire le message"));
        GridLayout actions = new GridLayout(this);
        actions.setColumnCount(2);
        root.addView(actions, matchWrap());
        addAction(actions, "Je suis à", "Je suis à ", () -> config.getStations());
        addAction(actions, "Je suis dans", "Je suis dans le ", () -> config.getTransports());
        addAction(actions, "Direction", "direction: ", () -> config.getStations());
        addAction(actions, "Je suis avec", "Je suis avec ", this::contactsAsChoices);
        addAction(actions, "Je suis arrivé chez", "Je suis arrivé chez ", this::contactsAsChoices);

        choicesContainer = new LinearLayout(this);
        choicesContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams choicesParams = matchWrap();
        choicesParams.setMargins(0, dp(14), 0, dp(14));
        root.addView(choicesContainer, choicesParams);

        root.addView(sectionTitle("Destinataires SMS"));
        recipientsContainer = new LinearLayout(this);
        recipientsContainer.setOrientation(LinearLayout.VERTICAL);
        recipientsContainer.setBackground(cardBackground(Color.WHITE));
        recipientsContainer.setPadding(dp(12), dp(8), dp(12), dp(8));
        root.addView(recipientsContainer, matchWrap());
        renderRecipients();

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams controlsParams = matchWrap();
        controlsParams.setMargins(0, dp(18), 0, 0);
        root.addView(controls, controlsParams);

        Button reset = secondaryButton("Reset");
        reset.setOnClickListener(view -> resetMessage());
        controls.addView(reset, weightedButtonParams());

        Button send = primaryButton("Envoyer");
        send.setOnClickListener(view -> sendMessage());
        controls.addView(send, weightedButtonParams());

        showEmptyChoices();
        setContentView(scrollView);
    }

    private void addAction(GridLayout actions, String label, String prefix, Supplier<List<ConfigItem>> choices) {
        Button button = secondaryButton(label);
        button.setOnClickListener(view -> {
            config = repository.load();
            appendText(prefix);
            renderChoices(choices.get());
        });
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        actions.addView(button, params);
    }

    private void renderChoices(List<ConfigItem> choices) {
        choicesContainer.removeAllViews();
        choicesContainer.addView(sectionTitle("Choix"));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        choicesContainer.addView(grid, matchWrap());

        for (ConfigItem item : choices) {
            Button button = chipButton(item.getLabel());
            button.setOnClickListener(view -> appendText(item.getPhrase()));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dp(4), dp(4), dp(4), dp(4));
            grid.addView(button, params);
        }
    }

    private void showEmptyChoices() {
        if (choicesContainer == null) {
            return;
        }
        choicesContainer.removeAllViews();
        TextView text = muted("Choisis une action pour afficher les gares, transports ou personnes.");
        text.setGravity(Gravity.CENTER);
        text.setPadding(dp(12), dp(18), dp(12), dp(18));
        text.setBackground(cardBackground(Color.WHITE));
        choicesContainer.addView(text, matchWrap());
    }

    private void renderRecipients() {
        if (recipientsContainer == null || config == null) {
            return;
        }
        recipientsContainer.removeAllViews();
        if (config.getContacts().isEmpty()) {
            recipientsContainer.addView(muted("Aucun numéro configuré."));
            return;
        }
        for (ContactItem contact : config.getContacts()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(contact.getName() + " · " + contact.getPhone());
            checkBox.setTextSize(16);
            checkBox.setTextColor(Color.rgb(21, 32, 43));
            checkBox.setChecked(selectedContactIds.contains(contact.getId()));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedContactIds.add(contact.getId());
                } else {
                    selectedContactIds.remove(contact.getId());
                }
            });
            recipientsContainer.addView(checkBox, matchWrap());
        }
    }

    private List<ConfigItem> contactsAsChoices() {
        java.util.ArrayList<ConfigItem> choices = new java.util.ArrayList<>();
        for (ContactItem contact : config.getContacts()) {
            choices.add(new ConfigItem(contact.getId(), contact.getName(), contact.getName()));
        }
        return choices;
    }

    private void appendText(String text) {
        String before = preview.getText().toString();
        String cleanText = text == null ? "" : text.trim();
        if (cleanText.isEmpty()) {
            return;
        }
        if (!before.isEmpty() && !before.endsWith(" ")) {
            before += " ";
        }
        preview.setText(before + cleanText + " ");
    }

    private void resetMessage() {
        preview.setText("");
        showEmptyChoices();
    }

    private void sendMessage() {
        String message = preview.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Le message est vide.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedContactIds.isEmpty()) {
            Toast.makeText(this, "Choisis au moins un destinataire.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestSmsPermission();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        for (ContactItem contact : config.getContacts()) {
            if (selectedContactIds.contains(contact.getId())) {
                smsManager.sendTextMessage(contact.getPhone(), null, message, null, null);
            }
        }
        Toast.makeText(this, "Message envoyé.", Toast.LENGTH_SHORT).show();
        resetMessage();
    }

    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }
    }

    private TextView title(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(30);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(Color.rgb(21, 32, 43));
        return view;
    }

    private TextView sectionTitle(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(15);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(Color.rgb(71, 85, 105));
        view.setPadding(0, dp(12), 0, dp(8));
        return view;
    }

    private TextView muted(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(15);
        view.setTextColor(Color.rgb(100, 116, 139));
        return view;
    }

    private Button primaryButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.WHITE);
        button.setBackground(rounded(Color.rgb(37, 99, 235), dp(14)));
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.rgb(21, 32, 43));
        button.setBackground(rounded(Color.WHITE, dp(14)));
        return button;
    }

    private Button chipButton(String text) {
        Button button = baseButton(text);
        button.setTextColor(Color.rgb(30, 64, 175));
        button.setBackground(rounded(Color.rgb(219, 234, 254), dp(14)));
        return button;
    }

    private Button baseButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setMinHeight(dp(48));
        button.setPadding(dp(12), dp(8), dp(12), dp(8));
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

    private LinearLayout.LayoutParams weightedButtonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(dp(4), 0, dp(4), 0);
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Deprecated
    public static void addPrevisualisation(String text) {
    }

    @Deprecated
    public static void resetPrevisualisation() {
    }

    @Deprecated
    public static Button getPrevious() {
        return null;
    }

    @Deprecated
    public static Button getNext() {
        return null;
    }
}
