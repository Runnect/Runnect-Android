package com.runnect.runnect.util.extension

import com.runnect.runnect.data.dto.*
import com.runnect.runnect.data.dto.response.*

fun ResponsePostLogin.toData(): LoginDTO {
    with(this.data) {
        return LoginDTO(
            status = status,
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = email,
            type = type
        )
    }

}