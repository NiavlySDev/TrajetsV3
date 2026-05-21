package fr.niavlys.dev.trajets.settings

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.niavlys.dev.trajets.config.AppConfig
import fr.niavlys.dev.trajets.config.BlockType
import fr.niavlys.dev.trajets.config.CategoryItem
import fr.niavlys.dev.trajets.config.ConfigItem
import fr.niavlys.dev.trajets.config.ConfigRepository
import fr.niavlys.dev.trajets.config.ContactGroupItem
import fr.niavlys.dev.trajets.config.ContactItem
import fr.niavlys.dev.trajets.config.MessageBlock
import fr.niavlys.dev.trajets.config.PresetItem

class SettingsActivity : AppCompatActivity() {
    private lateinit var repository: ConfigRepository
    private lateinit var config: AppConfig
    private lateinit var content: LinearLayout
    private var activeTab = SettingsTab.TRANSPORTS

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

    @Deprecated("Kept for the simple Android contact picker result API.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_CONTACT && resultCode == RESULT_OK) {
            importContact(data?.data)
        }
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
        content.addView(tabs(), marginLayout(top = 18, bottom = 8))
        when (activeTab) {
            SettingsTab.CATEGORIES -> content.addView(categorySection())
            SettingsTab.TRANSPORTS -> content.addView(section("Modes de transport", config.transports, "Nom affiché", "Texte ajouté au message"))
            SettingsTab.STATIONS -> content.addView(section("Gares", config.stations, "Nom affiché", "Texte ajouté au message"))
            SettingsTab.CONTACTS -> content.addView(contactSection())
            SettingsTab.GROUPS -> content.addView(groupSection())
            SettingsTab.PRESETS -> content.addView(presetSection())
        }
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

