package com.runnect.runnect.data.repository

import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUser
import com.runnect.runnect.data.source.remote.UserDataSource
import com.runnect.runnect.domain.UserRepository

class UserRepositoryImpl(private val userDataSource: UserDataSource) : UserRepository {
    override suspend fun getUserInfo(): ResponseUser = userDataSource.getUserInfo()
    override suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName =
        userDataSource.updateNickName(requestUpdateNickName)

    override suspend fun getRecord(): MutableList<RecordInfoDTO> {
        val recordList = mutableListOf<RecordInfoDTO>()
        for (i in userDataSource.getRecord().data.records) {
            recordList.add(i.toData())
        }
        return recordList
    }
}