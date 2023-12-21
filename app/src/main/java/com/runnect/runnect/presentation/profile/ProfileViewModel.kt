package com.runnect.runnect.presentation.profile

import androidx.lifecycle.ViewModel
import com.runnect.runnect.data.dto.DepartureData
import com.runnect.runnect.data.dto.ProfileCourseData

class ProfileViewModel : ViewModel() {
    val courseList: List<ProfileCourseData> = generateMockData()

    private fun generateMockData(): List<ProfileCourseData> {
        val mockDataList = mutableListOf<ProfileCourseData>()

        val mockData1 = ProfileCourseData(
            publicCourseId = 1,
            courseId = 101,
            title = "제목 1",
            image = "이미지 1",
            departure = DepartureData(
                region = "지역 1",
                city = "도시 1",
                town = "동네 1",
                detail = null,
                name = "출발지 1"
            ),
            scrapTF = true
        )
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        mockDataList.add(mockData1)
        return mockDataList
    }
}
