package com.pankajgadge.core.api

import com.pankajgadge.core.api.model.Student

interface StudentApi {
    suspend fun getStudents(): List<Student>
}