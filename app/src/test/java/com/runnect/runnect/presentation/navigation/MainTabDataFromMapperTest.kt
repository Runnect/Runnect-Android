package com.runnect.runnect.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class MainTabDataFromMapperTest {

    @Test
    fun `그리기 관련 dataFrom 값은 STORAGE로 매핑된다`() {
        assertEquals(MainTab.STORAGE, mapDataFromToMainTab("fromDrawCourse"))
        assertEquals(MainTab.STORAGE, mapDataFromToMainTab("fromDeleteMyDrawDetail"))
        assertEquals(MainTab.STORAGE, mapDataFromToMainTab("fromMyDrawDetail"))
    }

    @Test
    fun `코스발견 관련 dataFrom 값은 DISCOVER로 매핑된다`() {
        assertEquals(MainTab.DISCOVER, mapDataFromToMainTab("fromMyScrap"))
        assertEquals(MainTab.DISCOVER, mapDataFromToMainTab("fromCourseDetail"))
    }

    @Test
    fun `매칭되지 않는 값은 null로 매핑된다`() {
        assertEquals(null, mapDataFromToMainTab("detail"))
        assertEquals(null, mapDataFromToMainTab("unknown"))
    }

    @Test
    fun `null 입력은 null로 매핑된다`() {
        assertEquals(null, mapDataFromToMainTab(null))
    }
}
