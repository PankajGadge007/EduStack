package com.pankajgadge.edustack.core.network

import com.pankajgadge.edustack.core.StudentApi
import com.pankajgadge.edustack.core.model.StudentDto
import kotlinx.coroutines.delay

class FakeStudentApi : StudentApi {
    override suspend fun getStudents(): List<StudentDto> {
        delay(500)
        return listOf(
            StudentDto("1", "Aarav", "10"),
            StudentDto("2", "Meera", "9"),
            StudentDto("3", "Kabir", "11")
        )
    }
}
