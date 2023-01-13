    suspend fun getRecommendCourse(): ResponseRecommendCourse = courseService.getRecommendCourse()
    suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap =
        courseService.postCourseScrap(requestCourseScrap)

    suspend fun getCourseSearch(keyword: String) = courseService.getCourseSearch(keyword)
