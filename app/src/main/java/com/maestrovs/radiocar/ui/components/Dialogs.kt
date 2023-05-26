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