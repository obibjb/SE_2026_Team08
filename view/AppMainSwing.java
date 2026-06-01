package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class AppMainSwing extends JFrame {

    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;
    private JTextField searchField;
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public AppMainSwing() {
        // 메인 창 설정 및 기본 타이틀 지정
        setTitle("Issue Tracking System - Desktop Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 전체 레이아웃 서브 패널 배치
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 250, 252)); // 웹 테마 메인 배경색 반영

        // 상단 필터 및 검색 바 영역 구성
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 콤보박스 및 입력창 UI 속성 공통 적용
        statusFilter = new JComboBox<>(new String[]{"All Status", "NEW", "ASSIGNED", "FIXED", "RESOLVED", "CLOSED", "REOPENED"});
        priorityFilter = new JComboBox<>(new String[]{"All Priority", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL"});
        searchField = new JTextField(20);

        Dimension compSize = new Dimension(150, 35);
        statusFilter.setPreferredSize(compSize);
        priorityFilter.setPreferredSize(compSize);
        searchField.setPreferredSize(new Dimension(250, 35));

        // 컴포넌트 순차 배치
        gbc.gridx = 0; topPanel.add(statusFilter, gbc);
        gbc.gridx = 1; topPanel.add(priorityFilter, gbc);
        gbc.gridx = 2; gbc.weightx = 1.0; topPanel.add(searchField, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 테이블 컬럼 헤더 정의
        String[] columns = {"Issue ID", "Title", "Priority", "Status", "Assignee", "Reporter", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 더블클릭 수정 금지 설정
            }
        };

        issueTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        issueTable.setRowSorter(rowSorter);

        // 테이블 내부 세부 디자인 다듬기
        issueTable.setRowHeight(30);
        issueTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        issueTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        issueTable.getTableHeader().setBackground(new Color(30, 41, 59)); // 웹 테마 네이비 컬러 매핑
        issueTable.getTableHeader().setForeground(Color.WHITE);
        issueTable.setGridColor(new Color(226, 232, 240));

        JScrollPane scrollPane = new JScrollPane(issueTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 연동 전 임시 샘플 데이터 세팅
        initMockData();

        // 실시간 필터 및 검색 이벤트 핸들러 연결
        statusFilter.addActionListener(e -> performLiveSearchAndFiltering());
        priorityFilter.addActionListener(e -> performLiveSearchAndFiltering());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performLiveSearchAndFiltering();
            }
        });

        add(mainPanel);
    }

    // 소민이 웹 UI(app.js)의 기본 데이터셋 구조와 동일하게 매핑
    private void initMockData() {
        tableModel.addRow(new Object[]{"ISSUE-001", "로그인 화면에서 비밀번호 찾기 버튼이 작동하지 않음", "BLOCKER", "NEW", "Unassigned", "tester1", "5/27/2026"});
        tableModel.addRow(new Object[]{"ISSUE-002", "대시보드 차트 렌더링 성능 저하", "CRITICAL", "ASSIGNED", "dev2", "pl1", "5/26/2026"});
        tableModel.addRow(new Object[]{"ISSUE-003", "알림 설정 저장 시 간헐적 튕김 현상 발생", "MAJOR", "FIXED", "dev1", "tester3", "5/25/2026"});
        tableModel.addRow(new Object[]{"ISSUE-004", "모바일 뷰에서 사이드바 메뉴 겹침 문제", "MINOR", "RESOLVED", "dev5", "tester2", "5/24/2026"});
        tableModel.addRow(new Object[]{"ISSUE-005", "오타 수정: 설정 페이지 '알림' 표기 오류", "TRIVIAL", "CLOSED", "dev3", "pl2", "5/23/2026"});
    }

    // 다중 조건 정규식 기반 동적 필터링 처리
    private void performLiveSearchAndFiltering() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedPriority = (String) priorityFilter.getSelectedItem();
        String searchText = searchField.getText().trim();

        Vector<RowFilter<Object, Object>> filters = new Vector<>();

        // 상태값 필터 검사
        if (selectedStatus != null && !selectedStatus.equals("All Status")) {
            filters.add(RowFilter.regexFilter("^" + selectedStatus + "$", 3));
        }

        // 우선순위 필터 검사
        if (selectedPriority != null && !selectedPriority.equals("All Priority")) {
            filters.add(RowFilter.regexFilter("^" + selectedPriority + "$", 2));
        }

        // 텍스트 검색 검사 (전체 컬럼 대상 타겟팅)
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText));
        }

        // 최종 필터 융합 및 테이블 업데이트
        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    public static void main(String[] args) {
        // OS별 컴포넌트 스타일 룩앤필 동기화 처리
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 메인 GUI 쓰레드 구동
        SwingUtilities.invokeLater(() -> {
            new AppMainSwing().setVisible(true);
        });
    }
}
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
