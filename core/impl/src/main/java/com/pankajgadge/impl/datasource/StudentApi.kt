package com.pankajgadge.impl.datasource

interface StudentApi {
    suspend fun getStudents(): List<Student>
}