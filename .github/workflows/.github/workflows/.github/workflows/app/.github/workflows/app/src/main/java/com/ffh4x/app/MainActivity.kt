package com.ffh4x.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvOverlay: TextView
    private lateinit var tvArchivos: TextView
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvOverlay = findViewById(R.id.tv_estado_overlay)
        tvArchivos = findViewById(R.id.tv_estado_archivos)
        tvResultado = findViewById(R.id.tv_resultado)

        findViewById<Button>(R.id.btn_overlay).setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:\$packageName")
                    )
                )
            } else {
                startService(Intent(this, FloatingMenuService::class.java))
                toast("Menu flotante encendido")
            }
        }

        findViewById<Button>(R.id.btn_archivos).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                !Environment.isExternalStorageManager()
            ) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:\$packageName")
                    )
                )
            } else {
                toast("Ya tienes acceso a los archivos")
            }
        }

        findViewById<Button>(R.id.btn_backup).setOnClickListener {
            mostrar(FileApplier.backup())
        }

        findViewById<Button>(R.id.btn_restaurar).setOnClickListener {
            mostrar(FileApplier.restaurar())
        }

        val contenedor = findViewById<LinearLayout>(R.id.lista_perfiles)
        for (perfil in FileApplier.perfiles()) {
            val b = Button(this)
            b.text = perfil
            b.setOnClickListener { mostrar(FileApplier.aplicarPerfil(perfil)) }
            contenedor.addView(b)
        }
