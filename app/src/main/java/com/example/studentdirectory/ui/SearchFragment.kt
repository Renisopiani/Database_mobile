package com.example.studentdirectory.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.R
import com.example.studentdirectory.adapter.StudentAdapter
import com.example.studentdirectory.database.AppDatabase
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)
        db = AppDatabase.getDatabase(requireContext())

        // Setup adapter — di Search tidak perlu edit/delete
        adapter = StudentAdapter(
            students = emptyList(),
            onEdit = {},
            onDelete = {}
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Load semua data saat pertama buka
        cariData("")

        // Dengarkan perubahan teks
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cariData(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
//
    private fun cariData(keyword: String) {
        lifecycleScope.launch {
            val results = if (keyword.isEmpty()) {
                db.studentDao().getAllStudents()
            } else {
                db.studentDao().searchStudents(keyword)
            }
            adapter.updateData(results)
        }
    }
}