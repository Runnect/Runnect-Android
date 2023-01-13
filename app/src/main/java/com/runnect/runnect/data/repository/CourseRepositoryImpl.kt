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

    override suspend fun getCourseSearch(keyword: String): MutableList<CourseSearchDTO> {
        val searchPublicCourse = mutableListOf<CourseSearchDTO>()
        for(i in courseDataSource.getCourseSearch(keyword).data.publicCourses){
            searchPublicCourse.add(i.toData())
        }
        return searchPublicCourse
    }

    override suspend fun getCourseDetail(publicCourseId: Int): CourseDetailDTO {
        return courseDataSource.getCourseDetail(publicCourseId).data.toData()
    }

    override suspend fun getMyCourseLoad(): MutableList<CourseLoadInfoDTO> {
        val myCourseLoad = mutableListOf<CourseLoadInfoDTO>()
        for(i in courseDataSource.getMyCourseLoad().data.privateCourses){
            myCourseLoad.add(i.toData())
        }
        return myCourseLoad
    }

