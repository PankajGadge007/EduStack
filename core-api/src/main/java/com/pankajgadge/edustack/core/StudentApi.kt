package com.pankajgadge.edustack.core

import com.pankajgadge.edustack.core.model.StudentDto

interface StudentApi {
    suspend fun getStudents(): List<StudentDto>
}