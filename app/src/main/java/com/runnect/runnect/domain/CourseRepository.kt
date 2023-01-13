    suspend fun getRecommendCourse():MutableList<RecommendCourseDTO>
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap):ResponseCourseScrap
