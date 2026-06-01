import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/* ==========================================================
   1. MODEL LAYER (기존 데이터 구조 및 Mock Data 완벽 매핑)
   ========================================================== */
class Issue {
    String id;
    String title;
    String priority;
    String status;
    String assignee;
    String reporter;
    String date;

    public Issue(String id, String title, String priority, String status, String assignee, String reporter, String date) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.assignee = assignee;
        this.reporter = reporter;
        this.date = date;
    }
}

/* ==========================================================
   2. VIEW & CONTROLLER LAYER (Java Swing UI 구현)
   ========================================================== */
public class AppMainSwing extends JFrame {

    // 글로벌 데이터셋 (웹 프로토타입 app.js와 100% 동일한 데이터)
    private List<Issue> globalIssueDataset = new ArrayList<>();

    // UI 컴포넌트 레퍼런스
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterStatus;
    private JComboBox<String> filterPriority;
    private JComboBox<String> filterAssignee;
    private JTextField searchField;
    private JLabel footerLabel;

    public AppMainSwing() {
        // 데이터 초기화
        initMockData();

        // 윈도우 기본 설정
        setTitle("Issue Tracking System - Desktop GUI (Swing)");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메인 레이아웃 (BorderLayout)
        setLayout(new BorderLayout());

        // 상단 헤더 및 필터 패널 구성
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 중앙 테이블 영역 구성
        add(createTablePanel(), BorderLayout.CENTER);

        // 하단 푸터 패널 구성
        add(createFooterPanel(), BorderLayout.SOUTH);

        // 최초 데이터 로드
        updateTableGrid(globalIssueDataset);
    }

    private void initMockData() {
        globalIssueDataset.add(new Issue("ISSUE-001", "로그인 화면에서 비밀번호 찾기 버튼이 작동하지 않음", "Blocker", "New", "Unassigned", "tester1", "5/27/2026"));
        globalIssueDataset.add(new Issue("ISSUE-002", "대시보드 차트 렌더링 성능 저하", "Critical", "Assigned", "dev2", "pl1", "5/26/2026"));
        globalIssueDataset.add(new Issue("ISSUE-003", "알림 이메일이 중복 발송됨", "Major", "In Progress", "dev5", "tester2", "5/25/2026"));
        globalIssueDataset.add(new Issue("ISSUE-004", "모바일 화면에서 네비게이션 메뉴가 잘림", "Minor", "Resolved", "dev1", "tester1", "5/24/2026"));
        globalIssueDataset.add(new Issue("ISSUE-005", "검색 기능에서 특수문자 입력 시 오류 발생", "Major", "Assigned", "dev3", "tester2", "5/27/2026"));
        globalIssueDataset.add(new Issue("ISSUE-006", "프로필 이미지 업로드 제한 없음", "Minor", "New", "Unassigned", "pl2", "5/27/2026"));
        globalIssueDataset.add(new Issue("ISSUE-007", "다국어 지원 시 일부 텍스트가 번역되지 않음", "Trivial", "Closed", "dev4", "tester1", "5/20/2026"));
        globalIssueDataset.add(new Issue("ISSUE-008", "데이터 익스포트 기능이 CSV 형식을 지원하지 않음", "Minor", "Assigned", "dev2", "pl1", "5/26/2026"));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(new Color(248, 250, 252)); // var(--bg-main) 매핑

        // 타이틀 영역
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Issue Browser");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));
        JLabel subLabel = new JLabel("Track and manage all project issues in desktop interface");
        subLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        subLabel.setForeground(new Color(100, 116, 139));

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subLabel, BorderLayout.SOUTH);

        // 신규 이슈 버튼 (우측 배치)
        JButton btnNewIssue = new JButton("+ New Issue");
        btnNewIssue.setBackground(new Color(15, 23, 42));
        btnNewIssue.setForeground(Color.WHITE);
        btnNewIssue.setFocusPainted(false);
        titlePanel.add(btnNewIssue, BorderLayout.EAST);

        // 필터 및 검색 바 영역
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterBar.setOpaque(false);
        filterBar.setBorder(BorderFactory.createTitledBorder("Filters & Live Search"));

