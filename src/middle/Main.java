package middle;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import middle.data.*;
import java.util.Set;


public class Main {
    private static String sqlKey = "1_sql_key2015";
    public static void main(String[] args) {
        Mysql pool = Mysql.getInstance();
        JavaRedis jRedis = JavaRedis.getInstance();
        //从队列中取出sql数据放入list中
//        for(int i = 0;i<1000;i++){
//            jRedis.sortAdd(sqlKey,
//                    "INSERT INTO users(name,email,password)VALUES('test_" + (i + 1102) + "','test" + (i + 1002) + "@test.com','12324')"
//            ,System.currentTimeMillis()
//            );
//        }

        while(true){
            Long count = jRedis.getSortCount(sqlKey);
            if(count>0){
                int pageSize = 1000;
                //初始化数据库连接池
                Long allPage = count%pageSize==0? count/pageSize:(count/pageSize+1);
                for(int page=0;page<allPage;page++){
                    Set<String> list = jRedis.getSortList(sqlKey,page,pageSize);
                    for( String s:list ){
                        //移除该条纪录
                        jRedis.SortRemove(sqlKey,s);
                        String[] str= s.split(";");
                        if(str.length>0){
                            new SqlThread(str[0],pool).run();
                        }
                    }
                }
                System.out.println("共"+SqlThread.threadCount+"条线程,等待线程执行结束！");
            }
            else{
                System.out.println("等待队列新数据中！");
            }
        }

//        ArrayList<String> sqls = new ArrayList<String>();
//        //从队列中取出sql数据放入list中
//        for(int i = 0;i<100000;i++){
//            sqls.add("INSERT INTO users(name,email,password)VALUES('test_"+(i+1102)+"','test"+(i+1002)+"@test.com','12324')");
//        }
//        System.out.println("总队列数:" + sqls.size() );
//        //初始化数据库连接池
//        Mysql pool = Mysql.getInstance();
//        //初始化sql执行线程池
//        SqlThread st = new SqlThread(sqls,pool);
//        //同步创建所有线程
//        for(int i = 0 ; i <SqlThread.threadCount;i++){
//            new Thread(st,i+1+"").start();
//        }

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
