interface PCourseService {
    @GET("/api/public-course")
    suspend fun getRecommendCourse(
    ): ResponseRecommendCourse

    @POST("/api/scrap")
    suspend fun postCourseScrap(
        @Body requestCourseScrap: RequestCourseScrap
    ): ResponseCourseScrap

    @GET("/api/public-course/search?")
    suspend fun getCourseSearch(
        @Query("keyword") keyword: String
    ): ResponseCourseSearch

