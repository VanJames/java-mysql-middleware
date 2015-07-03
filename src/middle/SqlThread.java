package middle;

import middle.data.Mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by fanxu(746439274@qq.com) on 15/7/2.
 */
public class SqlThread implements Runnable{

    //sql队列
    //private final ArrayList<String> sqls;
    //mysql连接池
    private Mysql pool;
    //每条线程需要处理的个数
    private int perCount;
    //mysql连接池
    private String sql;
    //总的线程数
    public static int threadCount = 100;

    /**
     * 构造函数
     * @param sqls ArrayList<String>
     * @param pool Mysql
     */
    public SqlThread(final ArrayList<String> sqls , Mysql pool){
//        this.sqls = sqls;
//        this.pool = pool;
//        int sqlLength = sqls.size();
//        this.perCount = sqlLength%threadCount==0?
//                sqlLength/threadCount:
//                (sqlLength/threadCount+1);
    }

    /**
     * 构造函数
     * @param sql String
     * @param pool Mysql
     */
    public SqlThread(String sql , Mysql pool){
        this.sql = sql;
        this.pool = pool;
    }

    /**
     * 执行线程
     */
    public void run(){
        //线程执行开始时间
        long start = System.currentTimeMillis();
        try{
                //从连接池中取出一个连接 来进行sql操作
                Connection conn = this.pool.getConnection();
                if(conn!=null){
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(this.sql);
                    //执行之后移除该队咧成员 并释放该连接
                    stmt.close();
                    this.pool.release(conn);
                }

            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
//        int currentIndex = Integer.parseInt(Thread.currentThread().getName());
//
//        for (int i=(currentIndex-1)*this.perCount;i<currentIndex*this.perCount;i++){
//            try{
//                //从连接池中取出一个连接 来进行sql操作
//                Connection conn = this.pool.getConnection();
//                if(conn!=null){
//                    Statement stmt = conn.createStatement();
//                    stmt.executeUpdate(this.sqls.get(i));
//                    //执行之后移除该队咧成员 并释放该连接
//                    stmt.close();
//                    this.pool.release(conn);
//                }
//
//            }catch(SQLException ex){
//                System.out.println(ex.getMessage());
//            }
//        }
        System.out.println("线程"+Thread.currentThread().getName()
                +"花费的时间:"
                + (System.currentTimeMillis() - start)
                + "ms\n");
    }
}
