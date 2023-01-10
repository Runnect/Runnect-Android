import com.runnect.runnect.data.api.PUserService
import com.runnect.runnect.data.dto.response.ResponseUser
    suspend fun getUserInfo(): ResponseUser = userService.getUserInfo()
