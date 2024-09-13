# TimeCapsule
## 프로젝트 소개

- 가족의 역사를 기록하고 싶은 분<br>
- 세대 간의 추억을 간직하고 싶은 분<br>
- 부모님이나 조부모님의 모습을 생생하게 기억하고 싶은 분

**TimeCapsule** 서비스는 가족 간 또는 세대 간의 이야기를 기록하고 보존하는 것을 목표로 기획되었습니다.<br> 
사용자는 가족의 역사를 웹페이지 형태로 저장하여, 자손 및 후손들이 자신들의 생전 모습을 볼 수 있을 뿐만 아니라, 목소리도 들을 수 있습니다.<br>
이를 통해 소중한 가족의 추억이 세월이 흘러도 생생하게 유지되며, 다음 세대에 전해질 수 있습니다.

또한, 이 서비스는 단순히 사진과 목소리뿐만 아니라, 가족 구성원들의 생애 이야기를 텍스트로 기록할 수 있는 기능도 제공합니다.<br>
이를 통해 부모님이나 조부모님의 일대기를 세세하게 남길 수 있으며, 후손들은 이 기록을 통해 자신들의 뿌리를 더욱 깊이 이해하고, 그 의미를 되새길 수 있습니다.

언제든지 원할 때마다 이 기록들을 되돌아보며, 가족의 유대감을 강화하고, 세대 간의 소통을 돕는 소중한 자산이 될 것입니다.

<br>

## 프로젝트 개요

- 프로젝트 이름 : TimeCapsule
- 프로젝트 팀명 : A-HI
- 프로젝트 기간 : 2024.08.21 ~ 2024.09.13

<br>

## 프로젝트 목표

- 가족의 **역사와 추억**을 세대 간에 전하는 커뮤니티
- **생생한 기억**과 **따뜻한 목소리**를 보존하는 커뮤니티
- 미래 세대에게 **소중한 유산**을 전달하는 커뮤니티

<br>

## 팀 소개

<br>

|     <img src="src/main/resources/static/docs/hjl-profile-img.png" width="310" height="250" alt="이현준">      |               <img src="src/main/resources/static/docs/smk-profile-img.png" width="310" height="250" alt="김수민">               |                           <img src="src/main/resources/static/docs/hskim-profile-img.png" width="310" height="250" alt="김형섭">                            |
|:----------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                                                  **이현준**                                                   |                                                            **김수민**                                                            |                                                                         **김형섭**                                                                          |
|                                                     팀장                                                     |                                                              팀원                                                               |                                                                            팀원                                                                            |
|                                         ERD 설계/시스템 아키텍처 작성/회의록 작성                                          |                                                    화면 설계/PPT 작성/노션 템플릿 작성                                                     |                                                                     화면 설계/README 작성                                                                      |
| **Backend** <br>- API 연동<br>- 스토리 생성 및 저장<br>- CI/CD 구축 <br> **Frontend** <br>- 스토리 생성 페이지<br>- 스토리 작성 페이지 | **Backend** <br>- 공지사항 CRUD<br>- 공지사항 단위 테스트 코드 작성<br> **Frontend** <br>- 공지사항 목록 페이지<br>- 공지사항 상세 페이지<br>- 공지사항 작성/수정/삭제 페이지 | **Backend** <br>- OAuth2.0 로그인(구글/카카오)<br>- 회원정보수정<br>- 회원탈퇴<br>- 임시비밀번호발급<br> **Frontend** <br>- 메인페이지(로그인 전/후)<br>- 회원정보수정/회원탈퇴 페이지<br>- 임시비밀번호 발급 페이지 |

