package com.maestrovs.radiocar.ui.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maestrovs.radiocar.R
import kotlin.system.exitProcess


class ExitDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.exit_dialog)

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_yes).setOnClickListener {
            exitProcess(0)
        }

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_no).setOnClickListener {
            dismiss()
        }

    }
}


 fun showDeleteStationDialog(context: Context, name: String, onDelete: ()->(Unit)) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.resources.getString(R.string.remove_ask))
        .setMessage(name)
        .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, which ->

        }
        .setPositiveButton(context.resources.getString(R.string.remove)) { dialog, which ->
            onDelete.invoke()
        }
        .show()
}