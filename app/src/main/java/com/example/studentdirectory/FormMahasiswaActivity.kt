package com.example.studentdirectory

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.entity.StudentEntity
import kotlinx.coroutines.launch

class FormMahasiswaActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etNim: EditText
    private lateinit var spinnerProdi: Spinner
    private lateinit var etEmail: EditText
    private lateinit var etSemester: EditText
    private lateinit var btnSimpan: Button
    private lateinit var db: AppDatabase

    // Untuk mode EDIT, simpan ID mahasiswa yang sedang diedit
    private var studentId: Int = 0
    private var isEditMode = false

    private val listProdi = listOf(
        "Teknik Informatika",
        "Sistem Informasi",
        "Teknik Elektro",
        "Manajemen",
        "Akuntansi"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_mahasiswa)

        // Inisialisasi view
        etNama = findViewById(R.id.etNama)
        etNim = findViewById(R.id.etNim)
        spinnerProdi = findViewById(R.id.spinnerProdi)
        etEmail = findViewById(R.id.etEmail)
        etSemester = findViewById(R.id.etSemester)
        btnSimpan = findViewById(R.id.btnSimpan)
        db = AppDatabase.getDatabase(this)

        // Setup Spinner prodi
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listProdi)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProdi.adapter = spinnerAdapter

        // Cek apakah mode EDIT
        isEditMode = intent.getStringExtra("MODE") == "EDIT"
        studentId = intent.getIntExtra("STUDENT_ID", 0)

        if (isEditMode) {
            supportActionBar?.title = "Edit Mahasiswa"
            loadDataUntukEdit()
        } else {
            supportActionBar?.title = "Tambah Mahasiswa"
        }

        // Tombol simpan
        btnSimpan.setOnClickListener {
            simpanData()
        }
    }

    private fun loadDataUntukEdit() {
        lifecycleScope.launch {
            val student = db.studentDao().getStudentById(studentId)
            student?.let {
                etNama.setText(it.name)
                etNim.setText(it.nim)
                etEmail.setText(it.email)
                etSemester.setText(it.semester.toString())

                // Set spinner ke prodi yang sesuai
                val prodiIndex = listProdi.indexOf(it.prodi)
                if (prodiIndex >= 0) spinnerProdi.setSelection(prodiIndex)
            }
        }
    }

    private fun simpanData() {
        // Ambil nilai dari semua field
        val nama = etNama.text.toString().trim()
        val nim = etNim.text.toString().trim()
        val prodi = spinnerProdi.selectedItem.toString()
        val email = etEmail.text.toString().trim()
        val semesterStr = etSemester.text.toString().trim()

        // Validasi — jangan sampai ada yang kosong
        if (nama.isEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            etNama.requestFocus()
            return
        }
        if (nim.isEmpty()) {
            etNim.error = "NIM tidak boleh kosong"
            etNim.requestFocus()
            return
        }
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }
        if (semesterStr.isEmpty()) {
            etSemester.error = "Semester tidak boleh kosong"
            etSemester.requestFocus()
            return
        }

        val semester = semesterStr.toIntOrNull()
        if (semester == null || semester < 1 || semester > 14) {
            etSemester.error = "Semester harus angka 1-14"
            etSemester.requestFocus()
            return
        }

        // Simpan ke database
        lifecycleScope.launch {
            if (isEditMode) {
                val updated = StudentEntity(
                    id = studentId,
                    name = nama,
                    nim = nim,
                    prodi = prodi,
                    email = email,
                    semester = semester
                )
                db.studentDao().update(updated)
                Toast.makeText(this@FormMahasiswaActivity, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            } else {
                val newStudent = StudentEntity(
                    name = nama,
                    nim = nim,
                    prodi = prodi,
                    email = email,
                    semester = semester
                )
                db.studentDao().insert(newStudent)
                Toast.makeText(this@FormMahasiswaActivity, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            }
            finish() //
        }
    }
}