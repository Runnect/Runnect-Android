package com.runnect.runnect.domain.repository

import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.data.dto.LoginDTO
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun postLogin(requestPostLogin: RequestPostLogin): Flow<Result<LoginDTO>>
}