|           <img src="src/main/resources/static/docs/yrp-profile-img.png" width="310" height="250" alt="박예린">            | <img src="src/main/resources/static/docs/dhl-profile-img.png" width="310" height="250" alt="이동현"> |                                    <img src="src/main/resources/static/docs/sjl-profile-img.png" width="310" height="250" alt="이소정">                                    |
|:----------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                                                        **박예린**                                                         |                                              **이동현**                                              |                                                                                 **이소정**                                                                                 |
|                                                           팀원                                                           |                                                팀원                                                 |                                                                                   팀원                                                                                    |
|                                                   ERD 설계/시스템 아키텍처 작성                                                   |                                               화면 설계                                               |                                                                           ERD 설계/시스템 아키텍처 작성                                                                            |
| **Backend** <br>- 스토리 조회/수정/삭제<br>- 스토리 검색<br>- 커뮤니티 조회<br> **Frontend** <br>- 스토리/커뮤니티 목록 페이지<br>- 스토리/커뮤니티 상세 조회 페이지 |            **Backend** <br>- 댓글 CRUD <br> **Frontend** <br>- 마이스토리 & 커뮤니티 페이지 댓글 CRUD             | **Backend** <br>- 회원가입<br>- 로그인<br>- 로그아웃<br>- Spring Security<br>- JWT 토큰 관리<br>- Redis<br>- 회원 Repository 단위 테스트 코드 작성  <br> **Frontend** <br>- 회원가입 페이지<br>- 로그인 페이지 |

## 프로젝트 배포
서버 주소:  http://ec2-3-36-56-149.ap-northeast-2.compute.amazonaws.com:8080/

### 관리자 계정  
ID: test123  
PW: test1234!!

### 일반 계정  
ID: test  
PW: a12345

<br>

## 기술 스택

### FrontEnd
<img src="https://img.shields.io/badge/THYMELEAF-005F0F?style=for-the-badge&logo=THYMELEAF&logoColor=white" alt="THYMELEAF"> <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white" alt="HTML5">
<img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=white" alt="CSS3">
<img src="https://img.shields.io/badge/JAVASCRIPT-F7DF1E?style=for-the-badge&logo=JAVASCRIPT&logoColor=white" alt="JAVASCRIPT">

### BackEnd
<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=JAVA&logoColor=white" alt="JAVA"> <img src="https://img.shields.io/badge/SPRING BOOT-6DB33F?style=for-the-badge&logo=SPRING BOOT&logoColor=white" alt="SPRING BOOT">
<img src="https://img.shields.io/badge/SPRING SECURITY-6DB33F?style=for-the-badge&logo=SPRING SECURITY&logoColor=white" alt="SPRING SECURITY">
<img src="https://img.shields.io/badge/SPRING DATA JPA-6DB33F?style=for-the-badge&logo=SPRING DATA JPA&logoColor=white" alt="SPRING DATA JPA">
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON WEB TOKENS&logoColor=white" alt="JWT">
<img src="https://img.shields.io/badge/OAUTH2.0-FAF4C0?style=for-the-badge&logo=OAUTH2.0&logoColor=white" alt="OAUTH2.0">
<img src="https://img.shields.io/badge/REDIS-FF4438?style=for-the-badge&logo=REDIS&logoColor=white" alt="REDIS">

### Database
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MYSQL">

### Deployment
<img src="https://img.shields.io/badge/AMAZON EC2-FF9900?style=for-the-badge&logo=AMAZON EC2&logoColor=white" alt="AMAZON EC2"> <img src="https://img.shields.io/badge/AMAZON RDS-527FFF?style=for-the-badge&logo=AMAZON RDS&logoColor=white" alt="AMAZON RDS">
<img src="https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=DOCKER&logoColor=white" alt="DOCKER">

### CI/CD
<img src="https://img.shields.io/badge/GITHUB ACTIONS-2088FF?style=for-the-badge&logo=GITHUB ACTIONS&logoColor=white" alt="GITHUB ACTIONS">

### External API
<img src="https://img.shields.io/badge/STT API-6B66FF?style=for-the-badge&logo=STT API&logoColor=white" alt="STT API"> <img src="https://img.shields.io/badge/Alan AI-A6A6A6?style=for-the-badge&logo=Alan AI&logoColor=white" alt="Alan AI">

