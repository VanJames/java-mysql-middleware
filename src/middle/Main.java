package middle;

import java.sql.*;
import java.util.ArrayList;

import middle.data.Mysql;



public class Main {

    public static void main(String[] args) {
        ArrayList<String> sqls = new ArrayList<String>();
        //从队列中取出sql数据放入list中
        for(int i = 0;i<1000;i++){
            sqls.add("INSERT INTO users(name,email,password)VALUES('test_"+(i+4102)+"','test"+(i+4002)+"@test.com','12324')");
        }
        //初始化数据库连接池
        Mysql pool = Mysql.getInstance();
        //初始化sql执行线程池
        SqlThread st = new SqlThread(sqls,pool);
        for(int i = 0 ; i <4;i++){
            new Thread(st,(i+1)+"号执行sql线程").start();
        }
    }

    public static void test(){
        String sql = "select * from `user`";
        long start = System.currentTimeMillis();
        Mysql pool = null;

        for (int i = 0; i < 100; i++) {
            try{
                pool = Mysql.getInstance();
                Connection conn = pool.getConnection();
                if(conn!=null){
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        //System.out.println(rs.getString(1));
                    }
                    rs.close();
                    stmt.close();
                    pool.release(conn);
                }

            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        pool.closePool();
        System.out.println("经过100次的循环调用，使用连接池花费的时间:" + (System.currentTimeMillis() - start) + "ms\n");

        String hostName = "192.168.34.10";
        String driverClass = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://"+hostName+":3306/mysql";
        String user = "root";
        String password = "";
        start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            try{
                Class.forName(driverClass);
            }
            catch (ClassNotFoundException ex){
                System.out.println(ex.getMessage());
            }
            try{
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    //System.out.println("host:"+rs.getString(1));
                }
                rs.close();
                stmt.close();
                conn.close();
            }
            catch (SQLException ex){
                System.out.println(ex.getMessage());
            }

        }
        System.out.println("经过100次的循环调用，不使用连接池花费的时间:" + (System.currentTimeMillis() - start) + "ms");
    }
}
