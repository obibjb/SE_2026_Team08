package issuetracker;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class IssueTrackerService {
    private List<Issue> issues = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int nextIssueNumber = 9;
    private static final String FILE_PATH = "data.ser"; // 데이터가 영속 저장될 바이너리 파일 경로

    public IssueTrackerService() {
        // 백엔드가 켜질 때 백업 파일이 있으면 데이터 복구, 없으면 초기값(Seed) 로드 후 자동 저장
        if (!loadData()) {
            seedUsers();
            seedIssues();
            saveData();
        }
    }

    // [데이터 파일 내보내기 (직렬화)] 데이터에 변경이 생길 때마다 자동 호출
    private synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(issues);
            oos.writeObject(users);
            oos.writeInt(nextIssueNumber);
        } catch (IOException e) {
            System.err.println("데이터 영속화 저장 실패: " + e.getMessage());
        }
    }

    // [데이터 파일 불러오기 (역직렬화)]
    @SuppressWarnings("unchecked")
    private boolean loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return false;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            issues = (List<Issue>) ois.readObject();
            users = (List<User>) ois.readObject();
            nextIssueNumber = ois.readInt();
            return true;
        } catch (Exception e) {
            System.err.println("데이터 로드 실패 (초기화 진행): " + e.getMessage());
            return false;
        }
    }

    private void seedUsers() {
        users.add(new User("dev1", "김철수 (Dev1)", "dev1@company.com", "Dev"));
        users.add(new User("dev2", "이영희 (Dev2)", "dev2@company.com", "Dev"));
        users.add(new User("dev3", "박민수 (Dev3)", "dev3@company.com", "Dev"));
        users.add(new User("dev4", "정수진 (Dev4)", "dev4@company.com", "Dev"));
        users.add(new User("dev5", "최동욱 (Dev5)", "dev5@company.com", "Dev"));
        users.add(new User("pl1", "강태영 (PL1)", "pl1@company.com", "PL"));
        users.add(new User("pl2", "윤서현 (PL2)", "pl2@company.com", "PL"));
        users.add(new User("tester1", "임지훈 (Tester1)", "tester1@company.com", "Tester"));
        users.add(new User("tester2", "홍유진 (Tester2)", "tester2@company.com", "Tester"));
        users.add(new User("admin1", "서관리 (Admin)", "admin@company.com", "Admin"));
    }

    private void seedIssues() {
        issues.add(new Issue("ISSUE-001", "로그인 화면에서 비밀번호 찾기 버튼이 작동하지 않음", "Password reset button does not work.", "Blocker", "New", "Unassigned", "tester1", "", "5/27/2026"));
        issues.add(new Issue("ISSUE-002", "대시보드 차트 렌더링 성능 저하", "Dashboard chart rendering is slow.", "Critical", "Assigned", "dev2", "pl1", "", "5/26/2026"));
        issues.add(new Issue("ISSUE-003", "알림 이메일이 중복 발송됨", "Notification emails are sent twice.", "Major", "Fixed", "dev5", "tester2", "dev5", "5/25/2026"));
        issues.add(new Issue("ISSUE-004", "모바일 화면에서 네비게이션 메뉴가 잘림", "Mobile navigation menu is cut off.", "Minor", "Resolved", "dev1", "tester1", "dev1", "5/24/2026"));
        issues.add(new Issue("ISSUE-005", "검색 기능에서 특수문자 입력 시 오류 발생", "Search fails for special characters.", "Major", "Assigned", "dev3", "tester2", "", "5/27/2026"));
        issues.add(new Issue("ISSUE-006", "프로필 이미지 업로드 제한 없음", "Profile image upload has no validation.", "Minor", "New", "Unassigned", "pl2", "", "5/27/2026"));
        issues.add(new Issue("ISSUE-007", "다국어 지원 시 일부 텍스트가 번역되지 않음", "Some text is not translated.", "Trivial", "Closed", "dev4", "tester1", "dev4", "5/20/2026"));
        issues.add(new Issue("ISSUE-008", "데이터 익스포트 기능이 CSV 형식을 지원하지 않음", "Export does not support CSV.", "Minor", "Assigned", "dev2", "pl1", "", "5/26/2026"));
    }

    public List<Issue> getIssues(String status, String priority, String assignee, String keyword) {
        return issues.stream().filter(i ->
                matches(status, i.status) && matches(priority, i.priority) && matches(assignee, i.assignee) &&
                        (keyword == null || keyword.isBlank() || i.id.toLowerCase().contains(keyword.toLowerCase()) || i.title.toLowerCase().contains(keyword.toLowerCase()))
        ).collect(Collectors.toList());
    }

    private boolean matches(String filter, String value) {
        return filter == null || filter.equalsIgnoreCase("all") || filter.equalsIgnoreCase(value);
    }

    public List<User> getUsers() { return users; }

    public Issue createIssue(String title, String description, String priority, String reporter) {
        String id = String.format("ISSUE-%03d", nextIssueNumber++);
        Issue issue = new Issue(id, title, description, defaultValue(priority, "Major"), "New", "Unassigned", defaultValue(reporter, "tester1"), "", LocalDate.now().toString());
        issues.add(issue);
        saveData(); // 상태 변경 시 파일 저장 트리거
        return issue;
    }

    public User createUser(String id, String name, String email, String role) {
        User user = new User(id, name, email, role);
        users.add(user);
        saveData(); // 유저 생성 시 파일 저장 트리거
        return user;
    }

    public Issue assignIssue(String issueId, String devId, String plId, String comment) {
        Issue issue = findIssue(issueId);
        issue.assignee = devId;
        issue.status = "Assigned";
        if (comment != null && !comment.isBlank()) addComment(issueId, plId, comment);
        saveData(); // 담당자 배정 시 파일 저장 트리거
        return issue;
    }

    public Issue changeStatus(String issueId, String status, String userId, String comment) {
        Issue issue = findIssue(issueId);
        issue.status = status;
        if ("Fixed".equalsIgnoreCase(status)) issue.fixer = userId;
        if (comment != null && !comment.isBlank()) addComment(issueId, userId, comment);
        saveData(); // 상태 변경 시 파일 저장 트리거
        return issue;
    }

    public Issue addComment(String issueId, String authorId, String content) {
        Issue issue = findIssue(issueId);
        issue.comments.add(new Comment(authorId, content, java.time.LocalDateTime.now().toString()));
        saveData(); // 댓글 추가 시 파일 저장 트리거
        return issue;
    }

    public String recommendAssignee(String issueId) {
        Issue target = findIssue(issueId);
        Map<String, Integer> score = new HashMap<>();
        for (Issue old : issues) {
            if (old.fixer == null || old.fixer.isBlank()) continue;
            int s = 0;
            for (String word : target.title.split(" ")) {
                if (word.length() > 1 && old.title.contains(word)) s += 2;
            }
            if (target.priority.equals(old.priority)) s += 1;
            if (old.status.equals("Resolved") || old.status.equals("Closed") || old.status.equals("Fixed")) s += 1;
            score.put(old.fixer, score.getOrDefault(old.fixer, 0) + Math.max(s, 1));
        }
        return score.entrySet().stream()
                .sorted((a,b) -> b.getValue() - a.getValue())
                .limit(3)
                .map(e -> e.getKey() + "(" + e.getValue() + ")")
                .collect(Collectors.joining(", "));
    }

    public String statisticsJson() {
        long total = issues.size();
        long open = issues.stream().filter(i -> !i.status.equals("Closed") && !i.status.equals("Resolved")).count();
        long resolved = issues.stream().filter(i -> i.status.equals("Resolved") || i.status.equals("Closed")).count();
        long unassigned = issues.stream().filter(i -> i.assignee.equals("Unassigned")).count();
        return "{\"total\":" + total + ",\"open\":" + open + ",\"resolved\":" + resolved + ",\"unassigned\":" + unassigned + ",\"resolutionRate\":" + (total == 0 ? 0 : (resolved * 100 / total)) + "}";
    }

    private Issue findIssue(String id) {
        return issues.stream().filter(i -> i.id.equals(id)).findFirst().orElseThrow(() -> new RuntimeException("Issue not found: " + id));
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
    private static IssueTrackerService instance;

    public static IssueTrackerService getInstance() {
        if (instance == null) {
            instance = new IssueTrackerService();
        }
        return instance;
    }
}
