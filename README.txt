Simple Java Issue Tracker Backend
=================================

This is a simplified Java backend made to match the existing HTML/CSS dashboard.
It is intentionally small for a university software engineering project.

How to run:
1. Open terminal in this folder.
2. Compile:
   javac -encoding UTF-8 -d out issuetracker/*.java view/*.java
3. Run:
   java -cp out issuetracker.Main
4. Open browser:
   http://localhost:8080/

Included features:
- Java backend using built-in HttpServer
- Issue browsing and filtering
- User management
- Create issue
- Add user
- Assign issue
- Add comment
- Change issue status
- View basic statistics
- Recommend assignee using simple history-based scoring

Project structure:
- src/issuetracker/User.java
- src/issuetracker/Issue.java
- src/issuetracker/Comment.java
- src/issuetracker/IssueTrackerService.java
- src/issuetracker/Main.java
- src/issuetracker/Json.java
- public/index.html
- public/style.css
- public/app.js

Design explanation:
The UI is separated from the backend. The browser communicates with Java through API endpoints.
The service class contains application logic, while model classes store data.
This is simpler than a full enterprise architecture, but still supports MVC-style separation for the project report.

fix: 실제 폴더 구조에 맞게 컴파일 명령어 경로 수정
