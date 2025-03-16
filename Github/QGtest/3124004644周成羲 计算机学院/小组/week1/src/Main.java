import java.sql.*;

    public class Main {
        public static void main(String[] args) throws ClassNotFoundException, SQLException{
            //1.导入驱动jar包
            //2.注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //3.获取数据库连接对象 Connect
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/qg", "root", "root");
            //4.获取执行sql语句的对象
            String sql = "update student set name = 'KOBE' where age = 18";
            Statement statement = connection.createStatement();
            //5.执行sql语句,处理结果
            int resultSet = statement.executeUpdate(sql);
            System.out.println(resultSet);
            //6.释放资源
            statement.close();
            connection.close();
        }
    }