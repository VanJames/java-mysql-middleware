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
        //mysql连接池
        Mysql pool = Mysql.getInstance();
        //redis单次持久连接
        JavaRedis jRedis = JavaRedis.getInstance();
        while(true){
            //当前队列中总共有多少数据
            Long count = jRedis.getSortCount(sqlKey);
            if(count>0){
                //每次取出多少条语句进行执行
                int pageSize = 1000;
                //总页数
                Long allPage = count%pageSize==0? count/pageSize:(count/pageSize+1);
                for(int page=0;page<allPage;page++){
                    //队列先进先出 从最开始进的地方开始取最老的1000条
                    Set<String> list = jRedis.getSortList(sqlKey,page,pageSize);
                    for( String member : list ){
                        //移除该member
                        jRedis.SortRemove(sqlKey,member);
                        //将member进行分割获取将要执行的sql语句member生成规则"{sql}_{unixTime}"
                        String[] str= member.split(";");
                        if(str.length>0){
                            //生成一条线程去执行
                            new SqlThread(str[0],pool).run();
                        }
                    }
                }
                System.out.println("共"+SqlThread.threadCount+"条线程,等待线程执行结束！");
            }
            else{
                System.out.println("等待新数据...");
            }
        }

    }
}