### Collaboration Tools
<img src="https://img.shields.io/badge/GITHUB-181717?style=for-the-badge&logo=GITHUB&logoColor=white" alt="GITHUB"> <img src="https://img.shields.io/badge/NOTION-000000?style=for-the-badge&logo=NOTION&logoColor=white" alt="NOTION">
<img src="https://img.shields.io/badge/FIGMA-F24E1E?style=for-the-badge&logo=FIGMA&logoColor=white" alt="FIGMA">
<img src="https://img.shields.io/badge/dbdiagram.io-F08705?style=for-the-badge&logo=dbdiagram.io&logoColor=white" alt="dbdiagram.io">

<br>

## 개발 일정

<br>
<img src="src/main/resources/static/docs/project-wbs.png" width="1100" height="700" alt="요구사항 정의서 마인드 맵">

<br>

## 요구사항 정의서

<br>
<img src="src/main/resources/static/docs/requirements-mind-map.jpg" width="1100" height="700" alt="요구사항 정의서 마인드 맵">

<br>

## ERD

<br>
<img src="src/main/resources/static/docs/database-erd.png" width="1100" height="700" alt="데이터베이스 ERD">

<br>

## 시스템 아키텍처

<br>
<img src="src/main/resources/static/docs/system-architecture.png" width="1100" height="700" alt="시스템 아키텍처">

<br>

## 프로젝트 구조

### BackEnd

