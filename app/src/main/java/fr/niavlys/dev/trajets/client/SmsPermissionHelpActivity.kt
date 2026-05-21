package fr.niavlys.dev.trajets.client

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import fr.niavlys.dev.trajets.R

class SmsPermissionHelpActivity : AppCompatActivity() {
    private val appBackground = Color.rgb(246, 248, 252)
    private val surface = Color.WHITE
    private val textPrimary = Color.rgb(15, 23, 42)
    private val textSecondary = Color.rgb(100, 116, 139)
    private val border = Color.rgb(226, 232, 240)
    private val primary = Color.rgb(37, 99, 235)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val scrollView = ScrollView(this).apply {
            setFillViewport(true)
            setBackgroundColor(appBackground)
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }
        scrollView.addView(root, matchWrap())

        root.addView(text("Autoriser les SMS", 30f, textPrimary, Typeface.BOLD))
        root.addView(text(
            "Si Android bloque l'autorisation SMS après installation de l'APK, active d'abord les paramètres restreints pour Trajets.",
            16f,
            textSecondary
        ).apply { setPadding(0, dp(8), 0, dp(12)) })

        root.addView(stepCard(
            1,
            "Ouvrir les infos",
            "Appuie longtemps sur l'icône Trajets, puis touche le bouton infos.",
            R.drawable.tutorial_sms_step_1
        ))
        root.addView(stepCard(
            2,
            "Ouvrir les autorisations",
            "Dans Infos sur l'application, ouvre Autorisations de l'application.",
            R.drawable.tutorial_sms_step_2
        ))
        root.addView(stepCard(
            3,
            "Autoriser les paramètres restreints",
            "Dans les paramètres avancés ou le menu en haut à droite, active Autoriser les paramètres restreints.",
            R.drawable.tutorial_sms_step_3
        ))
        root.addView(stepCard(
            4,
            "Autoriser les SMS",
            "Reviens dans Trajets, retente l'envoi, puis appuie sur Autoriser quand Android le demande.",
            R.drawable.tutorial_sms_step_4
        ))

        root.addView(Button(this).apply {
            text = "Retour"
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            background = rounded(primary, 14)
            setOnClickListener { finish() }
        }, marginLayout(top = 16))

        setContentView(scrollView)
    }

    private fun stepCard(number: Int, title: String, body: String, imageRes: Int): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(16))
            background = cardBackground(surface)

            addView(text("$number. $title", 20f, textPrimary, Typeface.BOLD))
            addView(text(body, 15f, textSecondary).apply {
                setPadding(0, dp(6), 0, dp(12))
            })
            addView(ImageView(this@SmsPermissionHelpActivity).apply {
                setImageResource(imageRes)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                background = rounded(Color.rgb(248, 250, 252), 14)
                setPadding(dp(8), dp(8), dp(8), dp(8))
            }, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
        }.also {
            it.layoutParams = marginLayout(top = 12)
        }

    private fun text(label: String, size: Float, color: Int, style: Int = Typeface.NORMAL): TextView =
        TextView(this).apply {
            text = label
            textSize = size
            setTextColor(color)
            typeface = Typeface.create(Typeface.DEFAULT, style)
            setLineSpacing(dp(2).toFloat(), 1.0f)
        }

    private fun matchWrap(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    private fun marginLayout(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        matchWrap().apply { setMargins(0, dp(top), 0, dp(bottom)) }

    private fun cardBackground(color: Int): GradientDrawable =
        rounded(color, 18).apply { setStroke(dp(1), border) }

    private fun rounded(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
