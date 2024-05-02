package com.runnect.runnect.data.source.remote

import com.runnect.runnect.data.dto.response.ResponseGetSearchTmap
import com.runnect.runnect.data.service.SearchService
import javax.inject.Inject

class RemoteDepartureSearchDataSource @Inject constructor(private val searchService: SearchService) {
    suspend fun getSearchList(searchKeyword: String): Result<ResponseGetSearchTmap> =
        searchService.getSearchLocation(keyword = searchKeyword)

}