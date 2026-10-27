package com.ffh4x.app

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class FloatingMenuService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var burbuja: View
    private lateinit var params: WindowManager.LayoutParams
    private var panel: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        burbuja = LayoutInflater.from(this).inflate(R.layout.burbuja, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 400
        }

        wm.addView(burbuja, params)
        burbuja.setOnTouchListener(Arrastre())
    }

    override fun onDestroy() {
        super.onDestroy()
        panel?.let { runCatching { wm.removeView(it) } }
        runCatching { wm.removeView(burbuja) }
    }

    private fun alternarPanel() {
        val abierto = panel
        if (abierto != null) {
            wm.removeView(abierto)
            panel = null
            return
        }

        val v = LayoutInflater.from(this).inflate(R.layout.panel, null)

        v.findViewById<Button>(R.id.p_backup).setOnClickListener {
            aviso(FileApplier.backup())
        }
        v.findViewById<Button>(R.id.p_restaurar).setOnClickListener {
            aviso(FileApplier.restaurar())
        }

        val cont = v.findViewById<LinearLayout>(R.id.p_perfiles)
        for (perfil in FileApplier.perfiles()) {
            val b = Button(this)
            b.text = perfil
            b.setOnClickListener { aviso(FileApplier.aplicarPerfil(perfil)) }
            cont.addView(b)
        }

        v.findViewById<Button>(R.id.p_cerrar).setOnClickListener { stopSelf() }

        val p = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 560
        }

        wm.addView(v, p)
        panel = v
    }

    private fun aviso(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private inner class Arrastre : View.OnTouchListener {
        private var x0 = 0
        private var y0 = 0
        private var px = 0f
        private var py = 0f
        private var movido = false

        override fun onTouch(v: View, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    x0 = params.x
                    y0 = params.y
                    px = e.rawX
                    py = e.rawY
                    movido = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - px).toInt()
                    val dy = (e.rawY - py).toInt()
                    if (dx != 0 || dy != 0) movido = true
                    params.x = x0 + dx
                    params.y = y0 + dy
                    wm.updateViewLayout(burbuja, params)
                }
                MotionEvent.ACTION_UP -> if (!movido) alternarPanel()
            }
            return true
        }
    }
}
