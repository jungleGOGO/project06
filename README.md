## 📍Chunjae Project 05
천재교육 풀스택 JAVA 과정 2기 6차 팀 프로젝트 _TCODING 홈페이지
<br><br>

## 🖥️ 프로젝트 : TCODING (2023.11.30 - 2023.12.27)
<p align="center"><img alt="list" src="https://github.com/jungleGOGO/project06/assets/138674233/c2606da0-e205-4c1b-979e-27a97b0ffee3"></p>
<br>
<p align="center"><img alt="list" src=""></p>
<br><br>

### 🧑‍🤝‍🧑 멤버구성
<p align="center"><img alt="list" src="https://github.com/jungleGOGO/project06/assets/138674233/0ec80f6c-6341-41bf-9f6e-765dc8d58d0c"></p>
<br><br>

### ⚙️ 개발 환경
<div align=center> 
<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/css-0769AD?style=for-the-badge&logo=css&logoColor=white">
<img src="https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white">
<img src="https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white">
<br>
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
<br>
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/fontawesome-339AF0?style=for-the-badge&logo=fontawesome&logoColor=white">
<br>
<img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white">
<img src="https://img.shields.io/badge/apache tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=white">
</div>
<br><br>

## ⚒프로젝트 설계

### 📂DATABASE - 테이블 ERD
<p align="center"><img alt="usecasediagram1" src=""></p>
<br><br>

### 📂DATABASE - 테이블 구현
#### ROLE
<p align="center"><img alt="role" src="https://github.com/jungleGOGO/project05/assets/138674233/592cbffb-6b54-4f19-806e-c3ad35e2dd6c"></p>

#### USER
<p align="center"><img alt="user" src="https://github.com/jungleGOGO/project05/assets/138674233/b549dd7b-8436-49a7-96e2-c3d119d1ca89"></p>


<br><br>

### 📂CLASS DIAGRAM

<br><br>

### 📂시퀀스 다이어그램 (Sequence Diagram)

#### 관리자
<p align="center"><img alt="시퀀스다이아그램1" src="https://github.com/jungleGOGO/project05/assets/138674233/f165e11f-2686-4908-a960-003fff962211"></p>

<br><br>

# 📎기능 구현
## 🗂 메인 페이지
<br>

![메인페이지](https://github.com/jungleGOGO/project05/assets/138674233/d083ff83-7aff-4deb-b5e7-3b0b2fc61233)
<br><br>

## 🗂 회원 기능
### 1. 로그인
- DB값 검증
- Spring Security 통한 로그인 인증 처리 구현
- 로그아웃
  <br>
  <br><br>

### 2. 회원가입
- 주소 API 연동
- ID 중복 체크
- 비밀번호 유효성 검사
  <br>
  <br><br>

### 4. 비밀번호 찾기
- 가입한 이메일로 임시 비밀번호 발급
- 발급된 비밀번호 암호화 되어 DB에 저장
 <br>
 
![join](https://github.com/jungleGOGO/project05/assets/138674233/5b4415ca-f87a-4b13-9d53-b1eb3e8b730a)
<br><br>

## 🗂 팝니다/삽니다 기능
### 1. 팝니다
- 비회원 : 열람 가능
- 회원 : 글쓰기/ 채팅 / 신고 / 찜하기 가능
- 네이버 지도 API 적용하여 상세주소 구현
  <br>
  <br><br>
  
### 2. 삽니다
- 비회원 : 열람 가능
- 회원 : 글쓰기/ 채팅 / 신고 / 찜하기 가능
- 네이버 도서 API 적용하여 목록 구현
  <br>
  <br><br>

### 3. 채팅
- stomp.js / socket.js 활용하여 구현
- 1 대 1 채팅 
- DB에 채팅 내용 실시간 반영
  <br>
  <br><br>



  
## 🗂 공지사항
- 관리자만 공지사항 글 쓰기, 수정, 삭제 가능하도록 구현
- 회원은 공지사항 글 상세 보기만 가능하도록 구현
  <br>
  <br><br>


## 🗂 관리자페이지
- 회원관리
- 커뮤니티 관리 : 공지사항 CRUD / 미답변 질문글 답변하기
- AJAX를 활요하여 신고된 회원 상태 변경
- 신고된 글 삭제 가능 

![관리자페이지](https://github.com/jungleGOGO/project05/assets/138674233/b7ecd584-4bdf-43d3-ac60-f9c626a90f2c)
