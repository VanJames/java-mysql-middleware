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
    private final ArrayList<String> sqls;
    //mysql连接池
    private Mysql pool;

    /**
     * 构造函数
     * @param sqls ArrayList<String>
     * @param pool Mysql
     */
    public SqlThread(final ArrayList<String> sqls , Mysql pool){
        this.sqls = sqls;
        this.pool = pool;
    }

    /**
     * 执行线程
     */
    public void run(){
        //线程执行开始时间
        long start = System.currentTimeMillis();
        //内存块锁 防止重复执行
        synchronized (this.sqls){
            for (int i=0;i<this.sqls.size();i++){
                try{
                    //从连接池中取出一个连接 来进行sql操作
                    Connection conn = this.pool.getConnection();
                    if(conn!=null){
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate(this.sqls.get(i));
                        //执行之后移除该队咧成员 并释放该连接
                        this.sqls.remove(i);
                        stmt.close();
                        this.pool.release(conn);
                    }

                }catch(SQLException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
        System.out.println(Thread.currentThread().getName()
                +"花费的时间:"
                + (System.currentTimeMillis() - start)
                + "ms\n");
    }
}
