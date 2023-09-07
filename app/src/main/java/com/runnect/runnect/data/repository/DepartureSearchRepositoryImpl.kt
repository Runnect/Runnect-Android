package com.runnect.runnect.data.repository

import com.naver.maps.geometry.LatLng
import com.runnect.runnect.data.dto.SearchResultEntity
import com.runnect.runnect.data.dto.tmap.Poi
import com.runnect.runnect.data.dto.tmap.Pois
import com.runnect.runnect.data.source.remote.RemoteDepartureSearchDataSource
import com.runnect.runnect.domain.DepartureSearchRepository
import javax.inject.Inject

class DepartureSearchRepositoryImpl @Inject constructor(private val departureSourceDataSource: RemoteDepartureSearchDataSource) :
    DepartureSearchRepository {
    override suspend fun getSearchList(keyword: String): List<SearchResultEntity>? {
        return changeData(
            departureSourceDataSource.getSearchList(
                searchKeyword = keyword
            )
                .body()?.searchPoiInfo?.pois ?: return null
        )
    }

    private fun changeData(pois: Pois): List<SearchResultEntity> {
        val changedData = pois.poi.map {
            SearchResultEntity(
                fullAddress = makeMainAdress(it),
                name = it.name ?: "",
                locationLatLng = LatLng(it.noorLat.toDouble(), it.noorLon.toDouble()),
                mode = "searchLocation" //현위치, 지도에서 출발과 구분하기 위한 식별자
            )
        }
        return changedData
    }


    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
}