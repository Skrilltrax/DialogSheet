package com.marcoscg.dialogsheet.dsl

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.marcoscg.dialogsheet.*
import com.marcoscg.dialogsheet.Utils
import com.marcoscg.dialogsheet.Utils.dpToPx
import com.marcoscg.dialogsheet.Utils.gone
import com.marcoscg.dialogsheet.Utils.isVisible
import com.marcoscg.dialogsheet.Utils.visible
import com.marcoscg.dialogsheet.dsl.button.Button
import com.marcoscg.dialogsheet.dsl.button.ButtonBuilder
import com.marcoscg.dialogsheet.dsl.message.Message
import com.marcoscg.dialogsheet.dsl.message.MessageBuilder
import com.marcoscg.dialogsheet.dsl.title.Title
import com.marcoscg.dialogsheet.dsl.title.TitleBuilder

@DialogSheetDsl
class DialogSheetBuilder constructor(private val context: Context) {

    private var bottomSheetDialog: ExpandedBottomSheetDialog
    private lateinit var titleTextView: AppCompatTextView
    private lateinit var messageTextView: AppCompatTextView
    private lateinit var iconImageView: AppCompatImageView
    private lateinit var positiveButton: MaterialButton
    private lateinit var negativeButton: MaterialButton
    private lateinit var neutralButton: MaterialButton
    lateinit var inflatedView: View
        private set

    var coloredNavigationBar = true

    @ColorInt
    var accentColor: Int = -1
    @ColorRes
    var accentColorRes: Int = -1

    @ColorRes
    var backgroundColorRes: Int = -1
    @ColorInt
    var backgroundColor: Int = -1

    @DrawableRes
    var dialogIconRes: Int = -1
    var dialogIconBitmap: Bitmap? = null
    var dialogIconDrawable: Drawable? = null

    private var _backgroundColor = Utils.getAttrColor(context, android.R.attr.windowBackground)

    private var _accentColor = -1
        get() {
            field = when {
                accentColor != -1 -> accentColor
                accentColorRes != -1 -> ContextCompat.getColor(context, accentColorRes)
                else -> Utils.getTextColor(_backgroundColor)
            }
            return field
        }

    private fun findViews() {
        bottomSheetDialog.apply {
            titleTextView = findViewById(R.id.dialogTitle)!!
            messageTextView = findViewById(R.id.dialogMessage)!!
            iconImageView = findViewById(R.id.dialogIcon)!!
            positiveButton = findViewById(R.id.buttonPositive)!!
            negativeButton = findViewById(R.id.buttonNegative)!!
            neutralButton = findViewById(R.id.buttonNeutral)!!
        }
    }

