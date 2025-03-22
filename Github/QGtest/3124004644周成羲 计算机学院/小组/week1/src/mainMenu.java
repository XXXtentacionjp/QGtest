import java.sql.*;
import java.util.Scanner;

public class mainMenu {
    private static final String ADMIN_SECRET = "SuperSecure123"; // 预设管理员密钥
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("===========================");
            System.out.println("🎓 学生选课管理系统");
            System.out.println("===========================");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.print("请选择操作（输入 1-3）：");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    System.out.println("退出系统");
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    // 登录功能
    private static void login(Scanner scanner) {
        System.out.println("===== 用户登录 =====");
        System.out.print("请输入用户名：");
        String username = scanner.next();
        System.out.print("请输入密码：");
        String password = scanner.next();

        try (Connection conn = main.getConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                System.out.println("登录成功！你的角色是：" + role);
                if ("student".equals(role)) {
                    int userId = rs.getInt("user_id");

                    // 查询 students 表获取 student_id
                    String studentSql = "SELECT student_id FROM students WHERE user_id = ?";
                    PreparedStatement studentPs = conn.prepareStatement(studentSql);
                    studentPs.setInt(1, userId);
                    ResultSet studentRs = studentPs.executeQuery();
                    if (studentRs.next()) {
                        int studentId = studentRs.getInt("student_id");
                        studentMenu(userId, studentId);
                    } else {
                        System.out.println("未找到学生信息！");
                    }
                } else {
                    adminMenu();
                }
            } else {
                System.out.println("用户名或密码错误！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 用户注册
    private static void register(Scanner scanner) {
        System.out.println("===== 用户注册 =====");
        System.out.print("请输入用户名：");
        String username = scanner.next();
        System.out.print("请输入密码：");
        String password = scanner.next();
        System.out.print("请选择角色（1: 学生，2: 管理员）：");
        int roleChoice = scanner.nextInt();
        String role = (roleChoice == 1) ? "student" : "admin";

        try (Connection conn = main.getConnection()) {
            // 1. 插入到 `user` 表
            String sqlUser = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username);
            psUser.setString(2, password);
            psUser.setString(3, role);
            int userInserted = psUser.executeUpdate();

            // 如果是管理员，要求输入管理员密钥
            if ("admin".equals(role)) {
                System.out.print("请输入管理员注册密钥：");
                String adminKey = scanner.next();
                if (!ADMIN_SECRET.equals(adminKey)) {
                    System.out.println("管理员密钥错误，注册失败！");
                    return;
                }
            }

            if (userInserted > 0) {
                System.out.println("用户注册成功！");

                // 获取 `user_id`
                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1); // 获取生成的 `user_id`

                    // 2. 如果是学生，插入 `students` 表
                    if ("student".equals(role)) {
                        System.out.print("请输入学生姓名：");
                        String name = scanner.next();
                        System.out.print("请输入手机号（可选）：");
                        String phone = scanner.next();

                        String sqlStudent = "INSERT INTO students (user_id, name, phone) VALUES (?, ?, ?)";
                        PreparedStatement psStudent = conn.prepareStatement(sqlStudent);
                        psStudent.setInt(1, userId);
                        psStudent.setString(2, name);
                        psStudent.setString(3, phone.isEmpty() ? null : phone);
                        psStudent.executeUpdate();

                        System.out.println("学生信息录入成功！");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("注册失败：" + e.getMessage());
        }
    }

    // 学生功能菜单
    private static void studentMenu(int userId,int studentID) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("===== 学生菜单 =====");
            System.out.println("1. 查看可选课程");
            System.out.println("2. 选择课程");
            System.out.println("3. 退选课程");
            System.out.println("4. 查看已选课程");
            System.out.println("5. 修改手机号");
            System.out.println("6. 退出");
            System.out.print("请选择操作（输入 1-6）：");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    showAvailableCourses();
                    break;
                case 2:
                    selectCourse(studentID);
                    break;
                case 3:
                    dropCourse(studentID);
                    break;
                case 4:
                    showSelectedCourses(studentID);
                    break;
                case 5:
                    updatePhone(studentID);
                    break;
                case 6:
                    System.out.println("退出学生菜单");
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    // 管理员功能菜单
    private static void adminMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("===== 管理员菜单 =====");
            System.out.println("1. 查询所有学生");
            System.out.println("2. 修改学生手机号");
            System.out.println("3. 查询所有课程");
            System.out.println("4. 修改课程学分");
            System.out.println("5. 查询某课程的学生名单");
            System.out.println("6. 查询某学生的选课情况");
            System.out.println("7. 添加课程");
            System.out.println("8. 删除课程");
            System.out.println("9. 更改本学期课程开放状态");
            System.out.println("10. 退出");
            System.out.print("请选择操作（输入 1-10）：");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    showAllStudents();
                    break;
                case 2:
                    updateStudentPhone();
                    break;
                case 3:
                    showAllCourses();
                    break;
                case 4:
                    updateCourseCredits();
                    break;
                case 5:
                    showCourseStudents();
                    break;
                case 6:
                    showStudentCourses();
                    break;
                case 7:
                    addCourse();
                    break;
                case 8:
                    deleteCourse();
                    break;
                case 9:
                    toggleCourseStatus();
                    break;
                case 10:
                    System.out.println("退出管理员菜单");
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    // 查询所有可选课程
    private static void showAvailableCourses() {
        try (Connection conn = main.getConnection()) {
            String sql = "SELECT * FROM courses WHERE is_open = TRUE";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("course_id") + ". " + rs.getString("course_name") + " - " + rs.getInt("credits") + "学分");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //选择课程
    private static void selectCourse(int studentId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要选择的课程ID：");
        int courseId = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            // 1. 检查学生是否存在于 students 表中
            String checkStudentSql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
            PreparedStatement checkStudentPs = conn.prepareStatement(checkStudentSql);
            checkStudentPs.setInt(1, studentId);
            ResultSet checkStudentRs = checkStudentPs.executeQuery();

            if (checkStudentRs.next() && checkStudentRs.getInt(1) > 0) {  // 学生存在
                // 2. 查询课程是否开放
                String checkCourseSql = "SELECT is_open FROM courses WHERE course_id = ?";
                PreparedStatement checkCoursePs = conn.prepareStatement(checkCourseSql);
                checkCoursePs.setInt(1, courseId);
                ResultSet checkCourseRs = checkCoursePs.executeQuery();

                if (checkCourseRs.next()) {
                    boolean isCourseOpen = checkCourseRs.getBoolean("is_open");

                    if (!isCourseOpen) {
                        System.out.println("该课程目前不可选择！");
                        return;
                    }
                }

                // 3. 查询学生已选课程数量
                String sql = "SELECT COUNT(*) FROM student_courses WHERE student_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, studentId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {  // 确保有数据
                    int courseCount = rs.getInt(1);
                    if (courseCount < 5) {
                        // 4. 如果选课数量小于5，允许选课
                        sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
                        try (Connection conn2 = main.getConnection()) {  // 使用连接池管理
                            PreparedStatement pss = conn2.prepareStatement(sql);
                            pss.setInt(1, studentId);
                            pss.setInt(2, courseId);
                            pss.executeUpdate();
                            System.out.println("课程选择成功！");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("课程选择失败！");
                        }
                    } else {
                        System.out.println("你最多只能选择5门课程！");
                    }
                } else {
                    System.out.println("查询错误，没有找到该学生记录！");
                }
            } else {
                System.out.println("该学生不存在，请先注册或检查学生ID！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库操作失败！");
        }
    }



    // 退选课程
    private static void dropCourse(int studentId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要退选的课程ID：");
        int courseId = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            String sql = "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
            System.out.println("退选成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查看已选课程
    private static void showSelectedCourses(int studentId) {
        System.out.println("查询 student_id: " + studentId + " 的已选课程");
        try (Connection conn = main.getConnection()) {
            String sql = "SELECT c.course_id, c.course_name, c.credits FROM student_courses sc "
                    + "JOIN courses c ON sc.course_id = c.course_id WHERE sc.student_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                System.out.println(rs.getInt("course_id") + ". " + rs.getString("course_name") + " - " + rs.getInt("credits") + "学分");
                found = true;
            }
            if (!found) {
                System.out.println("没有找到已选课程！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库查询错误！");
        }
    }

    // 更新手机号
    private static void updatePhone(int studentId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入新的手机号：");
        String newPhone = scanner.next();

        try (Connection conn = main.getConnection()) {
            String sql = "UPDATE students SET phone = ? WHERE student_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPhone);
            ps.setInt(2, studentId);
            ps.executeUpdate();
            System.out.println("手机号更新成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询所有学生
    private static void showAllStudents() {
        try (Connection conn = main.getConnection()) {
            String sql = "SELECT s.student_id, s.name, u.username FROM students s "
                    + "JOIN user u ON s.user_id = u.user_id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("student_id") + ". " + rs.getString("name") + " - " + rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改学生手机号
    private static void updateStudentPhone() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入学生ID：");
        int studentId = scanner.nextInt();
        System.out.print("请输入新的手机号：");
        String newPhone = scanner.next();

        try (Connection conn = main.getConnection()) {
            String sql = "UPDATE students SET phone = ? WHERE student_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPhone);
            ps.setInt(2, studentId);
            ps.executeUpdate();
            System.out.println("手机号更新成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 添加课程
    private static void addCourse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入课程名称：");
        String courseName = scanner.nextLine();
        System.out.print("请输入课程学分：");
        int credits = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            String sql = "INSERT INTO courses (course_name, credits) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, courseName);
            ps.setInt(2, credits);
            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("课程添加成功！");
            } else {
                System.out.println("课程添加失败！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除课程
    private static void deleteCourse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要删除的课程ID：");
        int courseId = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            String sql = "DELETE FROM courses WHERE course_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("课程删除成功！");
            } else {
                System.out.println("课程ID不存在！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 切换课程开放状态
    private static void toggleCourseStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入课程ID：");
        int courseId = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            // 1. 查询当前状态
            String query = "SELECT is_open FROM courses WHERE course_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean currentStatus = rs.getBoolean("is_open");
                boolean newStatus = !currentStatus; // 取反，实现开关功能

                // 2. 更新状态
                String updateSql = "UPDATE courses SET is_open = ? WHERE course_id = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setBoolean(1, newStatus);
                updatePs.setInt(2, courseId);
                int result = updatePs.executeUpdate();

                if (result > 0) {
                    System.out.println("课程状态修改成功！当前状态：" + (newStatus ? "开放" : "关闭"));
                } else {
                    System.out.println("修改失败，请检查课程ID是否正确！");
                }
            } else {
                System.out.println("未找到该课程，请检查ID是否正确！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询所有课程
    private static void showAllCourses() {
        try (Connection conn = main.getConnection()) {
            String sql = "SELECT * FROM courses";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("course_id") + ". " + rs.getString("course_name") + " - " + rs.getInt("credits") + "学分");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改课程学分
    private static void updateCourseCredits() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要修改的课程ID：");
        int courseId = scanner.nextInt();
        System.out.print("请输入新的学分：");
        int newCredits = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            String sql = "UPDATE courses SET credits = ? WHERE course_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, newCredits);
            ps.setInt(2, courseId);
            ps.executeUpdate();
            System.out.println("课程学分更新成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询某课程的学生名单
    private static void showCourseStudents() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入课程ID：");
        int courseId = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            String sql = "SELECT s.name, u.username FROM students s "
                    + "JOIN student_courses sc ON s.student_id = sc.student_id "
                    + "JOIN user u ON s.user_id = u.user_id "
                    + "WHERE sc.course_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("name") + " - " + rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询某学生的选课情况
    private static void showStudentCourses() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入学生ID：");
        int studentId = scanner.nextInt();

        try (Connection conn = main.getConnection()) {
            String sql = "SELECT c.course_name, c.credits FROM student_courses sc "
                    + "JOIN courses c ON sc.course_id = c.course_id "
                    + "WHERE sc.student_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("course_name") + " - " + rs.getInt("credits") + "学分");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
