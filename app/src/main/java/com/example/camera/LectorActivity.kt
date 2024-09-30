package com.example.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class LectorActivity : AppCompatActivity() {
    //Instonclas
    private lateinit var codigo: EditText
    private lateinit var descripcion: EditText
    private lateinit var name: EditText
    private lateinit var importancia: EditText
    private lateinit var btnEscanear: Button
    private lateinit var btnCapturar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnBuscar: Button

    private val registros = Array(10) { Registro("", "", "", 0) }
    private var registroIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)

        cargarRegistros()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//Asocian con componente grafico
        codigo = findViewById(R.id.etRed)
        descripcion = findViewById(R.id.etDesc)
        name= findViewById(R.id.etName)
        importancia= findViewById(R.id.etRelev)
        btnEscanear = findViewById(R.id.btnScan)
        btnCapturar = findViewById(R.id.btnCapQR)
        btnBuscar=findViewById(R.id.btnSearch)
        btnLimpiar=findViewById(R.id.btnCleanQR)

        //Eventos
        btnEscanear. setOnClickListener { escanearCodigo() }
        btnCapturar.setOnClickListener {
            capturarDatos()
        }//btnCapturar

        btnLimpiar.setOnClickListener { limpiar() }
        btnBuscar.setOnClickListener { buscarRegistro() }



    }//oncreate

    private fun capturarDatos() {
        val codigoText = codigo.text.toString().trim() // Eliminar espacios
        val descripcionText = descripcion.text.toString().trim()
        val nameText = name.text.toString().trim()
        val importanciaText = importancia.text.toString().trim()

        if (codigoText.isEmpty() || descripcionText.isEmpty() || nameText.isEmpty() || importanciaText.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
            return
        }


        val cantidadValue = importanciaText.toIntOrNull()



        if (cantidadValue == null || cantidadValue <= 0) {
            Toast.makeText(this, "La cantidad es inválida", Toast.LENGTH_LONG).show()
            return
        }

        // Guardar el registro después de limpiar el código
        if (registroIndex < registros.size) {
            registros[registroIndex] = Registro(codigoText, descripcionText, nameText, cantidadValue)
            registroIndex++
            Toast.makeText(this, "Datos capturados correctamente", Toast.LENGTH_SHORT).show()
            guardarRegistros()
            limpiar()
        } else {
            Toast.makeText(this, "Registro lleno, no se pueden agregar más datos", Toast.LENGTH_LONG).show()
        }
    }


    private fun escanearCodigo() {
//Instancia para leer codigos

        val intentIntegrator = IntentIntegrator(this@LectorActivity)
//Definir el tipo de codigo a leer cualquier formato de código
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        intentIntegrator.setPrompt("Lector de códigos") //Titulo en cámara
        intentIntegrator.setCameraId(0) //Definir cámara frontal
        intentIntegrator.setBeepEnabled(true) //emitir beep al tomar la foto
        intentIntegrator.setBarcodeImageEnabled(true) //almacenar el código leído
        intentIntegrator.initiateScan() //iniciar escaneo
    } //escanearCodigo

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
//Instancia para recibir el resultado (Lectura de código)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//Validar que no este vacia
        if (intentResult != null) {
//Validar leyo información
            if (intentResult.contents == null) {
//Mensaje informativo - no hubo datos
                Toast.makeText(this,"Lectura cancelada", Toast.LENGTH_SHORT).show()
            } else {
//Mensaje informativo - si hubo datos
                Toast.makeText(this,"Codigo Leido", Toast.LENGTH_SHORT).show()
//Colocar el codigo en la caja de texto
                codigo.setText(intentResult.contents)
            } //if-else = null
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }//if-else !=null
    }//onActivityResult

    private fun limpiar(){
        codigo.setText("")
        descripcion.setText("")
        name.setText("")
        importancia.setText("")
    }

    private fun buscarRegistro() {
        val codigoBuscar = codigo.text.toString().trim()
        if (codigoBuscar.isEmpty()) {
            Toast.makeText(this, "Ingrese un código para buscar", Toast.LENGTH_LONG).show()
            return
        }

        val registro = registros.find { it.codigo == codigoBuscar }
        if (registro != null) {
            descripcion.setText(registro.descripcion)
            name.setText(registro.name)
            importancia.setText(registro.importancia.toString())
            Toast.makeText(this, "Registro encontrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Registro no encontrado", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "Regreso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun guardarRegistros() {
        val sharedPreferences = getSharedPreferences("RegistrosPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for (i in registros.indices) {
            val registro = registros[i]
            editor.putString("codigo_$i", registro.codigo)
            editor.putString("descripcion_$i", registro.descripcion)
            editor.putString("precio_$i", registro.name)
            editor.putInt("cantidad_$i", registro.importancia)
        }
        editor.putInt("registroIndex", registroIndex)
        editor.apply()
    }
    private fun cargarRegistros() {
        val sharedPreferences = getSharedPreferences("RegistrosPrefs", MODE_PRIVATE)

        for (i in registros.indices) {
            val codigo = sharedPreferences.getString("codigo_$i", "") ?: ""
            val descripcion = sharedPreferences.getString("descripcion_$i", "") ?: ""
            val precio = sharedPreferences.getString("precio_$i", "") ?: ""
            val cantidad = sharedPreferences.getInt("cantidad_$i", 0)

            if (codigo.isNotEmpty()) {
                registros[i] = Registro(codigo, descripcion, precio, cantidad)
            }
        }
        registroIndex = sharedPreferences.getInt("registroIndex", 0)
    }

}