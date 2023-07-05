package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.KSearchService
import com.runnect.runnect.data.model.tmap.SearchResponseTmapDto
import retrofit2.Response
import javax.inject.Inject

class DepartureSearchDataSource @Inject constructor(private val searchService: KSearchService) {
    suspend fun getSearchList(searchKeyword: String): Response<SearchResponseTmapDto> =
        searchService.getSearchLocation(keyword = searchKeyword)

}