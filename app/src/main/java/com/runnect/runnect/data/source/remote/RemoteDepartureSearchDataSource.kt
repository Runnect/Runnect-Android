package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.service.SearchService
import com.runnect.runnect.data.model.tmap.SearchResponseTmapDto
import retrofit2.Response
import javax.inject.Inject

class RemoteDepartureSearchDataSource @Inject constructor(private val searchService: SearchService) {
    suspend fun getSearchList(searchKeyword: String): Response<SearchResponseTmapDto> =
        searchService.getSearchLocation(keyword = searchKeyword)

}