```
📁 src
├── 📁 main
│   ├── 📁 java
│   │   ├── 📁 api
│   │   │   ├── 📄 AlanAPI.java
│   │   │   └── 📄 STTAPI.java
│   │   ├── 📁 config
│   │   │   ├── 📄 AuthorityUtils.java
│   │   │   ├── 📄 JwtAuthenticationEntryPoint.java
│   │   │   ├── 📄 JwtAuthenticationFilter.java
│   │   │   ├── 📄 JwtTokenProvider.java
│   │   │   ├── 📄 PasswordEncoderConfig.java
│   │   │   ├── 📄 RedisService.java
│   │   │   ├── 📄 RegExp.java
│   │   │   ├── 📄 SecurityConfig.java
│   │   │   └── 📄 UserDetailService.java
│   │   ├── 📁 controller
│   │   │   ├── 📄 ApiController.java
│   │   │   ├── 📄 CommentController.java
│   │   │   ├── 📄 MainController.java
│   │   │   ├── 📄 NoticeController.java
│   │   │   ├── 📄 StoryController.java
│   │   │   └── 📄 UserController.java
│   │   ├── 📁 dto
│   │   │   ├── 📁 notice
│   │   │   │   ├── 📁 request
│   │   │   │   │   ├── 📄 NoticeCreateDTO.java
│   │   │   │   │   └── 📄 NoticeUpdateDTO.java
│   │   │   │   └── 📁 response
│   │   │   │       ├── 📄 NoticeDetailDTO.java
│   │   │   │       └── 📄 NoticeListDTO.java
│   │   │   ├── 📁 request
│   │   │   │   ├── 📄 StoryContentDTO.java
│   │   │   │   └── 📄 StoryOptionDTO.java
│   │   │   ├── 📄 ApiResponse.java
│   │   │   ├── 📄 CommentDTO.java
│   │   │   ├── 📄 FindImagesResponseDTO.java
│   │   │   ├── 📄 FindSharedResponseDTO.java
│   │   │   ├── 📄 FindStoryResponseDTO.java
│   │   │   ├── 📄 ImageDTO.java
│   │   │   ├── 📄 NoticeDTO.java
│   │   │   ├── 📄 StoryDTO.java
│   │   │   ├── 📄 StoryShareDTO.java
│   │   │   ├── 📄 StoryUserResponseDTO.java
│   │   │   ├── 📄 TemporayPasswordDTO.java
│   │   │   ├── 📄 TokenExchangeRequest.java
│   │   │   ├── 📄 TokenResponse.java
│   │   │   ├── 📄 UpdateStoryRequestDTO.java
│   │   │   ├── 📄 UserDTO.java
│   │   │   ├── 📄 UserLoginDTO.java
│   │   │   ├── 📄 UserSignUpDTO.java
│   │   │   ├── 📄 UserUpdateDTO.java
│   │   │   └── 📄 UserWithdrawalDTO.java
│   │   ├── 📁 entity
│   │   │   ├── 📄 BaseEntity.java
│   │   │   ├── 📄 Comment.java
│   │   │   ├── 📄 Image.java
│   │   │   ├── 📄 Notice.java
│   │   │   ├── 📄 Story.java
│   │   │   ├── 📄 StoryShare.java
│   │   │   └── 📄 User.java
│   │   ├── 📁 exception
│   │   │   ├── 📄 GlobalExceptionHandler.java
│   │   │   ├── 📄 NotFoundException.java
│   │   │   ├── 📄 NoticeNotFoundException.java
│   │   │   ├── 📄 StoryNotFoundException.java
│   │   │   └── 📄 UserNotFoundException.java
│   │   ├── 📁 oauth
│   │   │   ├── 📄 CustomOAuth2FailureHandler.java
│   │   │   ├── 📄 CustomOAuth2SuccessHandler.java
│   │   │   ├── 📄 CustomOAuth2User.java
│   │   │   ├── 📄 CustomOAuth2UserService.java
│   │   │   ├── 📄 EmailAlreadyExistsException.java
│   │   │   ├── 📄 GoogleOAuth2UserInfo.java
│   │   │   ├── 📄 KakaoOAuth2UserInfo.java
│   │   │   └── 📄 OAuth2UserInfo.java
│   │   ├── 📁 repository
│   │   │   ├── 📄 CommentRepository.java
│   │   │   ├── 📄 ImageRepository.java
│   │   │   ├── 📄 NoticeRepository.java
│   │   │   ├── 📄 StoryRepository.java
│   │   │   ├── 📄 StoryShareRepository.java
│   │   │   └── 📄 UserRepository.java
│   │   ├── 📁 service
│   │   │   ├── 📄 ApiService.java
│   │   │   ├── 📄 CommentService.java
│   │   │   ├── 📄 EmailService.java
│   │   │   ├── 📄 ImageService.java
│   │   │   ├── 📄 NoticeService.java
│   │   │   ├── 📄 StoryService.java
│   │   │   └── 📄 UserService.java
│   │   ├── 📁 status
│   │   ├── 📁 util
│   │   │   └── 📄 FileUtil.java
│   │   └── 📄 TimecapsuleApplication.java
│   └── 📁 resources
│       └── 📄 application.yml
├── 📁 test
│   └── 📁 java
│       ├── 📁 controller
│       │   └── 📄 NoticeControllerTest.java
│       ├── 📁 repository
│       │   └── 📄 NoticeRepositoryTest.java
│       ├── 📁 service
│       │   ├── 📄 NoticeServiceTest.java
│       │   └── 📄 UserServiceTest.java
│       ├── 📄 TestSecurityConfig.java
│       └── 📄 TimecapsuleApplicationTests.java
│
├── 📄 .gitignore
├── 📄 build.gradle
├── 📄 docker-compose.yml
├── 📄 Dockerfile
├── 📄 gradlew
├── 📄 gradlew.bat
├── 📄 README.md
└── 📄 settings.gradle

```
<br>

### FrontEnd

