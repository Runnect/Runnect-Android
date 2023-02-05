# Runnect-Android
![212252027-210bd23c-e363-4e6f-8e15-1a88bf237bc2 (1)](https://user-images.githubusercontent.com/89737271/216818552-9f9c5e62-b6cb-4128-ab6e-99600493b9a8.png)

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
 <summary> 🌱 우남 </summary>
 <div markdown="1">
<br/>   
 <details>
 <summary> 코스 그리기 </summary>
 <div markdown="1">
<br/>
  
<p align="center"><img src="https://user-images.githubusercontent.com/89737271/216815000-80f4627e-e481-4714-852d-c9d1ed427eab.PNG"</p>

- `CourseMainFragment`에서는 `NaverMap API`, `TedParkPermission` 라이브러리를 활용하여 앱 빌드 시 현위치 권한을 받아오는 기능을 구현하였습니다. 오른쪽 하단의 현 위치 floattingButton을 터치하면 gps로 받아온 현위치로 카메라를 이동합니다.

- `DepartureActivity`에서는 `Tmap API`를 활용하였습니다. 쿼리문을 사용하여 사용자로부터 입력받은 키워드에 대응되는 data들을 받아옵니다. 이때 뷰에서 보여줄 data는 일부이기 때문에 map 함수와 data class를 활용하여 사용할 data들만 따로 `SearchResultEntitiy`로 만들어주어 편의성 및 가독성을 높였습니다.

- 검색 결과를 보여주는 리사이클러뷰의 어댑터는 `ListAdapter`로 구현하였으며 item을 터치하면 해당 주소를 출발지점으로 설정할 수 있는 화면으로 넘어갑니다.  
<br/>   
  
<p align="center"><img src="https://user-images.githubusercontent.com/89737271/216815438-78e9d502-3c7e-4077-a1fd-f44b09b0b934.PNG"</p>
   
- 출발 지점 마커의 정보창은 가변적이지 않기 때문에 `NaverMap API`에서 제공하는 `infoWindow`를 활용하지 않고 마커 이미지와 정보창 이미지를 그룹화한 후 한 번에 svg를 추출하여 사용하였습니다.

- `DrawActivty`에서는 `DepartureActivity`에서 intent로 받아온 출발지점 data로 마커를 생성하고 `setOnMapClickListener`를 사용하여 사용자의 터치 이벤트가 발생할 때마다 해당 좌표값을 별도의 List에 추가하여 관리하고 마커 객체가 생성되게 하였습니다. 경로선 그리기는 `path.map`을 활용하였습니다.

- 경로선의 거리 값은 `AAC ViewModel`에 `LiveData`를 만들어서 관리하였고 `Data Binding`으로 XML과 연결해서 값의 변화가 일어날 때마다 화면에 보여질 수 있게 하였습니다. 
   
- 화면 우측 하단의 백버튼은 누르면 터치 좌표 값을 관리하는 `touchList`와 생성된 마커를 관리하는 `markerList`에서 마지막 추가된 element를 삭제하는 식으로 구현하였습니다. 
   
- 완성하기 버튼을 누르면 `LatLngBounds`를 만들고 `snapShot` 메서드를 사용하여 그렸던 경로를 캡쳐하였습니다. 캡쳐된 이미지는 `Bitmap` -> `Uri` -> `RequestBody` 타입으로 변환한 후 경로 정보가 담긴 `JSON`과 함께 서버로 `POST`하는 `멀티파트 통신`을 하였습니다. 
<br/>   
  
<p align="center"><img src="https://user-images.githubusercontent.com/89737271/216815742-788d790a-cdbc-4e59-a5d5-436051cd7a1c.PNG"</p>
   
- 1초의 간격을 가지고 크기가 작아지면서 숫자가 바뀌는 애니메이션 구현 후 애니메이션 종료 시 자동으로 `RunActivity`로 전환될 수 있게 하였습니다.

- `RunActivity`에서는 현위치와 그렸던 경로를 동시에 보여주며 `Timer` 기능이 구현되어있습니다. 종료버튼을 누르면 `Timer`가 멈춥니다.
   
- `RunActivity`는 `StorageMyDrawFragment`에서 넘어오는 data와 `DrawActivity`에서 넘어오는 data를 모두 처리할 수 있어야 합니다. 따라서 if문으로 각각의 `getExtra`에 대해 null check를 한 후 null이 아닌 data를 ViewModel에 저장한 후 Activity에서 불러와 logic에 활용될 수 있게하였습니다. 
<br/>   

<p align="center"><img src="https://user-images.githubusercontent.com/89737271/216815903-608cd99b-7a48-451d-806f-86d08ed46aa5.PNG"</p>

- `EndRunActivity`에서는 이전 Activity들로부터 intent로 받아온 data들을 화면에 보여줍니다. 이때 intent로 주고받는 data들은 별도의 data class로 관리함으로써 코드 가독성을 높이고자 하였습니다. 

- (미구현-23.02.05) 저장하기 버튼을 누르면 서버로 런닝 기록이 POST 됩니다. 

 </div>
</details>  

 <details>
 <summary> 보관함 </summary>
 <div markdown="1">
<br/>
  
<p align="center"><img src="https://user-images.githubusercontent.com/89737271/216816737-e67beeb1-fb7e-40f1-b056-ef78ad439b95.PNG"</p>

- TabLayout의 각 Tab 터치 시 화면이 바뀌어야 해서 `StorageMainFragment` 하위에 `StorageMyDrawFragment`, `StorageScrapFragment`를 만들어서 뷰 구성을 하였습니다.

- `Header`에 `machineID`를 넣어서 서버 통신을 하고 `DrawActivity`에서 만들었던 코스들의 data를 받아와 리사이클러뷰로 보여주었습니다.

- 리사이클러뷰 item 터치 시 `MyDrawDetailActivity`로 이동하며 터치된 item의 `courseID`를 intent로 전달합니다.

- `MyDrawDetailActivity`에서는 `StorageMyDrawFragment`에서 받아온 `courseID`를 레트로핏 서비스에 `Path`로 넣어서 서버 통신을 하고 해당 `courseID`에 대응되는 data를 받아와서 화면에 보여줍니다.
   
- 시작하기 버튼 터치 시 `MyDrawDetailActivity`에서 `RunActivity`로 화면 전환되며 이때 RunActivity에서 필요로 하는 data는 `DetailToRunData`라는 data class를 만들어서 전달합니다.  
<br/>   
   
<p align="center"><img src="https://user-images.githubusercontent.com/89737271/216817424-3736f185-ff2a-4cfd-98fc-0f0452eadc89.PNG"</p>

- 스크랩 Tab 터치 시 `StorageMyDrawFragment`에서 `StorageScrapFragment`로 replace됩니다.

- `StorageMyDrawFragment`에서는 코스 발견에서 스크랩했던 코스 목록을 서버 통신으로 받아와서 리사이클러뷰로 보여줍니다.
      
- (미구현-23.02.05) 리사이클러뷰 item 터치 시 스크랩 상세 페이지로 이동합니다.
- (미구현-23.02.05) 하트 버튼을 누르면 사용자의 해당 스크랩 data를 삭제합니다.   
   
 </div>
</details>
   
 </div>
</details>
<br/>  
<details>
 <summary> 🌳 지훈 </summary>
 <div markdown="1">
  <br/> 
<details>
 <summary> 코스 발견 </summary>
 <div markdown="1">
 
   1. **코스 발견**

 </div>
</details>  
   
 <details>
  <summary> 마이페이지 </summary>
  <div markdown="1">
 
   2. **마이페이지**

 </div>
</details>  

 </div>
</details>  
<br/>

## 어려웠던 부분과 느낀점

<details>
 <summary> 🌱 우남 </summary>
 <div markdown="1">
<br/>

- '지도에 러닝 코스를 그린다'는 미션은 뚜렷했는데 '어떻게' 그릴지는 논의된 바가 없어서 막막했습니다. 그리고 해커톤이어서 마감 기간이 굉장히 짧았는데 안 해본 기능을 공부해가면서 구현했어야 해서 부담이 됐습니다. 이에 시중에 있는 러닝 어플을 모두 깔아서 '그리기' 기능이 있는지, 있다면 어떻게 구현했는지 래퍼런스를 조사했습니다. 또한 참고 자료는 충분한지, API 가이드는 자세히 설명돼있는지도 꼼꼼히 리서치했습니다. 이 덕분에 마감 기간 내에 핵심 기능을 잘 구현해낼 수 있었습니다. 무턱대고 기획, 디자인부터 했다가 나중에 구현을 못해서 프로젝트를 엎었던 경험이 있는 저는 이 경험으로 사전 리서치의 중요성을 다시 한 번 느꼈습니다.
<br/> 
  
- git branch 전략을 짤 때 merge conflict 이슈가 안 나는 것에만 집중했습니다. 처음부터 폴더링 및 코드 컨벤션을 잘 하면 conflict 이슈가 없을 것이라고 생각했습니다. 따라서 번거롭게 매번 PR을 올리지 말고 각자 개인 브랜치를 파서 작업을 다 끝마친 후에 합치기로 하였습니다. 그런데 몇가지 문제가 있었습니다. 예상대로 conflict 이슈는 크지 않았지만 drawble처럼 공유되는 자원은 한 명만 만들어도 되는데 PR 자체를 안 올리다보니 서로 상황 공유가 안 돼서 두 명이 만들게 되는 등 비효율적인 상황이 생겼습니다. 그리고 logic을 짤 때 logic에 쓰이는 data를 팀원이 작업한 페이지에서도 받아올 수 있다는 걸 고려 못하고 제가 작업한 Activity에서 받아온 data만 쓰이게 코드를 작성했습니다. 이 때문에 팀원이 작업한 페이지와 연결이 안 되는 상황도 생겼습니다. 이 부분은 제 실수였습니다. 만약 feature 단위로 branch를 파서 서로 PR 올리고 PULL 받고 하는 과정을 따랐으면 이러한 저의 실수를 좀 더 빨리 파악할 수 있었을 거란 생각에 아쉬움이 남았습니다. 저는 이 경험으로 git branch 전략을 짤 때 conflict 외의 이슈들도 고려해야 한다는 걸 배웠습니다.
<br/>
  
- 멀티파트 통신을 할 때 로그를 확인해보면 분명히 이미지를 RequestBody 타입으로 잘 변환했고 서버에 request도 잘 보낸 것 같은데 서버에서 계속 500 에러 메세지가 날아왔습니다. 500이라고 뜨니 클라이언트의 문제는 아니라고 생각했는데 Postman으로 테스트해보니 서버 측엔 문제가 없었습니다. 클라이언트 측에서 특별한 문제점을 못찾고 있었는데 서버 개발 팀원이 분기 처리를 안 해놔서 상황에 맞는 에러 메세지가 안 날아가는 것일 수도 있다며 직접 확인해주겠다고 하였습니다. 서버 측에서 확인해보니 request가 잘 날아오기는 하는데 클라이언트에서 보내주는 이미지 형식이 서버에서 처리하는 형식과 달라서 문제가 되는 것 같다는 의견을 주었습니다. 이에 fileProvider를 사용하여 file:// 형태의 Uri를 content:// 로 바꾸어주었더니 문제가 해결되었습니다. 서버 개발 팀원으로부터 도움을 받았고 fileProvider를 활용해보자는 의견도 같은 안드로이드 팀원이 내주었습니다. 저에게 이번 이슈는 여러 사람들의 도움으로 문제를 해결할 수 있었던 기억에 남는 경험이었습니다.       
   
 </div>
</details>
<br/>  
<details>
 <summary> 🌳 지훈 </summary>
 <div markdown="1">
 

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
