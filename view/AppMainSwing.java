package view;

import issuetracker.Issue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppMainSwing extends JFrame {

    private List<Issue> globalIssueDataset = new ArrayList<>();

    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterStatus;
    private JComboBox<String> filterPriority;
    private JComboBox<String> filterAssignee;
    private JTextField searchField;
    private JPanel filterBar;

    public AppMainSwing() {
        setTitle("Issue Tracker System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        initMockData();
        refreshTable(globalIssueDataset);
    }

    private void initMockData() {
        globalIssueDataset.add(new Issue("ISSUE-001", "로그인 화면에서 비밀번호 찾기 버튼이 작동하지 않음", "Password reset...", "Blocker", "New", "Unassigned", "tester1", "5/27/2026"));
        globalIssueDataset.add(new Issue("ISSUE-002", "대시보드 차트 렌더링 성능 저하", "Performance...", "Critical", "Assigned", "dev2", "pl1", "5/26/2026"));
        globalIssueDataset.add(new Issue("ISSUE-003", "알림 이메일이 중복 발송됨", "Notification...", "Major", "Fixed", "dev5", "tester2", "5/25/2026"));
        globalIssueDataset.add(new Issue("ISSUE-004", "모바일 화면에서 네비게이션 메뉴가 잘림", "Mobile...", "Minor", "Resolved", "dev1", "tester1", "5/24/2026"));
        globalIssueDataset.add(new Issue("ISSUE-005", "검색 기능에서 특수문자 입력 시 오류 발생", "Search...", "Major", "Assigned", "dev3", "tester2", "5/27/2026"));
        globalIssueDataset.add(new Issue("ISSUE-006", "프로필 이미지 업로드 제한 없음", "Profile...", "Minor", "New", "Unassigned", "pl2", "5/27/2026"));
        globalIssueDataset.add(new Issue("ISSUE-007", "다국어 지원 시 일부 텍스트가 번역되지 않음", "Translation...", "Trivial", "Closed", "dev4", "tester1", "5/20/2026"));
        globalIssueDataset.add(new Issue("ISSUE-008", "데이터 익스포트 기능이 CSV 형식을 지원하지 않음", "Export...", "Minor", "Assigned", "dev2", "pl1", "5/26/2026"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppMainSwing().setVisible(true);
        });
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 20, 15, 20));

        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Issue Management System");
        titleLabel.setFont(new Font("Pretendard", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton btnCreate = new JButton("Create Issue");
        btnCreate.setFont(new Font("Pretendard", Font.BOLD, 14));
        titlePanel.add(btnCreate, BorderLayout.EAST);
        btnCreate.addActionListener(e -> openCreateIssueDialog());

        filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

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
        tablePanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        tablePanel.setBackground(Color.WHITE);

        String[] columnNames = {"Issue ID", "Title", "Priority", "Status", "Assignee", "Reporter", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        issueTable = new JTable(tableModel);
        issueTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(issueTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel infoLabel = new JLabel("Total Issues: 8");
        footerPanel.add(infoLabel, BorderLayout.EAST);

        return footerPanel;
    }

    private void refreshTable(List<Issue> list) {
        tableModel.setRowCount(0);
        for (Issue issue : list) {
            tableModel.addRow(new Object[]{
                    issue.getId(),
                    issue.getTitle(),
                    issue.getPriority(),
                    issue.getStatus(),
                    issue.getAssignee() != null ? issue.getAssignee() : "Unassigned",
                    issue.getReporter(),
                    issue.getDate()
            });
        }
    }

    private void openCreateIssueDialog() {
        JDialog dialog = new JDialog(this, "Create New Issue", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtTitle = new JTextField();
        JTextArea txtDesc = new JTextArea(3, 20);
        txtDesc.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblRecommended = new JLabel("Choi Matia (알고리즘 추천됨)");
        lblRecommended.setForeground(Color.BLUE);
        lblRecommended.setFont(new Font("Pretendard", Font.BOLD, 13));

        panel.add(new JLabel("Title:"));
        panel.add(txtTitle);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(txtDesc));
        panel.add(new JLabel("Recommended Assignee (UC-4):"));
        panel.add(lblRecommended);

        JButton btnSave = new JButton("Save Issue");
        btnSave.setFont(new Font("Pretendard", Font.BOLD, 13));
        btnSave.addActionListener(e -> {
            String newId = "ISSUE-00" + (globalIssueDataset.size() + 1);
            globalIssueDataset.add(new Issue(newId, txtTitle.getText(), txtDesc.getText(), "Major", "New", "Choi Matia", "tester1", "6/3/2026"));
            refreshTable(globalIssueDataset);
            dialog.dispose();
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);

        txtTitle.setText("DB Connection 오류");
        txtDesc.setText("MySQL 서버 연결이 자꾸 끊어집니다.");

        dialog.setVisible(true);
    }

    private void performLiveSearchAndFiltering() {
        String statusValue = (String) filterStatus.getSelectedItem();
        String priorityValue = (String) filterPriority.getSelectedItem();
        String assigneeValue = (String) filterAssignee.getSelectedItem();
        String keyword = searchField.getText().toLowerCase().trim();

        List<Issue> filteredResult = globalIssueDataset.stream().filter(item -> {
            boolean matchStatus = statusValue.equals("All Status") || item.getStatus().equals(statusValue);
            boolean matchPriority = priorityValue.equals("All Priority") || item.getPriority().equals(priorityValue);
            boolean matchAssignee = assigneeValue.equals("All Assignees") || (item.getAssignee() != null && item.getAssignee().equalsIgnoreCase(assigneeValue)) || (assigneeValue.equals("Unassigned") && (item.getAssignee() == null || item.getAssignee().equalsIgnoreCase("Unassigned")));

            boolean matchKeyword = keyword.isEmpty() ||
                    item.getTitle().toLowerCase().contains(keyword) ||
                    item.getId().toLowerCase().contains(keyword);

            return matchStatus && matchPriority && matchAssignee && matchKeyword;
        }).collect(Collectors.toList());

        refreshTable(filteredResult);
    }
}