```
📁 src/main/resources
├── 📁 static
│   ├── 📁 css
│   │   ├── 📄 account.css
│   │   ├── 📄 common.css
│   │   ├── 📄 footer.css
│   │   ├── 📄 header.css
│   │   ├── 📄 login.css
│   │   ├── 📄 main.css
│   │   ├── 📄 modal.css
│   │   ├── 📄 notice.css
│   │   ├── 📄 sidebar.css
│   │   ├── 📄 signup.css
│   │   ├── 📄 story-created.css
│   │   ├── 📄 story-detail.css
│   │   ├── 📄 story-form.css
│   │   ├── 📄 story-update.css
│   │   └── 📄 storylist.css
│   ├── 📁 images
│   │   └── 📄 accounticon.png
│   ├── 📁 docs
│   │   └── 📄 community-detail.png
│   └── 📁 js
│       ├── 📄 accountManagement.js
│       ├── 📄 auth.js
│       ├── 📄 file-upload.js
│       ├── 📄 form-warning.js
│       ├── 📄 header.js
│       ├── 📄 image-upload.js
│       ├── 📄 login.js
│       ├── 📄 modalUtils.js
│       ├── 📄 notice-delete.js
│       ├── 📄 oauthLogin.js
│       ├── 📄 record.js
│       ├── 📄 resetPassword.js
│       ├── 📄 signup.js
│       ├── 📄 story-config.js
│       ├── 📄 story-modal.js
│       └── 📄 story.js
└── 📁 templates
    ├── 📁 account
    │   ├── 📄 account-management.html
    │   └── 📄 reset-password.html
    ├── 📁 fragments
    │   ├── 📄 footer.html
    │   ├── 📄 header.html
    │   ├── 📄 modal.html
    │   └── 📄 sidebar.html
    ├── 📁 notice
    │   ├── 📄 detail.html
    │   ├── 📄 edit.html
    │   ├── 📄 form.html
    │   └── 📄 list.html
    ├── 📄 index.html
    ├── 📄 login.html
    ├── 📄 main.html
    ├── 📄 signup.html
    ├── 📄 story-created.html
    ├── 📄 story-detail.html
    ├── 📄 story-form.html
    ├── 📄 story-update.html
    └── 📄 storylist.html

```

<br>

## 화면 설계

<br>

|                                                    메인페이지                                                    |                                                     메인페이지                                                     |
|:-----------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------:|
|      <img src="src/main/resources/static/docs/index.png" width="550" height="350" alt="메인페이지(로그인 전)">       |        <img src="src/main/resources/static/docs/main.png" width="550" height="350" alt="메인페이지(로그인 후)">        |
|                                                     로그인                                                     |                                                     회원가입                                                      |
|           <img src="src/main/resources/static/docs/login.png" width="550" height="350" alt="로그인">           |           <img src="src/main/resources/static/docs/signup.png" width="550" height="350" alt="회원가입">           |
|                                                   비밀번호 찾기                                                   |                                                     계정 관리                                                     |
|    <img src="src/main/resources/static/docs/reset-password.png" width="550" height="350" alt="비밀번호 찾기">     |       <img src="src/main/resources/static/docs/profile.png" width="550" height="350" alt="임시 비밀번호 발급">        |
|                                                공지사항 목록(관리자)                                                 |                                                 공지사항 상세(관리자)                                                  |
|   <img src="src/main/resources/static/docs/notice-list.png" width="550" height="400" alt="공지사항 목록(관리자)">    |   <img src="src/main/resources/static/docs/notice-detail.png" width="550" height="400" alt="공지사항 상세(관리자)">    |
|                                                공지사항 생성(관리자)                                                 |                                                 공지사항 수정(관리자)                                                  |
|  <img src="src/main/resources/static/docs/notice-create.png" width="550" height="400" alt="공지사항 생성(관리자)">   |   <img src="src/main/resources/static/docs/notice-update.png" width="550" height="400" alt="공지사항 수정(관리자)">    |
|                                                공지사항 목록(사용자)                                                 |                                                 공지사항 상세(사용자)                                                  |
| <img src="src/main/resources/static/docs/notice-list-user.png" width="550" height="400" alt="공지사항 목록(사용자)"> | <img src="src/main/resources/static/docs/notice-detail-user.png" width="550" height="400" alt="공지사항 상세(사용자)"> |
|                                                   스토리 생성                                                    |                                                    스토리 결과                                                     |
|      <img src="src/main/resources/static/docs/story-create.png" width="550" height="400" alt="스토리 생성">      |       <img src="src/main/resources/static/docs/story-result.png" width="550" height="400" alt="스토리 결과">       |
|                                                  마이스토리 목록                                                   |                                                   마이스토리 상세                                                    |
|    <img src="src/main/resources/static/docs/my-story-list.png" width="550" height="400" alt="마이스토리 목록">     |    <img src="src/main/resources/static/docs/my-story-detail.png" width="550" height="400" alt="마이스토리 상세">     |
|                                                  마이스토리 수정                                                   |                                                   공유스토리 목록                                                    |
|   <img src="src/main/resources/static/docs/my-story-update.png" width="550" height="400" alt="마이스토리 수정">    |    <img src="src/main/resources/static/docs/share-story-list.png" width="550" height="400" alt="공유스토리 목록">    |
|                                                  공유스토리 상세                                                   |                                                    커뮤니티 목록                                                    |
|  <img src="src/main/resources/static/docs/share-story-detail.png" width="550" height="400" alt="공유스토리 상세">  |     <img src="src/main/resources/static/docs/community-list.png" width="550" height="400" alt="커뮤니티 목록">      |
|                                                   커뮤니티 상세                                                   |                                                      비고                                                       |
|   <img src="src/main/resources/static/docs/community-detail.png" width="550" height="400" alt="커뮤니티 상세">    |                                                                                                               |

