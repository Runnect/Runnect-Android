import com.runnect.runnect.data.dto.response.ResponseUser
import retrofit2.http.GET
interface PUserService {
    @GET("api/user")
    suspend fun getUserInfo(
    ): ResponseUser
