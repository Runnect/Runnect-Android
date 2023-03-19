package com.runnect.runnect.data.repository

import com.naver.maps.geometry.LatLng
import com.runnect.runnect.data.model.SearchResultEntity
import com.runnect.runnect.data.model.tmap.Poi
import com.runnect.runnect.data.model.tmap.Pois
import com.runnect.runnect.data.source.remote.DepartureSearchDataSource
import com.runnect.runnect.domain.DepartureSearchRepository

class DepartureSearchRepositoryImpl(private val departureSourceDataSource: DepartureSearchDataSource) :
    DepartureSearchRepository {
    override suspend fun getSearchList(keyword: String): List<SearchResultEntity>? {
        return changeData(departureSourceDataSource.getSearchList(keyword)
            .body()?.searchPoiInfo?.pois ?: return null)
    }

    private fun changeData(pois: Pois): List<SearchResultEntity> {
        val changedData = pois.poi.map {
            SearchResultEntity(
                fullAddress = makeMainAdress(it),
                name = it.name ?: "",
                locationLatLng = LatLng(it.noorLat.toDouble(), it.noorLon.toDouble())
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