package com.pankajgadge.impl.datasource

import com.pankajgadge.api.StudentApi
import com.pankajgadge.api.model.Student
import kotlinx.coroutines.delay

class FakeStudentApi : StudentApi {
    override suspend fun getStudents(): List<Student> {
        delay(500)
        return listOf(
            Student("1", "Aarav", "10"),
            Student("2", "Meera", "9"),
            Student("3", "Kabir", "11")
        )
    }
}