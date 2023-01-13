interface PCourseService {
    @GET("/api/public-course")
    suspend fun getRecommendCourse(
    ): ResponseRecommendCourse
