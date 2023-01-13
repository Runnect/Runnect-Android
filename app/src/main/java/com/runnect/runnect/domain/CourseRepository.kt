    suspend fun getRecommendCourse():MutableList<RecommendCourseDTO>
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap):ResponseCourseScrap
    suspend fun getCourseSearch(keyword:String): MutableList<CourseSearchDTO>
    suspend fun getCourseDetail(publicCourseId:Int):CourseDetailDTO
