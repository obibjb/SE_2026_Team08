/* ==========================================================
   A. GLOBAL DATA MODEL SETTING (시나리오 요구 데이터 매핑)
   ========================================================== */
const globalIssueDataset = [
    { id: "ISSUE-001", title: "로그인 화면에서 비밀번호 찾기 버튼이 작동하지 않음", priority: "Blocker", status: "New", assignee: "Unassigned", reporter: "tester1", date: "5/27/2026" },
    { id: "ISSUE-002", title: "대시보드 차트 렌더링 성능 저하", priority: "Critical", status: "Assigned", assignee: "dev2", reporter: "pl1", date: "5/26/2026" },
    { id: "ISSUE-003", title: "알림 이메일이 중복 발송됨", priority: "Major", status: "In Progress", assignee: "dev5", reporter: "tester2", date: "5/25/2026" },
    { id: "ISSUE-004", title: "모바일 화면에서 네비게이션 메뉴가 잘림", priority: "Minor", status: "Resolved", assignee: "dev1", reporter: "tester1", date: "5/24/2026" },
    { id: "ISSUE-005", title: "검색 기능에서 특수문자 입력 시 오류 발생", priority: "Major", status: "Assigned", assignee: "dev3", reporter: "tester2", date: "5/27/2026" },
    { id: "ISSUE-006", title: "프로필 이미지 업로드 제한 없음", priority: "Minor", status: "New", assignee: "Unassigned", reporter: "pl2", date: "5/27/2026" },
    { id: "ISSUE-007", title: "다국어 지원 시 일부 텍스트가 번역되지 않음", priority: "Trivial", status: "Closed", assignee: "dev4", reporter: "tester1", date: "5/20/2026" },
    { id: "ISSUE-008", title: "데이터 익스포트 기능이 CSV 형식을 지원하지 않음", priority: "Minor", status: "Assigned", assignee: "dev2", reporter: "pl1", date: "5/26/2026" }
];

const globalUserDataset = [
    { name: "김철수 (Dev1)", email: "dev1@company.com", role: "Dev", id: "dev1", colorClass: "av-green" },
    { name: "이영희 (Dev2)", email: "dev2@company.com", role: "Dev", id: "dev2", colorClass: "av-green" },
    { name: "박민수 (Dev3)", email: "dev3@company.com", role: "Dev", id: "dev3", colorClass: "av-green" },
    { name: "정수진 (Dev4)", email: "dev4@company.com", role: "Dev", id: "dev4", colorClass: "av-green" },
    { name: "최동욱 (Dev5)", email: "dev5@company.com", role: "Dev", id: "dev5", colorClass: "av-green" },
    { name: "강태영 (PL1)", email: "pl1@company.com", role: "PL", id: "pl1", colorClass: "av-blue" },
    { name: "윤서현 (PL2)", email: "pl2@company.com", role: "PL", id: "pl2", colorClass: "av-blue" },
    { name: "임지훈 (Tester1)", email: "tester1@company.com", role: "Tester", id: "tester1", colorClass: "av-orange" },
    { name: "홍유진 (Tester2)", email: "tester2@company.com", role: "Tester", id: "tester2", colorClass: "av-orange" },
    { name: "서관리 (Admin)", email: "admin@company.com", role: "Admin", id: "admin1", colorClass: "av-purple" }
];

let initializedCharts = { line: null, pie: null, bar: null };

/* ==========================================================
   B. CLIENT-SIDE ROUTING CONTROLLER (싱글 페이지 화면 전환)
   ========================================================== */
document.querySelectorAll('.sidebar-menu .menu-item').forEach(menuBtn => {
    menuBtn.addEventListener('click', function() {
        document.querySelectorAll('.sidebar-menu .menu-item').forEach(b => b.classList.remove('active'));
        this.classList.add('active');

        const targetPageId = this.getAttribute('data-target');
        document.querySelectorAll('.content-page').forEach(page => page.classList.remove('active'));
        document.getElementById(targetPageId).classList.add('active');

        if(targetPageId === 'page-analytics') {
            triggerChartRenderPipeline();
        }
    });
});

/* ==========================================================
   C. RENDERING PIEPLINE (데이터 동적 바인딩 렌더링)
   ========================================================== */
function populateIssueTableGrid(issues) {
    const tableBody = document.getElementById("issueGridTableBody");
    tableBody.innerHTML = "";
    
    if (issues.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 40px;">No issues match the selected filters.</td></tr>`;
        document.getElementById("issueTrackerFooterText").textContent = "Showing 0 of 0 issues";
        return;
    }

    issues.forEach(item => {
        const tr = document.createElement("tr");
        const assigneeDisplay = item.assignee === "Unassigned" ? `<span class="text-unassigned">Unassigned</span>` : item.assignee;
        tr.innerHTML = `
            <td><a href="#" class="issue-link">${item.id}</a></td>
            <td style="font-weight: 500; color: #1e293b;">${item.title}</td>
            <td><span class="badge ${item.priority.toLowerCase()}">${item.priority}</span></td>
            <td><span class="badge-st ${item.status.toLowerCase().replace(" ", "")}">${item.status}</span></td>
            <td>${assigneeDisplay}</td>
            <td>${item.reporter}</td>
            <td>${item.date}</td>
        `;
        tableBody.appendChild(tr);
    });
    document.getElementById("issueTrackerFooterText").textContent = `Showing ${issues.length} of ${globalIssueDataset.length} issues`;
}

