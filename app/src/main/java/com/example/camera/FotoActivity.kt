package com.example.camera

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Environment
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FotoActivity : AppCompatActivity() {
    //Instancias
    private lateinit var foto: ImageView
    private lateinit var btnTomar: Button
    private lateinit var btnGuardar: Button
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//Asociar con instancia
        foto = findViewById(R.id.ivFoto)
        btnTomar = findViewById(R.id.btnTFoto)
        btnGuardar=findViewById(R.id.btnSave)

//Mëtodos
        btnTomar.setOnClickListener {

            if (checkPermissions()) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                responseLauncher.launch(intent)
            } else {
                requestPermissions()
            }
//Intancia para abrir la cámara
//Intancia para abrir la camara

                //val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//Lo que sucede cuando la cámara regresa un resultado
                //responseLauncher. launch(intent)

        }

        btnGuardar.setOnClickListener {
            if (imageBitmap != null) {
                guardarFoto(imageBitmap!!)
            } else {
                Toast.makeText(this, "No hay foto para guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }//onCreate

    @SuppressLint("SuspiciousIndentation")
    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            Toast.makeText(this, "Foto tomada", Toast.LENGTH_SHORT).show()
            val extras = activityResult.data!!.extras
            imageBitmap = extras!!["data"] as Bitmap?
            foto.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(this, "Foto no tomada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarFoto(bitmap: Bitmap) {
        if (isExternalStorageWritable()) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            // Crear el directorio si no existe
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs()
            }

            // Generar el nombre del archivo
            val imageFile = File(storageDir, "JPEG_${timeStamp}.jpg")

            try {
                val fos = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                Toast.makeText(this, "Foto guardada en: ${imageFile.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El almacenamiento externo no está disponible", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                responseLauncher.launch(intent)
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
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

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}//class