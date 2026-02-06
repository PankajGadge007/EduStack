package com.pankajgadge.api

import com.pankajgadge.api.model.Student

interface StudentApi {
    suspend fun getStudents(): List<Student>
}