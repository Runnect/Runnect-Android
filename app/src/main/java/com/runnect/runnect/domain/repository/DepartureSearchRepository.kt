package com.runnect.runnect.domain.repository

import com.runnect.runnect.data.dto.SearchResultEntity
import kotlinx.coroutines.flow.Flow

interface DepartureSearchRepository {
    suspend fun getSearchList(keyword: String): Flow<Result<List<SearchResultEntity>>>
}