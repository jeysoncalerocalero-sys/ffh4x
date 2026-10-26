package com.ffh4x.app

import android.os.Environment
import java.io.File

object FileApplier {

    private const val RUTA_JUEGO =
        "Android/data/com.dts.freefireth/files/contentcache/Compulsory/android/gameassetbundles/config"

    private const val NUESTRA = "FFH4X"

    private fun externa(): File = Environment.getExternalStorageDirectory()
    private fun dirJuego(): File = File(externa(), RUTA_JUEGO)
    private fun dirNuestra(): File = File(externa(), NUESTRA)
    private fun dirBackup(): File = File(dirNuestra(), "backup_original")
    private fun dirPerfiles(): File = File(dirNuestra(), "perfiles")

    fun perfiles(): List<String> {
        val f = dirPerfiles()
        if (!f.exists()) f.mkdirs()
        val lista = f.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
        return if (lista.isEmpty()) listOf("original") else lista
    }

    fun backup(): String {
        val origen = dirJuego()
        if (!origen.exists())
            return "No encontre la carpeta del juego. Abre Free Fire una vez."
        val destino = dirBackup()
        destino.deleteRecursively()
        destino.mkdirs()
        copiar(origen, destino)
        return "Respaldo listo en /FFH4X/backup_original"
    }

    fun restaurar(): String {
        val origen = dirBackup()
        if (!origen.exists()) return "Todavia no hiciste respaldo"
        copiar(origen, dirJuego())
        return "Original restaurado. Reinicia Free Fire."
    }

    fun aplicarPerfil(perfil: String): String {
        val origen = File(dirPerfiles(), perfil)
        if (!origen.exists() || origen.listFiles().isNullOrEmpty())
            return "El perfil '$perfil' esta vacio. Mete archivos en /FFH4X/perfiles/$perfil"
        copiar(origen, dirJuego())
        return "Perfil '$perfil' aplicado. Reinicia Free Fire."
    }

    private fun copiar(origen: File, destino: File) {
        if (!destino.exists()) destino.mkdirs()
        origen.listFiles()?.forEach { f ->
            val d = File(destino, f.name)
            if (f.isDirectory) copiar(f, d) else f.copyTo(d, overwrite = true)
        }
    }
}
