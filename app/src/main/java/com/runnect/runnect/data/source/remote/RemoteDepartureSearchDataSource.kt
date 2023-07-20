package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.tmap.SearchResponseTmapDto
import com.runnect.runnect.data.service.SearchService
import retrofit2.Response
import javax.inject.Inject

class RemoteDepartureSearchDataSource @Inject constructor(private val searchService: SearchService) {
    suspend fun getSearchList(searchKeyword: String): Response<SearchResponseTmapDto> =
        searchService.getSearchLocation(keyword = searchKeyword)

}