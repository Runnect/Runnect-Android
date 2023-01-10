import com.runnect.runnect.data.dto.response.ResponseUser
import com.runnect.runnect.data.source.remote.UserDataSource
import com.runnect.runnect.domain.UserRepository
    override suspend fun getUserInfo(): ResponseUser = userDataSource.getUserInfo()
