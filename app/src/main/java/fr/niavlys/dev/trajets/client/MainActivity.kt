package fr.niavlys.dev.trajets.client

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.niavlys.dev.trajets.R
import fr.niavlys.dev.trajets.config.AppConfig
import fr.niavlys.dev.trajets.config.ConfigItem
import fr.niavlys.dev.trajets.config.ConfigRepository
import fr.niavlys.dev.trajets.config.ContactItem
import fr.niavlys.dev.trajets.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var repository: ConfigRepository
    private lateinit var config: AppConfig
    private lateinit var preview: TextView
    private lateinit var choicesContainer: LinearLayout
    private lateinit var recipientsContainer: LinearLayout

    private val selectedContactIds = mutableSetOf<String>()
    private val appBackground = Color.rgb(246, 248, 252)
    private val surface = Color.WHITE
    private val textPrimary = Color.rgb(15, 23, 42)
    private val textSecondary = Color.rgb(100, 116, 139)
    private val border = Color.rgb(226, 232, 240)
    private val primary = Color.rgb(37, 99, 235)
    private val primarySoft = Color.rgb(219, 234, 254)
    private val success = Color.rgb(16, 185, 129)
    private val danger = Color.rgb(220, 38, 38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        repository = ConfigRepository(this)
        checkAndRequestSmsPermission()
        buildScreen()
    }

    override fun onResume() {
        super.onResume()
        if (::repository.isInitialized && ::recipientsContainer.isInitialized) {
            config = repository.load()
            selectedContactIds.retainAll(config.contacts.map { it.id }.toSet())
            renderRecipients()
            showEmptyChoices()
        }
    }

    private fun buildScreen() {
        config = repository.load()

        val scrollView = ScrollView(this).apply {
            setFillViewport(true)
            setBackgroundColor(appBackground)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(24))
        }
        scrollView.addView(root, matchWrap())

        root.addView(header(), matchWrap())
        root.addView(previewCard(), marginLayout(top = 18, bottom = 18))
        root.addView(sectionTitle("Construire le message"))
        root.addView(actionGrid(), matchWrap())

        choicesContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(choicesContainer, marginLayout(top = 14, bottom = 18))

        root.addView(sectionTitle("Destinataires SMS"))
        recipientsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = cardBackground(surface)
        }
        root.addView(recipientsContainer, matchWrap())
        renderRecipients()

        root.addView(bottomControls(), marginLayout(top = 18))
        showEmptyChoices()
        setContentView(scrollView)
    }

    private fun header(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val logo = ImageView(this@MainActivity).apply {
            setImageResource(R.drawable.ic_launcher_foreground)
            background = rounded(primarySoft, 18)
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        addView(logo, LinearLayout.LayoutParams(dp(52), dp(52)))

        val titleBlock = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
            addView(text("Trajets", 30f, textPrimary, Typeface.BOLD))
            addView(text("Messages rapides et configurables", 14f, textSecondary))
        }
        addView(titleBlock, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        addView(secondaryButton("Paramètres").apply {
            setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
        })
    }

    private fun previewCard(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(14), dp(16), dp(14))
        background = cardBackground(surface)

        addView(text("Message", 13f, textSecondary, Typeface.BOLD))
        preview = text("", 20f, textPrimary).apply {
            minHeight = dp(82)
            gravity = Gravity.CENTER_VERTICAL
            text = "Ton message apparaîtra ici"
            setTextColor(textSecondary)
        }
        addView(preview, matchWrap())
    }

    private fun actionGrid(): GridLayout = GridLayout(this).apply {
        columnCount = 2
        addAction("Je suis à", "Je suis à", { config.stations })
        addAction("Je suis dans", "Je suis dans le", { config.transports })
        addAction("Direction", "direction:", { config.stations })
        addAction("Je suis avec", "Je suis avec", ::contactsAsChoices)
        addAction("Je suis arrivé chez", "Je suis arrivé chez", ::contactsAsChoices)
    }

    private fun GridLayout.addAction(label: String, prefix: String, choices: () -> List<ConfigItem>) {
        val button = secondaryButton(label).apply {
            setOnClickListener {
                config = repository.load()
                appendSegment(prefix)
                renderChoices(choices())
            }
        }
        addView(button, gridParams())
    }

    private fun renderChoices(choices: List<ConfigItem>) {
        choicesContainer.removeAllViews()
        choicesContainer.addView(sectionTitle("Choix"))

        if (choices.isEmpty()) {
            choicesContainer.addView(emptyCard("Aucun élément configuré. Ajoute-le depuis Paramètres."))
            return
        }

        val grid = GridLayout(this).apply { columnCount = 2 }
        choicesContainer.addView(grid, matchWrap())
        choices.forEach { item ->
            val chip = chipButton(item.label).apply {
                setOnClickListener { appendSegment(item.phrase) }
            }
            grid.addView(chip, gridParams())
        }
    }

    private fun showEmptyChoices() {
        choicesContainer.removeAllViews()
        choicesContainer.addView(emptyCard("Choisis une action pour afficher les gares, transports ou personnes."))
    }

    private fun renderRecipients() {
        recipientsContainer.removeAllViews()
        if (config.contacts.isEmpty()) {
            recipientsContainer.addView(muted("Aucun numéro configuré."))
            return
        }

        config.contacts.forEach { contact ->
            val checkBox = CheckBox(this).apply {
                text = "${contact.name}  ·  ${contact.phone}"
                textSize = 16f
                setTextColor(textPrimary)
                isChecked = selectedContactIds.contains(contact.id)
                setOnCheckedChangeListener { _, checked ->
                    if (checked) selectedContactIds += contact.id else selectedContactIds -= contact.id
                }
            }
            recipientsContainer.addView(checkBox, matchWrap())
        }
    }

    private fun bottomControls(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER

        addView(secondaryButton("Effacer").apply { setOnClickListener { resetMessage() } }, weightedButtonParams())
        addView(primaryButton("Envoyer").apply { setOnClickListener { sendMessage() } }, weightedButtonParams())
    }

    private fun contactsAsChoices(): List<ConfigItem> =
        config.contacts.map { ConfigItem(it.id, it.name, it.name) }

    private fun appendSegment(segment: String) {
        val value = segment.trim()
        if (value.isEmpty()) return

        val current = preview.text.toString()
        val base = if (current == "Ton message apparaîtra ici") "" else current
        val separator = if (base.isNotBlank() && !base.endsWith(" ")) " " else ""
        preview.setTextColor(textPrimary)
        preview.text = "$base$separator$value "
    }

    private fun resetMessage() {
        preview.setTextColor(textSecondary)
        preview.text = "Ton message apparaîtra ici"
        showEmptyChoices()
    }

    private fun sendMessage() {
        val message = preview.text.toString().trim()
        if (message.isEmpty() || message == "Ton message apparaîtra ici") {
            toast("Le message est vide.")
            return
        }
        if (selectedContactIds.isEmpty()) {
            toast("Choisis au moins un destinataire.")
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestSmsPermission()
            toast("Autorise l'envoi de SMS pour continuer.")
            return
        }

        val smsManager = SmsManager.getDefault()
        config.contacts
            .filter { selectedContactIds.contains(it.id) }
            .forEach { smsManager.sendTextMessage(it.phone, null, message, null, null) }

        toast("Message envoyé.")
        resetMessage()
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun primaryButton(label: String): Button = baseButton(label).apply {
        setTextColor(Color.WHITE)
        background = rounded(primary, 14)
    }

    private fun secondaryButton(label: String): Button = baseButton(label).apply {
        setTextColor(textPrimary)
        background = outlined(surface, 14)
    }

    private fun chipButton(label: String): Button = baseButton(label).apply {
        setTextColor(Color.rgb(30, 64, 175))
        background = rounded(primarySoft, 14)
    }

    private fun baseButton(label: String): Button = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 15f
        minHeight = dp(48)
        setPadding(dp(12), dp(8), dp(12), dp(8))
    }

    private fun sectionTitle(label: String): TextView = text(label, 15f, Color.rgb(71, 85, 105), Typeface.BOLD).apply {
        setPadding(0, dp(8), 0, dp(8))
    }

    private fun muted(label: String): TextView = text(label, 15f, textSecondary)

    private fun emptyCard(label: String): TextView = muted(label).apply {
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(18), dp(12), dp(18))
        background = cardBackground(surface)
    }

    private fun text(label: String, size: Float, color: Int, style: Int = Typeface.NORMAL): TextView =
        TextView(this).apply {
            text = label
            textSize = size
            setTextColor(color)
            typeface = Typeface.create(Typeface.DEFAULT, style)
        }

    private fun gridParams(): GridLayout.LayoutParams = GridLayout.LayoutParams().apply {
        width = 0
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        setMargins(dp(4), dp(4), dp(4), dp(4))
    }

    private fun matchWrap(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    private fun marginLayout(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        matchWrap().apply { setMargins(0, dp(top), 0, dp(bottom)) }

    private fun weightedButtonParams(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            setMargins(dp(4), 0, dp(4), 0)
        }

    private fun cardBackground(color: Int): GradientDrawable = outlined(color, 18)

    private fun outlined(color: Int, radius: Int): GradientDrawable =
        rounded(color, radius).apply { setStroke(dp(1), border) }

    private fun rounded(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val REQUEST_SMS_PERMISSION = 1
    }
}
