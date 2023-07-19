package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.SearchResultEntity

interface DepartureSearchRepository {
    suspend fun getSearchList(keyword: String): List<SearchResultEntity>?
}