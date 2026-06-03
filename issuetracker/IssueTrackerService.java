package issuetracker;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class IssueTrackerService {
    private List<Issue> issues = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int nextIssueNumber = 9;
    private static final String FILE_PATH = "data.ser";

    public IssueTrackerService() {
        if (!loadData()) {
            seedUsers();
            seedIssues();
            saveData();
        }
    }

    private synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(issues);
            oos.writeObject(users);
            oos.writeInt(nextIssueNumber);
        } catch (IOException e) {
            System.err.println("데이터 영속화 저장 실패: " + e.getMessage());
        }
    }

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
        users.add(new User("
