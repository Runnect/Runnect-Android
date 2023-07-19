package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.api.KSearchService
import com.runnect.runnect.data.dto.tmap.SearchResponseTmapDto
import retrofit2.Response

class DepartureSearchDataSource(private val searchService: KSearchService) {
    suspend fun getSearchList(searchKeyword: String): Response<SearchResponseTmapDto> =
        searchService.getSearchLocation(keyword = searchKeyword)

}