    init {
        accentColor = Utils.getAttrColor(context, R.attr.dialogSheetAccent)
        bottomSheetDialog = if (accentColor != -1) {
            ExpandedBottomSheetDialog(context, R.style.DialogSheetTheme_Colored)
        } else {
            ExpandedBottomSheetDialog(context, R.style.DialogSheetTheme)
        }
        bottomSheetDialog.apply {
            setContentView(R.layout.layout_bottomdialog)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        findViews()
        setColors()
    }

    fun positiveButton(block: ButtonBuilder.() -> Unit)  {
        val button: Button = ButtonBuilder(context).apply(block).build()
        positiveButton.apply {

            positiveButton.text = button.text
            positiveButton.isAllCaps = button.textAllCaps
            if (button.typeface != null)
                positiveButton.typeface = button.typeface
            if (button.color == -1) {
                positiveButton.setBackgroundColor(_accentColor)
                positiveButton.setTextColor(Utils.getTextColor(_accentColor))
            }
            else {
                positiveButton.setBackgroundColor(button.color)
                positiveButton.setTextColor(Utils.getTextColor(button.color))
            }
            positiveButton.setOnClickListener {
                if (button.shouldDismiss) bottomSheetDialog.dismiss()
                button.onClick(positiveButton)
            }
            visible()
        }
    }

    fun negativeButton(block: ButtonBuilder.() -> Unit)  {
        val button: Button = ButtonBuilder(context).apply(block).build()
        negativeButton.apply {
            text = button.text
            isAllCaps = button.textAllCaps
            if (button.typeface != null) typeface = button.typeface
            if (button.color == -1) setTextColor(_accentColor)
            else setTextColor(button.color)
            setOnClickListener {
                if (button.shouldDismiss) bottomSheetDialog.dismiss()
                button.onClick(this)
            }
            visible()
        }
    }

    fun neutralButton(block: ButtonBuilder.() -> Unit)  {
        val button: Button = ButtonBuilder(context).apply(block).build()
        neutralButton.apply {
            text = button.text
            isAllCaps = button.textAllCaps
            if (button.typeface != null) typeface = button.typeface
            if (button.color == -1) setTextColor(_accentColor)
            else setTextColor(button.color)
            setOnClickListener {
                if (button.shouldDismiss) bottomSheetDialog.dismiss()
                button.onClick(this)
            }
            visible()
        }
    }

    fun message(block: MessageBuilder.() -> Unit) {
        val message: Message
        val messageBuilder = MessageBuilder(context)
        message = messageBuilder.apply(block).build()
        if (message.text.isNotEmpty()) {
            messageTextView.visible()
            messageTextView.text = message.text
            messageTextView.textSize = message.textSize.toFloat()
            if (message.typeface != null)
                messageTextView.typeface = message.typeface
            if (message.color == -1) {
               messageTextView.setTextColor(Utils.getTextColorSec(backgroundColor))
            } else {
                messageTextView.setTextColor(message.color)
            }
        }
    }

    fun title(block: TitleBuilder.() -> Unit) {
        val title: Title
        val titleBuilder = TitleBuilder(context)
        title = titleBuilder.apply(block).build()
        if (title.text.isNotEmpty()) {
            titleTextView.visible()
            titleTextView.text = title.text
            titleTextView.textSize = title.textSize.toFloat()
            titleTextView.isSingleLine = title.singleLineTitle
            if (title.typeface != null)
                titleTextView.typeface = title.typeface
            if (title.color == -1) {
                titleTextView.setTextColor(Utils.getTextColor(backgroundColor))
            } else {
                titleTextView.setTextColor(title.color)
            }
        }
    }

    fun build(): DialogSheet {
        setupIcon()
        setupBackground()
        show()
        return DialogSheet(context, bottomSheetDialog, coloredNavigationBar,  iconImageView, titleTextView, messageTextView, positiveButton, negativeButton, neutralButton )
    }

    private fun setupIcon() {
        iconImageView.visible()
        when {
            dialogIconBitmap != null -> iconImageView.setImageBitmap(dialogIconBitmap)
            dialogIconDrawable != null -> iconImageView.setImageDrawable(dialogIconDrawable)
            dialogIconRes != -1 -> iconImageView.setImageResource(dialogIconRes)
            else -> iconImageView.gone()
        }
    }

    private fun setupBackground() {
        _backgroundColor =  when {
            backgroundColor != -1 -> backgroundColor
            backgroundColorRes != -1 -> ContextCompat.getColor(context, backgroundColorRes)
            else -> Utils.getAttrColor(context, android.R.attr.windowBackground)
        }
        bottomSheetDialog.findViewById<View>(R.id.mainDialogContainer)?.background?.colorFilter =
                PorterDuffColorFilter(_backgroundColor, PorterDuff.Mode.SRC_IN)
    }

    fun show() {
        setSpacing()
        setColoredNavBar(coloredNavigationBar)
        bottomSheetDialog.show()
        // Landscape fixed width
        val configuration = context.resources.configuration
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && configuration.screenWidthDp > 400) {
            bottomSheetDialog.window?.setLayout(400.dpToPx(), -1)
        }
    }

    private fun setSpacing() {
        if (!iconImageView.isVisible()) {
            if (titleTextView.isVisible())
                titleTextView.setPadding(0, 24.dpToPx(), 0, 0)
            else
                messageTextView.setPadding(0, 12.dpToPx(), 0, 0)
        }
    }

    private fun setColors() {
        if (_accentColor != -1) {
            positiveButton.setBackgroundColor(_accentColor)
            positiveButton.setTextColor(Utils.getTextColor(_accentColor))
            negativeButton.setTextColor(_accentColor)
            neutralButton.setTextColor(_accentColor)
            titleTextView.setTextColor(Utils.getTextColor(backgroundColor))
            messageTextView.setTextColor(Utils.getTextColorSec(backgroundColor))
        } else {
            positiveButton.setTextColor(Color.WHITE)
        }
    }

    private fun setColoredNavBar(coloredNavigationBar: Boolean) {
        if (coloredNavigationBar && bottomSheetDialog.window != null && Build.VERSION.SDK_INT >= 21) {
            if (Utils.isColorLight(backgroundColor)) {
                if (Build.VERSION.SDK_INT >= 26) {
                    bottomSheetDialog.window?.navigationBarColor = backgroundColor
                    var flags = bottomSheetDialog.window?.decorView?.systemUiVisibility
                    if (flags != null) {
                        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                    bottomSheetDialog.window?.decorView?.systemUiVisibility = flags!!
                }
            } else {
                bottomSheetDialog.window?.navigationBarColor = backgroundColor
                if (Build.VERSION.SDK_INT >= 26) {
                    var flags = bottomSheetDialog.window?.decorView?.systemUiVisibility
                    if (flags != null) {
                        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                    }
                    bottomSheetDialog.window?.decorView?.systemUiVisibility = flags!!
                }
            }
        }
    }
}
