    override suspend fun getRecommendCourse(): MutableList<RecommendCourseDTO> {
        val recommendCourse = mutableListOf<RecommendCourseDTO>()
        for(i in courseDataSource.getRecommendCourse().data.publicCourses){
            recommendCourse.add(i.toData())
        }
        return recommendCourse
    }

    override suspend fun postCourseScrap(requestCourseScrap: RequestCourseScrap): ResponseCourseScrap {
        return courseDataSource.postCourseScrap(requestCourseScrap)
    }
