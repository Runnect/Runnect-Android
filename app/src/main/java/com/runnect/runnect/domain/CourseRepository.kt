    suspend fun getRecommendCourse():MutableList<RecommendCourseDTO>
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap):ResponseCourseScrap
    suspend fun getCourseSearch(keyword:String): MutableList<CourseSearchDTO>