        searchField = new JTextField(15);
        filterStatus = new JComboBox<>(new String[]{"All Status", "New", "Assigned", "In Progress", "Resolved", "Closed"});
        filterPriority = new JComboBox<>(new String[]{"All Priority", "Blocker", "Critical", "Major", "Minor", "Trivial"});
        filterAssignee = new JComboBox<>(new String[]{"All Assignees", "Unassigned", "dev1", "dev2", "dev3", "dev4", "dev5"});

        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);
        filterBar.add(new JLabel("Status:"));
        filterBar.add(filterStatus);
        filterBar.add(new JLabel("Priority:"));
        filterBar.add(filterPriority);
        filterBar.add(new JLabel("Assignee:"));
        filterBar.add(filterAssignee);

        // 필터 및 검색 이벤트 리스너 바인딩 (실시간 반응형 엔진)
        searchField.addCaretListener(e -> performLiveSearchAndFiltering());
        filterStatus.addActionListener(e -> performLiveSearchAndFiltering());
        filterPriority.addActionListener(e -> performLiveSearchAndFiltering());
        filterAssignee.addActionListener(e -> performLiveSearchAndFiltering());

        headerPanel.add(titlePanel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(filterBar);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        tablePanel.setBackground(Color.WHITE);

        String[] columnNames = {"Issue ID", "Title", "Priority", "Status", "Assignee", "Reporter", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가 설정
            }
        };

        issueTable = new JTable(tableModel);
        issueTable.setRowHeight(30);
        issueTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 13));
        issueTable.getTableHeader().setReorderingAllowed(false);
        issueTable.setFont(new Font("Inter", Font.PLAIN, 13));
        issueTable.setGridColor(new Color(226, 232, 240));

        JScrollPane scrollPane = new JScrollPane(issueTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 24, 15, 24));
        footerPanel.setBackground(Color.WHITE);

        footerLabel = new JLabel("Showing 8 of 8 issues");
        footerLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 116, 139));

        JLabel infoLabel = new JLabel("CAU SE Term Project 2026 - Team UI Layer 2");
        infoLabel.setFont(new Font("Inter", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(148, 163, 184));

        footerPanel.add(footerLabel, BorderLayout.WEST);
        footerPanel.add(infoLabel, BorderLayout.EAST);

        return footerPanel;
    }

    /* ==========================================================
       3. CONTROLLER CORE LOGIC (복합 실시간 필터링 기능)
       ========================================================== */
    private void performLiveSearchAndFiltering() {
        String statusValue = (String) filterStatus.getSelectedItem();
        String priorityValue = (String) filterPriority.getSelectedItem();
        String assigneeValue = (String) filterAssignee.getSelectedItem();
        String keyword = searchField.getText().toLowerCase().trim();

        List<Issue> filteredResult = globalIssueDataset.stream().filter(item -> {
            boolean matchStatus = statusValue.equals("All Status") || item.status.equals(statusValue);
            boolean matchPriority = priorityValue.equals("All Priority") || item.priority.equals(priorityValue);
            boolean matchAssignee = assigneeValue.equals("All Assignees") || item.assignee.equals(assigneeValue);
            boolean matchKeyword = keyword.isEmpty() ||
                    item.title.toLowerCase().contains(keyword) ||
                    item.id.toLowerCase().contains(keyword);

            return matchStatus && matchPriority && matchAssignee && matchKeyword;
        }).collect(Collectors.toList());

        updateTableGrid(filteredResult);
    }

    private void updateTableGrid(List<Issue> issues) {
        tableModel.setRowCount(0); // 테이블 초기화

        for (Issue item : issues) {
            tableModel.addRow(new Object[]{
                    item.id,
                    item.title,
                    item.priority,
                    item.status,
                    item.assignee,
                    item.reporter,
                    item.date
            });
        }
        footerLabel.setText("Showing " + issues.size() + " of " + globalIssueDataset.size() + " issues");
    }

    public static void main(String[] args) {
        // 세련된 시스템 Look앤Feel 적용
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new AppMainSwing().setVisible(true);
        });
    }
}