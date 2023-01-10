package com.runnect.runnect.domain

import com.runnect.runnect.data.dto.request.RequestUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUpdateNickName
import com.runnect.runnect.data.dto.response.ResponseUser

interface UserRepository {
    suspend fun getUserInfo(): ResponseUser
    suspend fun updateNickName(requestUpdateNickName: RequestUpdateNickName): ResponseUpdateNickName
}