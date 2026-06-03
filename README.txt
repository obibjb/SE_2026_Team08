Simple Java Issue Tracker System (Team 08)
=========================================

본 프로젝트는 소프트웨어공학 01분반 08조의 텀 프로젝트 산출물로, 
동일한 백엔드 코어 서비스와 영속성 데이터셋을 공유하는 [Java Swing GUI 클라이언트] 및 
[Web UI 클라이언트] 연동형 이슈 트래커 시스템입니다.

How to run
----------
1. 본 프로젝트의 루트 폴더(issuetracker 및 view 폴더가 있는 위치)에서 터미널을 엽니다.

2. 기존의 환경 충돌 유발 요인인 바이너리(.class) 파일을 일괄 청소합니다:
   rm -rf issuetracker/*.class view/*.class

3. 인코딩 규격을 UTF-8로 지정하여 전체 패키지 소스코드를 동시 컴파일합니다:
   javac -encoding UTF-8 issuetracker/*.java view/*.java

4. 크로스 플랫폼 독립적 컴파일 환경에서 애플리케이션을 구동합니다:
   - [추천] 자바 스윙 데스크톱 GUI 프레임 전격 구동:
     java -Dfile.encoding=UTF-8 view.AppMainSwing
   
   - 내장 HttpServer 기반 웹 대시보드 서버 단독 구동:
     java -Dfile.encoding=UTF-8 issuetracker.Main (구동 후 http://localhost:8080/ 접속)

Included features
-----------------
- 단일 백엔드 코어 서비스(IssueTrackerService)를 통한 비즈니스 로직 집중화 (Controller 패턴)
- Java 객체 직렬화 메커니즘(.ser 바이너리 파일)을 활용한 안전한 영속 계층 구조 구현
- 사용자 권한 식별 및 다중 클라이언트(Swing GUI / Web UI) 간 실시간 데이터 상호 동기화
- 결함 등록(UC-1) 시 과거 해결 이력의 단어 빈도 분석 기반 담당자 자동 추천 알고리즘 내장(UC-4)
- 결함 조건별 실시간 복합 필터링(UC-2), 상세 및 댓글 조회(UC-6), 상태 변경(UC-3) 기능
- 시스템 주요 성능 및 해결률 요약 데이터 산출 기능 (statisticsJson)

Project structure
-----------------
- issuetracker/User.java                - 사용자 도메인 모델 엔티티
- issuetracker/Issue.java               - 결함 데이터 및 댓글 캡슐화 정보 담당 객체
- issuetracker/Comment.java             - 결함 하위 종속 피드백 댓글 엔티티
- issuetracker/IssueTrackerService.java - 비즈니스 로직 및 파일 입출력 제어 코어 (싱글톤)
- issuetracker/Main.java                - 웹 서버 구동 메인 엔트리
- issuetracker/Json.java                - 웹 통신용 제이슨 포맷 데이터 가공 유틸리티
- view/AppMainSwing.java                - 자바 스윙 기반 데스크톱 메인 GUI 화면 레이어
- public/index.html                     - 웹 인터페이스 프론트엔드 대시보드 화면
- public/style.css                      - 웹 UI 스타일시트
- public/app.js                         - 비동기 API 통신 및 웹 화면 렌더링 스크립트

Design explanation
------------------
본 시스템은 프론트엔드 UI 레이어와 백엔드 비즈니스 로직을 철저히 분리하여 설계되었습니다. 
독립된 다중 클라이언트(Swing, Web) 환경이 단일 컨트롤러인 IssueTrackerService와 유기적으로 소통하며, 
SOLID의 단일 책임 원칙(SRP)과 낮은 결합도 구조를 준수하여 향후 인터페이스 확장 시에도 
백엔드 코어의 무결성이 흔들리지 않도록 정석적인 MVC 스타일의 구조적 separation을 보장합니다.
