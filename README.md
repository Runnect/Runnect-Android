# Runnect-Android
![image](https://user-images.githubusercontent.com/70442964/210553519-82a60073-8a9f-4b4c-a296-d2020414285c.png)
<br/>
<br/>
<br/>
# 서비스 한 줄 소개  
Runnect는 Run과 connect의 합성어로 직접 코스를 그리고 공유하며  서로를 연결해주고 
함께 달릴 수 있는 서비스입니다. 
<br/>
<br/>

# 🏃‍♀️🏃‍♂️Contributors🏃‍♂️🏃‍♀️
|김우남|박지훈|
|------|---|
|<p align="center"> [unam98](https://github.com/unam98)|<p align="center"> [Larry7939](https://github.com/Larry7939)|
|<p align="center"> <img width="70%" src="https://user-images.githubusercontent.com/70442964/210555989-39736dd6-51ca-4675-8e3f-7be54d9cd099.jpg"/>| <p align="center"><img width="80%" src="https://user-images.githubusercontent.com/70442964/210556002-3afb3793-960e-4881-a906-14c25791a870.png"/>|
<br/>


# Coding Convention
[Android Coding convention](https://www.notion.so/Code-Convention-cbbaf678076a4e74821406016736be73)
<br/>
<br/>

# GitHub Convention
[Git Branch 전략](https://www.notion.so/Git-branch-b91d5935c6744108a2ddf7ef6dc2c494)  
[Commit Convention](https://www.notion.so/Commit-Convention-560391655f4f4669bdd589ec7fe9fc20)
<br/>
<br/>
  
- Issue Template
<br/>
  
![image](https://user-images.githubusercontent.com/70442964/210562158-b3030f89-d972-4141-b703-13813483df2b.png)
<br/>
<br/>
  
- PR Template
<br/>
  
![image](https://user-images.githubusercontent.com/70442964/210562502-146d5e1f-517c-4e37-9a0d-8acbaa326944.png)

<br/>
<br/>

# Project Structure

<img width="20%" src="https://user-images.githubusercontent.com/70442964/212208921-7e8c1e1a-96c3-4372-93c6-594e2f070b78.png"/>

- 패키지 설명

|패키지|설명|
|------|---|
|application|SharedPreference 및 ApplicationClass 관련 파일|
|binding|바인딩 관련 파일|
|data|로컬 또는 원격 데이터 관련 폴더|
|di|의존성 주입을 위한 모듈 관련 폴더|
|domain|Domain Layer 관련 폴더|
|presentation|각 기능을 화면별로 나눈 폴더|
|util|확장함수 및 유틸 함수 관련 폴더|

<br/>


# Tech stack & Open-source libraries
- Minimum SDK level 28
- Kotlin based, Coroutines for asynchronous
- Jetpack
  - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes.
  - ViewModel: Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
  - DataBinding: Binds UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
  - Hilt: for dependency injection.
- Architecture
  - MVVM Architecture (View - DataBinding - ViewModel - Model)
  - Repository Pattern
- Retrofit2 & OkHttp3: Construct the REST APIs and paging network data.
- Timber: A logger with a small, extensible API.
- Coil : set images with URI from Network.

# Product Contributors
![image](https://user-images.githubusercontent.com/70442964/212206146-79bfe4d7-41d3-4c10-ac77-bee7d90149a7.png)
