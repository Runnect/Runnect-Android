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

## 맡은 뷰와 기능

<details>
 <summary> 우남 </summary>
 <div markdown="1">
   
 <details>
 <summary> 1.코스 그리기 </summary>
 <div markdown="1">
 
   1. **코스 그리기**
   
       ![코스그리기1](https://user-images.githubusercontent.com/89737271/216815000-80f4627e-e481-4714-852d-c9d1ed427eab.PNG)

- `CourseMainFragment`에서는 `NaverMap API`, `TedParkPermission` 라이브러리를 활용하여 앱 빌드 시 현위치 권한을 받아오는 기능을 구현하였습니다. 오른쪽 하단의 현 위치 floattingButton을 터치하면 gps로 받아온 현위치로 카메라를 이동합니다.

- `DepartureActivity`에서는 `Tmap API`를 활용하여 keyword와 발급받은 app key를 쿼리문을 활용하여 해당 키워드에 대응되는 data들을 받아옵니다. 이때 뷰에서 보여줄 data는 일부이기 때문에 map 함수와 data class를 활용하여 사용할 data들만 따로 `SearchResultEntitiy`로 만들어주어 편의성 및 가독성을 높였습니다.

- 검색 결과를 보여주는 리사이클러뷰의 어댑터는 ListAdapter로 구현하였으며 item을 터치하면 해당 주소를 출발지점으로 설정할 수 있는 화면으로 넘어갑니다.  
   
   
     ![코스그리기2](https://user-images.githubusercontent.com/89737271/216815438-78e9d502-3c7e-4077-a1fd-f44b09b0b934.PNG)
   
- 출발 지점 마커의 정보창은 가변적이지 않기 때문에 `NaverMap API`에서 제공하는 `infoWindow`를 활용하지 않고 마커 이미지와 정보창 이미지를 그룹화한 후 한 번에 svg를 추출하여 사용하였습니다.

- `DrawActivty`에서는 `DepartureActivity`에서 intent로 받아온 출발지점 data로 마커를 생성하고 `setOnMapClickListener`를 사용하여 사용자의 터치 이벤트가 발생할 때마다 해당 좌표값을 별도의 List에 추가하여 관리하고 마커 객체가 생성되게 하였습니다. 경로선 그리기는 `path.map`을 활용하였습니다.

- 경로선의 거리 값은 `AAC ViewModel`에 `LiveData`를 만들어서 관리하였고 `Data Binding`으로 XML과 연결해서 값의 변화가 일어날 때마다 화면에 보여질 수 있게 하였습니다. 
   
- 화면 우측 하단의 백버튼은 누르면 터치 좌표 값을 관리하는 `touchList`와 생성된 마커를 관리하는 `markerList`에서 마지막 추가된 element를 삭제하는 식으로 구현하였습니다. 
   
- 완성하기 버튼을 누르면 `LatLngBounds`를 만들고 `snapShot` 메서드를 사용하여 그렸던 경로를 캡쳐하였습니다. 캡쳐된 이미지는 `Bitmap` -> `Uri` -> `RequestBody` 타입으로 변환한 후 경로 정보가 담긴 `JSON`과 함께 서버로 post하는 `멀티파트 통신`을 하였습니다. 
   
    ![코스그리기3](https://user-images.githubusercontent.com/89737271/216815742-788d790a-cdbc-4e59-a5d5-436051cd7a1c.PNG)
   
- 1초의 간격을 가지고 크기가 작아지면서 숫자가 바뀌는 애니메이션 구현 후 애니메이션 종료 시 자동으로 `RunActivity`로 전환될 수 있게 하였습니다.

- `RunActivity`에서는 현위치와 그렸던 경로를 동시에 보여주며 `Timer` 기능이 구현되어있습니다. 종료버튼을 누르면 `Timer`가 멈춥니다.
   
- RunActivity는 StorageMyDrawFragment에서 넘어오는 data와 DrawActivity에서 넘어오는 data를 모두 처리할 수 있어야 합니다. 따라서 if문으로 각각의 getExtra에 대해 null check를 한 후 null이 아닌 data를 ViewModel에 저장한 후 Activity에서 불러와 logic에 활용될 수 있게하였습니다. 
   

    ![코스그리기4](https://user-images.githubusercontent.com/89737271/216815903-608cd99b-7a48-451d-806f-86d08ed46aa5.PNG)

- `EndRunActivity`에서는 이전 Activity들로부터 intent로 받아온 data들을 화면에 보여줍니다. 이때 intent로 주고받는 data들은 별도의 data class로 관리함으로써 코드 가독성을 높이고자 하였습니다. 

- (미구현-23.02.05) 저장하기 버튼을 누르면 서버로 런닝 기록이 post 됩니다. 

 </div>
</details>  

 <details>
 <summary> 2.보관함 </summary>
 <div markdown="1">
 
   2. **보관함**
   
      ![보관함1](https://user-images.githubusercontent.com/89737271/216816737-e67beeb1-fb7e-40f1-b056-ef78ad439b95.PNG)  

- TabLayout의 각 Tab 터치 시 화면이 바뀌어야 해서 `StorageMainFragment` 하위에 `StorageMyDrawFragment`, `StorageScrapFragment`를 만들어서 뷰 구성을 하였습니다.

- `Header`에 `machineID`를 넣어서 서버 통신을 하고 `DrawActivity`에서 만들었던 코스들의 data를 받아와 리사이클러뷰로 보여주었습니다.

- 리사이클러뷰 item 터치 시 `MyDrawDetailActivity`로 이동하며 터치된 item의 `courseID`를 intent로 전달합니다.

- `MyDrawDetailActivity`에서는 `StorageMyDrawFragment`에서 받아온 `courseID`를 레트로핏 서비스에 `Path`로 넣어서 서버 통신을 하고 해당 `courseID`에 대응되는 data를 받아와서 화면에 보여줍니다.
   
- 시작하기 버튼 터치 시 `MyDrawDetailActivity`에서 `RunActivity로 화면 전환되며 이때 RunActivity에서 필요로 하는 data는 DetailToRunData라는 data class를 만들어서 전달합니다.  
   
   
   ![보관함2](https://user-images.githubusercontent.com/89737271/216817424-3736f185-ff2a-4cfd-98fc-0f0452eadc89.PNG)

- 스크랩 Tab 터치 시 StorageMyDrawFragment에서 StorageScrapFragment로 replace됩니다.

- StorageMyDrawFragment에서는 코스 발견에서 스크랩했던 코스 목록을 서버 통신으로 받아와서 리사이클러뷰로 보여줍니다.
      
- (미구현-23.02.05) 리사이클러뷰 item 터치 시 스크랩 상세 페이지로 이동합니다.
- (미구현-23.02.05) 하트 버튼을 누르면 사용자의 해당 스크랩 data를 삭제합니다.   
   
 </div>
</details>
   
 </div>
</details>
  
<details>
 <summary> 지훈 </summary>
 <div markdown="1">
 
<details>
 <summary> 1.코스 발견 </summary>
 <div markdown="1">
 
   1. **코스 발견**

 </div>
</details>  
   
 <details>
  <summary> 2.마이페이지 </summary>
  <div markdown="1">
 
   2. **마이페이지**

 </div>
</details>  

 </div>
</details>  
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
