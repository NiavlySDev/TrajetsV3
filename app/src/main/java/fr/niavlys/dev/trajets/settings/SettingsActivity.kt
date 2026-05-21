package fr.niavlys.dev.trajets.settings

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.niavlys.dev.trajets.config.AppConfig
import fr.niavlys.dev.trajets.config.ConfigItem
import fr.niavlys.dev.trajets.config.ConfigRepository
import fr.niavlys.dev.trajets.config.ContactItem

class SettingsActivity : AppCompatActivity() {
    private lateinit var repository: ConfigRepository
    private lateinit var config: AppConfig
    private lateinit var content: LinearLayout

    private val appBackground = Color.rgb(246, 248, 252)
    private val surface = Color.WHITE
    private val textPrimary = Color.rgb(15, 23, 42)
    private val textSecondary = Color.rgb(100, 116, 139)
    private val border = Color.rgb(226, 232, 240)
    private val primary = Color.rgb(37, 99, 235)
    private val danger = Color.rgb(220, 38, 38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ConfigRepository(this)
        config = repository.load()
        buildScreen()
    }

    private fun buildScreen() {
        val scrollView = ScrollView(this).apply {
            setFillViewport(true)
            setBackgroundColor(appBackground)
        }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(24))
        }
        scrollView.addView(content, matchWrap())
        setContentView(scrollView)
        render()
    }

    private fun render() {
        content.removeAllViews()
        content.addView(header(), matchWrap())
        content.addView(section("Modes de transport", config.transports, "Nom affiché", "Texte ajouté au message"))
        content.addView(section("Gares", config.stations, "Nom affiché", "Texte ajouté au message"))
        content.addView(contactSection())
        content.addView(dangerButton("Réinitialiser les données").apply {
            setOnClickListener { confirmReset() }
        }, marginLayout(top = 18))
    }

    private fun header(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val titles = LinearLayout(this@SettingsActivity).apply {
            orientation = LinearLayout.VERTICAL
            addView(text("Paramètres", 28f, textPrimary, Typeface.BOLD))
            addView(text("Gares, transports et numéros", 14f, textSecondary))
        }
        addView(titles, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        addView(secondaryButton("Retour").apply { setOnClickListener { finish() } })
    }

    private fun section(
        title: String,
        items: MutableList<ConfigItem>,
        labelHint: String,
        phraseHint: String
    ): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(sectionTitle(title))

        val card = card()
        if (items.isEmpty()) {
            card.addView(muted("Aucun élément configuré."))
        }
        items.forEach { item ->
            card.addView(configRow(item, onEdit = {
                showConfigDialog(title, labelHint, phraseHint, items, item)
            }, onDelete = {
                items.remove(item)
                saveAndRender()
            }))
        }
        card.addView(primaryButton("Ajouter").apply {
            setOnClickListener { showConfigDialog(title, labelHint, phraseHint, items, null) }
        }, marginLayout(top = 8))
        addView(card, matchWrap())
    }

    private fun configRow(item: ConfigItem, onEdit: () -> Unit, onDelete: () -> Unit): LinearLayout =
        itemRow(item.label, item.phrase, onEdit, onDelete)

    private fun contactSection(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(sectionTitle("Numéros de téléphone"))

        val card = card()
        if (config.contacts.isEmpty()) {
            card.addView(muted("Aucun numéro configuré."))
        }
        config.contacts.forEach { contact ->
            card.addView(itemRow(contact.name, contact.phone, onEdit = {
                showContactDialog(contact)
            }, onDelete = {
                config.contacts.remove(contact)
                saveAndRender()
            }))
        }
        card.addView(primaryButton("Ajouter").apply {
            setOnClickListener { showContactDialog(null) }
        }, marginLayout(top = 8))
        addView(card, matchWrap())
    }

    private fun itemRow(title: String, subtitle: String, onEdit: () -> Unit, onDelete: () -> Unit): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(10))

            addView(text(title, 16f, textPrimary, Typeface.BOLD))
            addView(text(subtitle, 14f, textSecondary))

            val actions = LinearLayout(this@SettingsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
                addView(secondaryButton("Modifier").apply { setOnClickListener { onEdit() } })
                addView(dangerButton("Supprimer").apply { setOnClickListener { onDelete() } })
            }
            addView(actions, marginLayout(top = 6))
        }

    private fun showConfigDialog(
        title: String,
        labelHint: String,
        phraseHint: String,
        items: MutableList<ConfigItem>,
        item: ConfigItem?
    ) {
        val label = input(labelHint).apply { setText(item?.label.orEmpty()) }
        val phrase = input(phraseHint).apply { setText(item?.phrase.orEmpty()) }
        showFormDialog(
            title = if (item == null) "Ajouter - $title" else "Modifier - $title",
            fields = listOf(label, phrase),
            onSave = {
                val labelValue = label.text.toString().trim()
                val phraseValue = phrase.text.toString().trim()
                if (labelValue.isEmpty() || phraseValue.isEmpty()) return@showFormDialog false

                if (item == null) {
                    items += ConfigItem(repository.newId(), labelValue, phraseValue)
                } else {
                    item.label = labelValue
                    item.phrase = phraseValue
                }
                saveAndRender()
                true
            }
        )
    }

    private fun showContactDialog(contact: ContactItem?) {
        val name = input("Nom").apply { setText(contact?.name.orEmpty()) }
        val phone = input("Numéro").apply {
            inputType = InputType.TYPE_CLASS_PHONE
            setText(contact?.phone.orEmpty())
        }
        showFormDialog(
            title = if (contact == null) "Ajouter un numéro" else "Modifier un numéro",
            fields = listOf(name, phone),
            onSave = {
                val nameValue = name.text.toString().trim()
                val phoneValue = phone.text.toString().trim()
                if (nameValue.isEmpty() || phoneValue.isEmpty()) return@showFormDialog false

                if (contact == null) {
                    config.contacts += ContactItem(repository.newId(), nameValue, phoneValue)
                } else {
                    contact.name = nameValue
                    contact.phone = phoneValue
                }
                saveAndRender()
                true
            }
        )
    }

    private fun showFormDialog(title: String, fields: List<EditText>, onSave: () -> Boolean) {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), 0)
            fields.forEach { addView(it, matchWrap()) }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(form)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Enregistrer", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (onSave()) dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun confirmReset() {
        AlertDialog.Builder(this)
            .setTitle("Réinitialiser")
            .setMessage("Remettre les gares, transports et numéros par défaut ?")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Réinitialiser") { _, _ ->
                repository.resetToDefaults()
                config = repository.load()
                render()
            }
            .show()
    }

    private fun saveAndRender() {
        repository.save(config)
        render()
    }

    private fun input(hint: String): EditText = EditText(this).apply {
        this.hint = hint
        isSingleLine = true
        textSize = 16f
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(14), dp(12), dp(14), dp(12))
        background = cardBackground(surface)
    }

    private fun sectionTitle(label: String): TextView = text(label, 15f, Color.rgb(71, 85, 105), Typeface.BOLD).apply {
        setPadding(0, dp(18), 0, dp(8))
    }

    private fun muted(label: String): TextView = text(label, 15f, textSecondary).apply {
        setPadding(0, dp(8), 0, dp(8))
    }

    private fun primaryButton(label: String): Button = baseButton(label).apply {
        setTextColor(Color.WHITE)
        background = rounded(primary, 12)
    }

    private fun secondaryButton(label: String): Button = baseButton(label).apply {
        setTextColor(textPrimary)
        background = outlined(surface, 12)
    }

    private fun dangerButton(label: String): Button = baseButton(label).apply {
        setTextColor(Color.WHITE)
        background = rounded(danger, 12)
    }

    private fun baseButton(label: String): Button = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 14f
        minHeight = dp(42)
        setPadding(dp(10), dp(6), dp(10), dp(6))
    }

    private fun text(label: String, size: Float, color: Int, style: Int = Typeface.NORMAL): TextView =
        TextView(this).apply {
            text = label
            textSize = size
            setTextColor(color)
            typeface = Typeface.create(Typeface.DEFAULT, style)
        }

    private fun matchWrap(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    private fun marginLayout(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        matchWrap().apply { setMargins(0, dp(top), 0, dp(bottom)) }

    private fun cardBackground(color: Int): GradientDrawable = outlined(color, 18)

    private fun outlined(color: Int, radius: Int): GradientDrawable =
        rounded(color, radius).apply { setStroke(dp(1), border) }

    private fun rounded(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
