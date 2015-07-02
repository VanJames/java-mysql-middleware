package middle.data;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by fanxu(746439274@qq.com) on 15/5/20.
 */
public class Mysql {
    private Vector<Connection> pool;

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    /**
     * 连接池的大小，也就是连接池中有多少个数据库连接。
     */
    private int poolSize = 100;

    private static Mysql instance = null;

    /**
     * 私有的构造方法，禁止外部创建本类的对象，要想获得本类的对象，通过<code>getIstance</code>方法。
     * 使用了设计模式中的单子模式。
     */
    private Mysql() {
        init();
    }

    /**
     * 连接池初始化方法，读取属性文件的内容 建立连接池中的初始连接
     */
    private void init() {
        pool = new Vector<Connection>(poolSize);
        readConfig();
        addConnection();
    }

    /**
     * 返回连接到连接池中
     */
    public synchronized void release(Connection conn) {
        pool.add(conn);

    }

    /**
     * 关闭连接池中的所有数据库连接
     */
    public synchronized void closePool() {
        for (int i = 0; i < pool.size(); i++) {
            try {
                ((Connection) pool.get(i)).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pool.remove(i);
        }
    }

    /**
     * 返回当前连接池的一个对象
     */
    public static Mysql getInstance() {
        if (instance == null) {
            instance = new Mysql();
        }
        return instance;
    }

    /**
     * 返回连接池中的一个数据库连接
     */
    public synchronized Connection getConnection() {
        if (pool.size() > 0) {
            Connection conn = pool.get(0);
            pool.remove(conn);
            return conn;
        } else {
            return null;
        }
    }

    /**
     * 在连接池中创建初始设置的的数据库连接
     */
    private void addConnection() {
        Connection conn = null;
        for (int i = 0; i < poolSize; i++) {

            try {
                Class.forName(this.driverClassName);
                try{
                    conn = java.sql.DriverManager.getConnection(this.url, this.username, this.password);
                    pool.add(conn);
                }
                catch (SQLException ex){
                    ex.printStackTrace();
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 读取设置连接池的属性文件
     */
    private void readConfig() {
        try {
            String path = System.getProperty("user.dir") + "/dbpool.properties";
            FileInputStream is = new FileInputStream(path);
            Properties props = new Properties();
            props.load(is);
            this.driverClassName = props.getProperty("driverClassName");
            this.username = props.getProperty("username");
            this.password = props.getProperty("password");
            this.url = props.getProperty("url");
            this.poolSize = Integer.parseInt(props.getProperty("poolSize"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("读取属性文件出错. ");
        }
    }
}
