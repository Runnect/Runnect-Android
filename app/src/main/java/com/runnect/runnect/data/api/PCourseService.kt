interface PCourseService {
    @GET("/api/public-course")
    suspend fun getRecommendCourse(
    ): ResponseRecommendCourse

    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestCourseScrap: RequestCourseScrap
    ): ResponseCourseScrap