<br>

## API 명세서

#### User Management
| **NAME**               | **HTTP METHOD** | **URL**                         | **DESCRIPTION**                                | **AUTHORITY** |
|------------------------|-----------------|---------------------------------|------------------------------------------------|---------------|
| signUpPage             | GET             | `/signUpPage`                   | 회원가입 페이지로 이동                                   | 일반            |
| signUp                 | POST            | `/signUp`                       | 사용자 회원가입                                       | 일반            |
| login                  | GET             | `/login`                        | 로그인 폼 조회                                       | 일반, 관리자       |
| login                  | POST            | `/login`                        | 사용자 로그인(로컬 스토리지 방식)                            | 일반, 관리자       |
| logout                 | POST            | `/logout`                       | 사용자 로그아웃                                       | 일반, 관리자       |
| main                   | GET             | `/main`                         | 메인 페이지로 이동                                     | 일반, 관리자       |
| validateToken          | GET             | `/valid-token`                  | 페이지 접속 시 토큰 유효성 검사                             | 일반, 관리자       |
| checkDuplicate         | POST            | `/api/users/check-duplicate`    | 아이디, 이메일, 닉네임 중복 확인                            | 일반            |
| isPasswordMatching     | POST            | `/api/users/password-match`     | 비밀번호 매칭 확인                                     | 일반            |
| findPasswordPage       | GET             | `/users/password/find`          | 비밀번호 찾기 폼 조회                                   | 일반            |
| issueTemporaryPassword | POST            | `/users/password/temporary`     | 사용자가 입력한 아이디와 이메일을 검증하고, 임시 비밀번호를 발급하여 이메일로 전송 | 일반            |
| getUserInfo            | GET             | `/users/info`                   | 계정 관리 폼 조회                                     | 일반            |
| updateUser             | PUT             | `/users/update`                 | 사용자 정보 수정                                      | 일반            |
| deleteUser             | DELETE          | `/users/delete`                 | 사용자 회원 탈퇴                                      | 일반            |
| oauthLogin             | POST            | `/oauth/login`                  | OAuth2 로그인(구글, 카카오)                            | 일반            |
| checkDuplicateNickname | GET             | `/users/checkDuplicateNickname` | 닉네임 중복 체크                                      | 일반            |
| checkDuplicateEmail    | GET             | `/users/checkDuplicateEmail`    | 이메일 중복 체크                                      | 일반            |

