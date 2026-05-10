package com.example.studentdirectory.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.FormMahasiswaActivity
import com.example.studentdirectory.R
import com.example.studentdirectory.adapter.StudentAdapter
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.entity.StudentEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvStudentCount: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi view
        recyclerView = view.findViewById(R.id.recyclerView)
        tvStudentCount = view.findViewById(R.id.tvStudentCount)
        fabAdd = view.findViewById(R.id.fabAdd)

        // Inisialisasi database
        db = AppDatabase.getDatabase(requireContext())

        // Setup RecyclerView
        adapter = StudentAdapter(
            students = emptyList(),
            onEdit = { student -> bukaFormEdit(student) },
            onDelete = { student -> konfirmasiHapus(student) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Tombol tambah
        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), FormMahasiswaActivity::class.java)
            startActivity(intent)
        }

        // Load data
        loadData()
    }

    // Dipanggil tiap kali fragment kembali ke layar
    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            // Insert sample data jika database masih kosong
            val count = db.studentDao().getStudentCount()
            if (count == 0) {
                insertSampleData()
            }

            // Ambil semua data dan tampilkan
            val students = db.studentDao().getAllStudents()
            adapter.updateData(students)
            tvStudentCount.text = "${students.size} Mahasiswa terdaftar"
        }
    }

    private suspend fun insertSampleData() {
        val sampleData = listOf(
            StudentEntity(name = "Ahmad Fauzi", nim = "2024001", prodi = "Teknik Informatika", email = "ahmad@email.com", semester = 3),
            StudentEntity(name = "Budi Santoso", nim = "2024002", prodi = "Sistem Informasi", email = "budi@email.com", semester = 2),
            StudentEntity(name = "Clara Wijaya", nim = "2024003", prodi = "Teknik Informatika", email = "clara@email.com", semester = 4)
        )
        db.studentDao().insertAll(sampleData)
    }
//
    private fun bukaFormEdit(student: StudentEntity) {
        val intent = Intent(requireContext(), FormMahasiswaActivity::class.java)
        intent.putExtra("STUDENT_ID", student.id)
        intent.putExtra("MODE", "EDIT")
        startActivity(intent)
    }

    private fun konfirmasiHapus(student: StudentEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"${student.name}\"? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    db.studentDao().deleteById(student.id)
                    loadData()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}