package com.example.studentdirectory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.R
import com.example.studentdirectory.database.entity.StudentEntity

class StudentAdapter(
    private var students: List<StudentEntity>,
    private val onEdit: (StudentEntity) -> Unit,
    private val onDelete: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvNim: TextView = view.findViewById(R.id.tvNim)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }
//
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.tvInitial.text = student.name.take(2).uppercase()
        holder.tvName.text = student.name
        holder.tvNim.text = student.nim
        holder.btnEdit.setOnClickListener { onEdit(student) }
        holder.btnDelete.setOnClickListener { onDelete(student) }
    }

    override fun getItemCount() = students.size

    fun updateData(newStudents: List<StudentEntity>) {
        students = newStudents
        notifyDataSetChanged()
    }
}