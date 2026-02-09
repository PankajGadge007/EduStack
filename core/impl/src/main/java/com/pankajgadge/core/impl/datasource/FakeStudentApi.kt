package com.pankajgadge.core.impl.datasource

import com.pankajgadge.core.impl.network.StudentApi
import com.pankajgadge.core.api.model.Student
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