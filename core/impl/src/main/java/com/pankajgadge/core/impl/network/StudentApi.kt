package com.pankajgadge.core.impl.network

import com.pankajgadge.core.api.model.Student

interface StudentApi {
    suspend fun getStudents(): List<Student>
}