    private fun tabs(): GridLayout = GridLayout(this).apply {
        columnCount = 3
        setPadding(dp(4), dp(4), dp(4), dp(4))
        background = outlined(Color.rgb(241, 245, 249), 16)

        SettingsTab.values().forEach { tab ->
            addView(tabButton(tab), GridLayout.LayoutParams().apply {
                width = 0
                height = LinearLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(3), 0, dp(3), 0)
            })
        }
    }

    private fun tabButton(tab: SettingsTab): Button = baseButton(tab.label).apply {
        minHeight = dp(44)
        textSize = 13f
        setTextColor(if (activeTab == tab) Color.WHITE else textSecondary)
        background = if (activeTab == tab) rounded(primary, 12) else rounded(Color.TRANSPARENT, 12)
        setOnClickListener {
            activeTab = tab
            render()
        }
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
        items.sortedItems().forEach { item ->
            card.addView(configRow(item, onEdit = {
                showConfigDialog(title, labelHint, phraseHint, items, item, allowCategory = title == "Modes de transport")
            }, onDelete = {
                items.remove(item)
                saveAndRender()
            }))
        }
        card.addView(primaryButton("Ajouter").apply {
            setOnClickListener { showConfigDialog(title, labelHint, phraseHint, items, null, allowCategory = title == "Modes de transport") }
        }, marginLayout(top = 8))
        addView(card, matchWrap())
    }

    private fun configRow(item: ConfigItem, onEdit: () -> Unit, onDelete: () -> Unit): LinearLayout =
        itemRow(item.label, itemSubtitle(item), onEdit, onDelete)

    private fun categorySection(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(sectionTitle("Catégories de transport"))

        val card = card()
        if (config.transportCategories.isEmpty()) {
            card.addView(muted("Aucune catégorie configurée."))
        }
        config.transportCategories.sortedBy { it.order }.forEach { category ->
            card.addView(itemRow(category.label, "Ordre ${category.order}", onEdit = {
                showCategoryDialog(category)
            }, onDelete = {
                config.transports.filter { it.categoryId == category.id }.forEach { it.categoryId = null }
                config.transportCategories.remove(category)
                saveAndRender()
            }))
        }
        card.addView(primaryButton("Ajouter").apply {
            setOnClickListener { showCategoryDialog(null) }
        }, marginLayout(top = 8))
        addView(card, matchWrap())
    }

    private fun contactSection(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(sectionTitle("Numéros de téléphone"))

        val card = card()
        if (config.contacts.isEmpty()) {
            card.addView(muted("Aucun numéro configuré."))
        }
        config.contacts.sortedBy { it.name.lowercase() }.forEach { contact ->
            card.addView(itemRow(contact.name, contact.phone, onEdit = {
                showContactDialog(contact)
            }, onDelete = {
                config.contactGroups.forEach { it.contactIds.remove(contact.id) }
                config.contacts.remove(contact)
                saveAndRender()
            }))
        }
        card.addView(primaryButton("Ajouter").apply {
            setOnClickListener { showContactDialog(null) }
        }, marginLayout(top = 8))
        card.addView(secondaryButton("Importer depuis le téléphone").apply {
            setOnClickListener { pickPhoneContact() }
        }, marginLayout(top = 8))
        addView(card, matchWrap())
    }

    private fun groupSection(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(sectionTitle("Groupes de personnes"))

        val card = card()
        if (config.contactGroups.isEmpty()) {
            card.addView(muted("Aucun groupe configuré."))
        }
        config.contactGroups.sortedBy { it.order }.forEach { group ->
            card.addView(itemRow(group.label, groupSubtitle(group), onEdit = {
                showGroupDialog(group)
            }, onDelete = {
                config.contactGroups.remove(group)
                saveAndRender()
            }))
        }
        card.addView(primaryButton("Ajouter").apply {
            setOnClickListener { showGroupDialog(null) }
        }, marginLayout(top = 8))
        addView(card, matchWrap())
    }

    private fun presetSection(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(presetList("Messages prédéfinis", config.messagePresets))
    }

    private fun presetList(title: String, items: MutableList<PresetItem>): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(sectionTitle(title))
            val card = card()
            if (items.isEmpty()) {
                card.addView(muted("Aucun élément prédéfini."))
            }
            items.sortedBy { it.order }.forEach { preset ->
                card.addView(itemRow(preset.label, "${renderPreset(preset)}\nOrdre ${preset.order}", onEdit = {
                    showPresetDialog(title, items, preset)
                }, onDelete = {
                    items.remove(preset)
                    saveAndRender()
                }))
            }
            card.addView(primaryButton("Ajouter").apply {
                setOnClickListener { showPresetDialog(title, items, null) }
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
        item: ConfigItem?,
        allowCategory: Boolean
    ) {
        val label = input(labelHint).apply { setText(item?.label.orEmpty()) }
        val phrase = input(phraseHint).apply { setText(item?.phrase.orEmpty()) }
        val order = input("Ordre d'affichage").apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText((item?.order ?: nextOrder(items)).toString())
        }
        var selectedCategoryId = item?.categoryId
        val categoryLabel = text(categoryText(selectedCategoryId), 15f, textSecondary)
        val categoryButton = secondaryButton("Choisir la catégorie").apply {
            setOnClickListener {
                chooseCategory(selectedCategoryId) {
                    selectedCategoryId = it
                    categoryLabel.text = categoryText(selectedCategoryId)
                }
            }
        }
        val fields = mutableListOf(label, phrase, order)
        showFormDialog(
            title = if (item == null) "Ajouter - $title" else "Modifier - $title",
            fields = fields,
            extraViews = if (allowCategory) listOf(categoryLabel, categoryButton) else emptyList(),
            onSave = {
                val labelValue = label.text.toString().trim()
                val phraseValue = phrase.text.toString().trim()
                val orderValue = order.text.toString().trim().toIntOrNull() ?: 0
                if (labelValue.isEmpty() || phraseValue.isEmpty()) return@showFormDialog false

                if (item == null) {
                    items += ConfigItem(repository.newId(), labelValue, phraseValue, orderValue, selectedCategoryId)
                } else {
                    item.label = labelValue
                    item.phrase = phraseValue
                    item.order = orderValue
                    item.categoryId = selectedCategoryId
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

    private fun pickPhoneContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_CONTACT)
    }

    private fun importContact(uri: Uri?) {
        if (uri == null) return

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val name = cursor.getString(nameIndex).orEmpty().trim()
            val phone = cursor.getString(phoneIndex).orEmpty().trim()
            if (name.isBlank() || phone.isBlank()) return

            config.contacts += ContactItem(repository.newId(), name, phone)
            repository.save(config)
            activeTab = SettingsTab.CONTACTS
            render()
        }
    }

    private fun showCategoryDialog(category: CategoryItem?) {
        val label = input("Nom de la catégorie").apply { setText(category?.label.orEmpty()) }
        val order = input("Ordre d'affichage").apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText((category?.order ?: ((config.transportCategories.size + 1) * 10)).toString())
        }
        showFormDialog(
            title = if (category == null) "Ajouter une catégorie" else "Modifier une catégorie",
            fields = listOf(label, order),
            onSave = {
                val labelValue = label.text.toString().trim()
                val orderValue = order.text.toString().trim().toIntOrNull() ?: 0
                if (labelValue.isEmpty()) return@showFormDialog false
                if (category == null) {
                    config.transportCategories += CategoryItem(repository.newId(), labelValue, orderValue)
                } else {
                    category.label = labelValue
                    category.order = orderValue
                }
                saveAndRender()
                true
            }
        )
    }

    private fun showGroupDialog(group: ContactGroupItem?) {
        val label = input("Nom du groupe").apply { setText(group?.label.orEmpty()) }
        val order = input("Ordre d'affichage").apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText((group?.order ?: ((config.contactGroups.size + 1) * 10)).toString())
        }
        val selectedIds = group?.contactIds?.toMutableSet() ?: mutableSetOf()
        val checks = config.contacts.sortedBy { it.name.lowercase() }.map { contact ->
            CheckBox(this).apply {
                text = contact.name
                isChecked = selectedIds.contains(contact.id)
                setOnCheckedChangeListener { _, checked ->
                    if (checked) selectedIds += contact.id else selectedIds -= contact.id
                }
            }
        }
        showFormDialog(
            title = if (group == null) "Ajouter un groupe" else "Modifier un groupe",
            fields = listOf(label, order),
            extraViews = checks,
            onSave = {
                val labelValue = label.text.toString().trim()
                val orderValue = order.text.toString().trim().toIntOrNull() ?: 0
                if (labelValue.isEmpty()) return@showFormDialog false
                if (group == null) {
                    config.contactGroups += ContactGroupItem(repository.newId(), labelValue, selectedIds.toMutableList(), orderValue)
                } else {
                    group.label = labelValue
                    group.order = orderValue
                    group.contactIds = selectedIds.toMutableList()
                }
                saveAndRender()
                true
            }
        )
    }

    private fun showPresetDialog(title: String, items: MutableList<PresetItem>, preset: PresetItem?) {
        val label = input("Nom affiché").apply { setText(preset?.label.orEmpty()) }
        val order = input("Ordre d'affichage").apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText((preset?.order ?: ((items.size + 1) * 10)).toString())
        }
        val blocks = preset?.blocks?.toMutableList() ?: mutableListOf()
        val preview = text(renderBlocks(blocks).ifBlank { "Aucun bloc" }, 15f, textSecondary).apply {
            setPadding(0, dp(10), 0, dp(10))
        }
        fun refreshPreview() {
            preview.text = renderBlocks(blocks).ifBlank { "Aucun bloc" }
        }
        val blockActions = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(secondaryButton("Ajouter une action").apply {
                setOnClickListener { chooseActionBlock { blocks += it; refreshPreview() } }
            }, marginLayout(top = 4))
            addView(secondaryButton("Ajouter un transport").apply {
                setOnClickListener { chooseItemBlock("Transport", config.transports.sortedItems(), BlockType.TRANSPORT) { blocks += it; refreshPreview() } }
            }, marginLayout(top = 4))
            addView(secondaryButton("Ajouter une gare").apply {
                setOnClickListener { chooseItemBlock("Gare", config.stations.sortedItems(), BlockType.STATION) { blocks += it; refreshPreview() } }
            }, marginLayout(top = 4))
            addView(secondaryButton("Ajouter une personne").apply {
                setOnClickListener { chooseContactBlock { blocks += it; refreshPreview() } }
            }, marginLayout(top = 4))
            addView(secondaryButton("Ajouter un retard").apply {
                setOnClickListener { chooseDelayBlock { blocks += it; refreshPreview() } }
            }, marginLayout(top = 4))
            addView(dangerButton("Retirer le dernier bloc").apply {
                setOnClickListener {
                    if (blocks.isNotEmpty()) blocks.removeAt(blocks.lastIndex)
                    refreshPreview()
                }
            }, marginLayout(top = 4))
        }
        showFormDialog(
            title = if (preset == null) "Ajouter - $title" else "Modifier - $title",
            fields = listOf(label, order),
            extraViews = listOf(preview, blockActions),
            onSave = {
                val labelValue = label.text.toString().trim()
                val orderValue = order.text.toString().trim().toIntOrNull() ?: 0
                if (labelValue.isEmpty() || blocks.isEmpty()) return@showFormDialog false
                if (preset == null) {
                    items += PresetItem(repository.newId(), labelValue, blocks, orderValue)
                } else {
                    preset.label = labelValue
                    preset.blocks = blocks
                    preset.order = orderValue
                }
                saveAndRender()
                true
            }
        )
    }

    private fun chooseActionBlock(onSelected: (MessageBlock) -> Unit) {
        val actions = listOf(
            "Je suis à",
            "Je suis dans le",
            "direction:",
            "Je suis avec",
            "Je suis arrivé chez",
            "Je pars maintenant",
            "Je suis arrivé"
        )
        AlertDialog.Builder(this)
            .setTitle("Action")
            .setItems(actions.toTypedArray()) { dialog, which ->
                onSelected(MessageBlock(BlockType.ACTION, text = actions[which]))
                dialog.dismiss()
            }
            .show()
    }

    private fun chooseItemBlock(title: String, items: List<ConfigItem>, type: BlockType, onSelected: (MessageBlock) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(items.map { it.label }.toTypedArray()) { dialog, which ->
                onSelected(MessageBlock(type, itemId = items[which].id))
                dialog.dismiss()
            }
            .show()
    }

    private fun chooseContactBlock(onSelected: (MessageBlock) -> Unit) {
        val contacts = config.contacts.sortedBy { it.name.lowercase() }
        AlertDialog.Builder(this)
            .setTitle("Personne")
            .setItems(contacts.map { it.name }.toTypedArray()) { dialog, which ->
                onSelected(MessageBlock(BlockType.CONTACT, itemId = contacts[which].id))
                dialog.dismiss()
            }
            .show()
    }

    private fun chooseDelayBlock(onSelected: (MessageBlock) -> Unit) {
        val minutes = input("Minutes de retard").apply {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val cause = input("Cause optionnelle").apply {
            hint = "Ex: problème technique"
        }
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), 0)
            addView(minutes, matchWrap())
            addView(cause, matchWrap())
        }
        AlertDialog.Builder(this)
            .setTitle("Retard")
            .setView(form)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Transport") { _, _ ->
                val minuteValue = minutes.text.toString().toIntOrNull() ?: 0
                val causeValue = cause.text.toString().trim()
                val transports = config.transports.sortedItems()
                val labels = transports.map { it.label }.toTypedArray()
                AlertDialog.Builder(this)
                    .setTitle("Transport concerné")
                    .setItems(labels) { dialog, which ->
                        onSelected(MessageBlock(BlockType.DELAY, itemId = transports[which].id, text = causeValue, minutes = minuteValue))
                        dialog.dismiss()
                    }
                    .show()
            }
            .show()
    }

    private fun showFormDialog(
        title: String,
        fields: List<EditText>,
        extraViews: List<android.view.View> = emptyList(),
        onSave: () -> Boolean
    ) {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), 0)
            fields.forEach { addView(it, matchWrap()) }
            extraViews.forEach { addView(it, matchWrap()) }
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

    private fun chooseCategory(currentId: String?, onSelected: (String?) -> Unit) {
        val categories = config.transportCategories.sortedBy { it.order }
        val labels = listOf("Aucune") + categories.map { it.label }
        val checked = categories.indexOfFirst { it.id == currentId } + 1
        AlertDialog.Builder(this)
            .setTitle("Catégorie")
            .setSingleChoiceItems(labels.toTypedArray(), checked) { dialog, which ->
                onSelected(if (which == 0) null else categories[which - 1].id)
                dialog.dismiss()
            }
            .show()
    }

    private fun categoryText(categoryId: String?): String =
        "Catégorie : " + (config.transportCategories.firstOrNull { it.id == categoryId }?.label ?: "Aucune")

    private fun itemSubtitle(item: ConfigItem): String {
        val category = config.transportCategories.firstOrNull { it.id == item.categoryId }?.label
        return listOfNotNull(item.phrase, category?.let { "Catégorie $it" }, "Ordre ${item.order}").joinToString("\n")
    }

    private fun groupSubtitle(group: ContactGroupItem): String {
        val names = config.contacts.filter { group.contactIds.contains(it.id) }.joinToString(", ") { it.name }
        return (names.ifBlank { "Aucune personne" }) + "\nOrdre ${group.order}"
    }

    private fun renderPreset(preset: PresetItem): String = renderBlocks(preset.blocks)

    private fun renderBlocks(blocks: List<MessageBlock>): String =
        blocks.joinToString(" ") { renderBlock(it) }.trim()

    private fun renderBlock(block: MessageBlock): String = when (block.type) {
        BlockType.TEXT, BlockType.ACTION -> block.text
        BlockType.TRANSPORT -> config.transports.firstOrNull { it.id == block.itemId }?.phrase.orEmpty()
        BlockType.STATION -> config.stations.firstOrNull { it.id == block.itemId }?.phrase.orEmpty()
        BlockType.CONTACT -> config.contacts.firstOrNull { it.id == block.itemId }?.name.orEmpty()
        BlockType.DELAY -> {
            val transport = config.transports.firstOrNull { it.id == block.itemId }?.phrase
            delayMessage(transport.orEmpty(), block.minutes, block.text)
        }
    }

    private fun delayMessage(transport: String, minutes: Int, cause: String): String {
        val base = "Le transport ${transport.trim()} a un retard de $minutes minutes"
        val cleanCause = cause.trim()
        return if (cleanCause.isBlank()) base else "$base à cause de $cleanCause"
    }

    private fun List<ConfigItem>.sortedItems(): List<ConfigItem> =
        sortedWith(compareBy<ConfigItem> { it.order }.thenBy { it.label.lowercase() })

    private fun nextOrder(items: List<ConfigItem>): Int = ((items.maxOfOrNull { it.order } ?: 0) + 10)

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

    private enum class SettingsTab(val label: String) {
        CATEGORIES("Catégories"),
        TRANSPORTS("Transports"),
        STATIONS("Gares"),
        CONTACTS("Contacts"),
        GROUPS("Groupes"),
        PRESETS("Prédéfinis")
    }

    companion object {
        private const val REQUEST_PICK_CONTACT = 42
    }
}