<br>

#### Story
| **NAME**                 | **HTTP METHOD** | **URL**                  | **DESCRIPTION**         | **AUTHORITY** |
|--------------------------|-----------------|--------------------------|-------------------------|---------------|
| getStoryForm             | GET             | `/stories/form`          | 스토리 생성 폼 조회             | 일반            |
| uploadFile               | POST            | `/stories/form`          | 스토리 생성 폼에 파일 업로드        | 일반            |
| searchUsers              | GET             | `/stories/search`        | 공유하고자 할 유저 닉네임 검색       | 일반            |
| getCreateStoryForm       | GET             | `/stories/complete-form` | 스토리 생성 완료 후 작성폼 조회      | 일반            |
| createStory              | POST            | `/stories`               | 스토리 작성폼으로부터 최종으로 스토리 생성 | 일반            |
| getStoryList             | GET             | `/stories`               | 사용자가 작성한 전체 스토리 조회      | 일반            | 
| findStoryDetail          | GET             | `/stories/{id}`          | 특정 스토리 조회               | 일반            |
| getUpdateStoryForm       | GET             | `/stories/{id}/edit`     | 특정 스토리 수정폼 조회           | 일반            |
| updateStory              | PUT             | `/stories/{id}`          | 특정 스토리 수정               | 일반            |
| deleteStory              | DELETE          | `/stories/{id}`          | 특정 스토리 삭제               | 일반            |
| getSharedStoryList       | GET             | `/stories/shared`        | 사용자에게 공유된 전체 스토리 조회     | 일반            |
| findSharedStoryDetail    | GET             | `/stories/shared/{id}`   | 공유된 특정 스토리 조회           | 일반            |
| getCommunityStoryList    | GET             | `/community`             | 커뮤니티에 게시된 전체 스토리 조회     | 일반            |
| findCommunityStoryDetail | GET             | `/community/{id}`        | 커뮤니티에 게시된 특정 스토리 조회     | 일반            |
| downloadFile             | GET             | `/download`              | 인터뷰 파일 다운로드        | 일반             |

<br>

#### Notice
| **NAME**            | **HTTP METHOD** | **URL**              | **DESCRIPTION**         | **AUTHORITY** |
|---------------------|-----------------|----------------------|-------------------------|---------------|
| getNoticeList       | GET             | `/notices`           | 전체 공지사항 목록 조회 및 공지사항 검색 | 일반, 비회원, 관리자  |
| findNoticeDetail    | GET             | `/notices/{id}`      | 특정 공지사항 상세 조회           | 일반, 비회원, 관리자  |
| createNotice        | POST            | `/notices`           | 공지사항 생성                 | 관리자           |
| getCreateNoticeForm | GET             | `/notices/form`      | 공지사항 생성 폼 조회            | 관리자           |
| updateNotice        | POST            | `/notices/{id}`      | 공지사항 수정                 | 관리자           |
| getUpdateNoticeForm | GET             | `/notices/{id}/edit` | 공지사항 수정 폼 조회            | 관리자           |
| deleteNotice        | DELETE          | `/notices/{id}`      | 공지사항 삭제                 | 관리자           |

<br>

#### Comment
| **NAME**      | **HTTP METHOD** | **URL**          | **DESCRIPTION** | **AUTHORITY** |
|---------------|-----------------|------------------|-----------------|---------------|
| getComment    | GET             | `/comments`      | 댓글 조회           | 일반            |
| createComment | POST            | `/comments`      | 댓글 작성           | 일반            |
| updateComment | PUT             | `/comments/{id}` | 댓글 수정           | 일반            |
| deleteComment | DELETE          | `/comments/{id}` | 댓글 삭제           | 일반            |


