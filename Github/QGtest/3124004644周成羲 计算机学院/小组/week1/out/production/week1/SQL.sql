USE qg;
SELECT @@sql_mode;
SELECT COUNT(*) FROM students WHERE student_id = 3;
-- 用户表
CREATE TABLE user (
                       user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('student', 'admin') NOT NULL
);
ALTER TABLE user AUTO_INCREMENT = 1;

-- 学生表
CREATE TABLE students (
                          student_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          phone VARCHAR(20),
                          FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 课程表
CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         course_name VARCHAR(100) NOT NULL,
                         credits INT NOT NULL,
                         is_open BOOLEAN DEFAULT TRUE

);

-- 选课表
CREATE TABLE student_courses (
                                 student_id INT NOT NULL,
                                 course_id INT NOT NULL,
                                 PRIMARY KEY (student_id, course_id),
                                 FOREIGN KEY (student_id) REFERENCES students(student_id),
                                 FOREIGN KEY (course_id) REFERENCES courses(course_id)
);