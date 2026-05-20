package fr.niavlys.dev.trajets.client;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.Manifest;

import fr.niavlys.dev.trajets.R;
import fr.niavlys.dev.trajets.metier.BDD;
import fr.niavlys.dev.trajets.metier.Bouton;
import fr.niavlys.dev.trajets.metier.personnes.Numero;
import fr.niavlys.dev.trajets.metier.personnes.Personnes;
import fr.niavlys.dev.trajets.metier.switchs.Switchs;
import fr.niavlys.dev.trajets.metier.switchs.SwitchsListener;

public class MainActivity extends AppCompatActivity {

    private static MainActivity main;
    private static TextView previsualisation;
    private Button send;
    private Button reset;
    private static Button previous;
    private static Button next;
    public MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.main = new MainActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previsualisation = findViewById(R.id.textView);
        previsualisation.setText("");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        checkAndRequestSmsPermission();

        this.init();
    }

    public static void addPrevisualisation(String text) {
        String before = previsualisation.getText().toString();
        String toadd = before;
        if(!before.isEmpty() && !before.endsWith(" ") && !text.startsWith(" ")){
            toadd += " ";
        }
        toadd += text;
        previsualisation.setText(toadd);
    }

    public static void resetPrevisualisation() {
        previsualisation.setText("");
    }

    public void init(){
        this.initBoutons();
        BDD.hideAllBoutons();
        this.initSwitchs();
    }

    @SuppressLint("ResourceAsColor")
    public void initBoutons(){
        BDD.addBouton(1, new Bouton(1, findViewById(R.id.Bouton1)));
        BDD.addBouton(2, new Bouton(2, findViewById(R.id.Bouton2)));
        BDD.addBouton(3, new Bouton(3, findViewById(R.id.Bouton3)));
        BDD.addBouton(4, new Bouton(4, findViewById(R.id.Bouton4)));
        BDD.addBouton(5, new Bouton(5, findViewById(R.id.Bouton5)));
        BDD.addBouton(6, new Bouton(6, findViewById(R.id.Bouton6)));
        BDD.addBouton(7, new Bouton(7, findViewById(R.id.Bouton7)));
        BDD.addBouton(8, new Bouton(8, findViewById(R.id.Bouton8)));
        BDD.addBouton(9, new Bouton(9, findViewById(R.id.Bouton9)));
        BDD.addBouton(10, new Bouton(10, findViewById(R.id.Bouton10)));
        BDD.addBouton(11, new Bouton(11, findViewById(R.id.Bouton11)));
        BDD.addBouton(12, new Bouton(12, findViewById(R.id.Bouton12)));

        this.send = findViewById(R.id.Bouton_Envoyer);
        this.send.setText("Envoyer");
        this.send.setBackgroundColor(R.color.send);
        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                for(Numero numero : BDD.getNumeros()){
                    smsManager.sendTextMessage(numero.getNumero(), null, previsualisation.getText().toString(), null, null);
                }
                previsualisation.setText("");
                for(Switchs switchs : Switchs.values()){
                    switchs.getSwitch().setChecked(false);
                }
                for(Bouton bouton : BDD.getBoutons().values()){
                    bouton.get().setText("");
                }
            }
        });
        this.reset = findViewById(R.id.Bouton_Reset);
        this.reset.setText("Reset");
        this.reset.setBackgroundColor(R.color.reset);
        this.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previsualisation.setText("");
                for(Switchs switchs : Switchs.values()){
                    switchs.getSwitch().setChecked(false);
                }
            }
        });

        previous = findViewById(R.id.Bouton_Precedent);
        previous.setText("Précédent");
        previous.setBackgroundColor(R.color.precedent);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDD.getPages().afficherPageBoutons(BDD.getPages().getPrevious());
            }
        });

        next = findViewById(R.id.Bouton_Suivant);
        next.setText("Suivant");
        next.setBackgroundColor(R.color.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BDD.getPages().afficherPageBoutons(BDD.getPages().getNext());
            }
        });


    }

    public void initSwitchs(){
        Switchs.Je_Suis_A.setSwitch(findViewById(R.id.Switch_JeSuisA));
        Switchs.Je_Suis_Dans.setSwitch(findViewById(R.id.Switch_JeSuisDans));
        Switchs.Je_Suis_Avec.setSwitch(findViewById(R.id.Switch_JeSuisAvec));
        Switchs.Je_Suis_Arrive.setSwitch(findViewById(R.id.Switch_JeSuisArrive));
        Switchs.Direction.setSwitch(findViewById(R.id.Switch_Direction));
        Switchs.Maman.setSwitch(findViewById(R.id.Switch_Maman));
        Switchs.Papa.setSwitch(findViewById(R.id.Switch_Papa));
        Switchs.Patou.setSwitch(findViewById(R.id.Switch_Patou));
        Personnes.initSwitchsPersonnes();

        SwitchsListener.initJeSuisAEtDirection();
        SwitchsListener.initJeSuisDans();
        SwitchsListener.initJeSuisAvecEtArrive();
    }

    public static Button getPrevious() {
        return previous;
    }

    public static Button getNext() {
        return next;
    }

    private static final int REQUEST_SMS_PERMISSION = 1;

    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission non accordée, on la demande
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION
            );
        } else {
            // Permission déjà accordée, tu peux envoyer des SMS
        }
    }
}
