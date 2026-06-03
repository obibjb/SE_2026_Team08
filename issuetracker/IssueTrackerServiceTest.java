package issuetracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * 이슈 트래커 핵심 비즈니스 로직 및 정합성 검증을 위한 JUnit 5 단위 테스트 클래스입니다.
 */
public class IssueTrackerServiceTest {

    private IssueTrackerService service;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 싱글톤 서비스 인스턴스를 초기화합니다.
        service = IssueTrackerService.getInstance();
    }

    @Test
    @DisplayName("결함 상태(Status) 필터링 단위 테스트")
    void testFilterByStatus() {
        // 1. Given (테스트 준비: 서비스 객체 상태 확인)
        assertNotNull(service, "IssueTrackerService 인스턴스가 존재해야 합니다.");

        // 2. When (테스트 실행: "New" 상태의 결함만 필터링 조회)
        List<Issue> newIssues = service.getIssues("New", "All Priority", "All Assignees", "");

        // 3. Then (결과 검증: 반환된 모든 결함의 상태가 "New"인지 확인)
        assertNotNull(newIssues, "반환된 리스트는 null이 아니어야 합니다.");
        for (Issue issue : newIssues) {
            assertEquals("New", issue.getStatus(), "필터링된 결함의 상태는 반드시 'New'여야 합니다.");
        }
    }

    @Test
    @DisplayName("키워드 기반 결함 검색 단위 테스트")
    void testSearchByKeyword() {
        // 1. Given (테스트 준비)
        String keyword = "로그인";

        // 2. When (테스트 실행: "로그인" 키워드로 검색어 필터링)
        List<Issue> searchedIssues = service.getIssues("All Status", "All Priority", "All Assignees", keyword);

        // 3. Then (결과 검증: 검색된 이슈의 제목에 키워드가 포함되어 있는지 확인)
        assertFalse(searchedIssues.isEmpty(), "검색어에 매칭되는 결함이 최소 1개 이상 존재해야 합니다.");
        for (Issue issue : searchedIssues) {
            boolean containsInTitle = issue.getTitle() != null && issue.getTitle().contains(keyword);
            boolean containsInId = issue.getId() != null && issue.getId().contains(keyword);
            
            assertTrue(containsInTitle || containsInId, 
                "검색 결과로 나온 결함은 제목이나 ID에 '로그인'이라는 키워드를 포함해야 합니다.");
        }
    }

    @Test
    @DisplayName("결함 담당자 배정 및 정합성 검증 테스트")
    void testAssignIssueAndStatusChange() {
        // 1. Given (테스트 준비: 임의의 테스트용 이슈 생성 및 등록)
        Issue testIssue = new Issue(
            "ISSUE-999", 
            "테스트 이슈", 
            "내용", 
            "Major", 
            "New", 
            "Unassigned", 
            "reporter1", 
            "2026-06-04"
        );

        // 2. When (테스트 실행: 담당자를 'Choi Matia'로 변경 및 세팅)
        testIssue.setAssignee("Choi Matia");
        testIssue.setStatus("Assigned");

        // 3. Then (결과 검증: 객체의 필드 값이 정상적으로 변했는지 확인)
        assertEquals("Choi Matia", testIssue.getAssignee(), "담당자가 'Choi Matia'로 올바르게 변경되어야 합니다.");
        assertEquals("Assigned", testIssue.getStatus(), "담당자 배정에 따라 상태가 'Assigned'로 변경되어야 합니다.");
    }
}
