package com.example.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        limpiarRegistros()

        val lector = findViewById<Button>(R.id.btnQR)
        val camara = findViewById<Button>(R.id.btnFoto)
        lector.setOnClickListener { lector() }
        camara.setOnClickListener { camara() }
    }//onCreate

    fun lector() {

        val lectorAct = Intent(applicationContext, LectorActivity::class.java)
        startActivity(lectorAct)
    } //lector

    fun camara() {
        val fotoAct = Intent(applicationContext, FotoActivity::class.java)
        startActivity(fotoAct)
    } //camara

    private fun limpiarRegistros() {
        val sharedPreferences = getSharedPreferences("RegistrosPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Borrar todos los registros almacenados
        editor.clear()
        editor.apply() // O editor.commit() para operaciones sincrónicas

        Toast.makeText(this, "Registros limpiados al iniciar", Toast.LENGTH_SHORT).show()
    }


}