function populateUserTableGrid() {
    const tableBody = document.getElementById("userGridTableBody");
    tableBody.innerHTML = "";
    let countMap = { Admin: 0, PL: 0, Dev: 0, Tester: 0 };

    globalUserDataset.forEach(user => {
        countMap[user.role]++;
        const firstChar = user.name.charAt(0);
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>
                <div class="user-meta-cell">
                    <div class="user-avatar-mini ${user.colorClass}">${firstChar}</div>
                    <div style="font-weight: 500; color: #1e293b;">${user.name}</div>
                </div>
            </td>
            <td><a href="mailto:${user.email}" class="email-link"><i class="fa-regular fa-envelope"></i> ${user.email}</a></td>
            <td><span class="role-indicator-badge ri-${user.role.toLowerCase()}">${user.role}</span></td>
            <td style="font-family: monospace; color: var(--text-muted); font-size: 13px;">${user.id}</td>
            <td>
                <div class="action-button-group">
                    <button class="btn-table-edit">Edit</button>
                    <button class="btn-table-remove">Remove</button>
                </div>
            </td>
        `;
        tableBody.appendChild(tr);
    });

    document.getElementById("cntAdmin").textContent = countMap.Admin;
    document.getElementById("cntPL").textContent = countMap.PL;
    document.getElementById("cntDev").textContent = countMap.Dev;
    document.getElementById("cntTester").textContent = countMap.Tester;
    document.getElementById("userManagementFooterText").textContent = `Total ${globalUserDataset.length} users`;
}

/* ==========================================================
   D. FILTER ENGINE INTERACTION (검색 및 복합 필터 동기화)
   ========================================================== */
function performLiveSearchAndFiltering() {
    const statusValue = document.getElementById("filterStatus").value;
    const priorityValue = document.getElementById("filterPriority").value;
    const assigneeValue = document.getElementById("filterAssignee").value;
    const globalSearchKeyword = document.getElementById("globalSearchInput").value.toLowerCase().trim();

    const filteredResult = globalIssueDataset.filter(item => {
        const matchStatus = (statusValue === "all" || item.status === statusValue);
        const matchPriority = (priorityValue === "all" || item.priority === priorityValue);
        const matchAssignee = (assigneeValue === "all" || item.assignee === assigneeValue);
        const matchKeyword = (item.title.toLowerCase().includes(globalSearchKeyword) || item.id.toLowerCase().includes(globalSearchKeyword));

        return matchStatus && matchPriority && matchAssignee && matchKeyword;
    });

    populateIssueTableGrid(filteredResult);
}

document.getElementById("filterStatus").addEventListener("change", performLiveSearchAndFiltering);
document.getElementById("filterPriority").addEventListener("change", performLiveSearchAndFiltering);
document.getElementById("filterAssignee").addEventListener("change", performLiveSearchAndFiltering);
document.getElementById("globalSearchInput").addEventListener("input", performLiveSearchAndFiltering);

/* ==========================================================
   E. VISUALIZATION CHART CONTROLLER (차트 동적 렌더링)
   ========================================================== */
function triggerChartRenderPipeline() {
    if (initializedCharts.line) initializedCharts.line.destroy();
    if (initializedCharts.pie) initializedCharts.pie.destroy();
    if (initializedCharts.bar) initializedCharts.bar.destroy();

    const ctxLine = document.getElementById('canvasTrendLine').getContext('2d');
    initializedCharts.line = new Chart(ctxLine, {
        type: 'line',
        data: {
            labels: ['5/21', '5/22', '5/23', '5/24', '5/25', '5/26', '5/27'],
            datasets: [
                { label: 'Created', data: [2, 1, 3, 2, 1, 2, 3], borderColor: '#3b82f6', backgroundColor: 'transparent', borderWidth: 2, pointBackgroundColor: '#ffffff', pointBorderColor: '#3b82f6', pointRadius: 4, tension: 0.4 },
                { label: 'Resolved', data: [1, 2, 1, 2, 0, 1, 1], borderColor: '#10b981', backgroundColor: 'transparent', borderWidth: 2, pointBackgroundColor: '#ffffff', pointBorderColor: '#10b981', pointRadius: 4, tension: 0.4 }
            ]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } }, scales: { y: { min: 0, max: 3, ticks: { stepSize: 0.75 } }, x: { grid: { display: false } } } }
    });

    const ctxPie = document.getElementById('canvasPriorityPie').getContext('2d');
    initializedCharts.pie = new Chart(ctxPie, {
        type: 'pie',
        data: {
            labels: ['Minor: 38%', 'Major: 25%', 'Critical: 13%', 'Blocker: 13%', 'Trivial: 13%'],
            datasets: [{ data: [38, 25, 13, 13, 13], backgroundColor: ['#3b82f6', '#eab308', '#f97316', '#ef4444', '#64748b'], borderWidth: 2, borderColor: '#ffffff' }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right', labels: { boxWidth: 12, padding: 14 } } } }
    });

    const ctxBar = document.getElementById('canvasAssigneeBar').getContext('2d');
    initializedCharts.bar = new Chart(ctxBar, {
        type: 'bar',
        data: {
            labels: ['dev2', 'dev5', 'dev1', 'dev3', 'dev4'],
            datasets: [{ label: 'Issues Assigned', data: [2, 1, 1, 1, 1], backgroundColor: '#3b82f6', borderRadius: 4, barThickness: 120 }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { min: 0, max: 2, ticks: { stepSize: 0.5 } }, x: { grid: { display: false } } } }
    });
}

// 초기화 가동
document.addEventListener("DOMContentLoaded", () => {
    populateIssueTableGrid(globalIssueDataset);
    populateUserTableGrid();
});