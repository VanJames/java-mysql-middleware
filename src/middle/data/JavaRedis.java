package middle.data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;
/**
 * Created by fanxu(746439274@qq.com) on 15/7/3.
 */
public class JavaRedis {
    private Jedis jedis;//非切片额客户端连接
    private JedisPool jedisPool;//非切片连接池
    private ShardedJedis shardedJedis;//切片额客户端连接
    private ShardedJedisPool shardedJedisPool;//切片连接池

    private static JavaRedis single = null;

    public static JavaRedis getInstance(){
        if(single==null){
            single = new JavaRedis();
        }
        return single;
    }

    public JavaRedis()
    {
        initialPool();
        initialShardedPool();
        shardedJedis = shardedJedisPool.getResource();
        jedis = jedisPool.getResource();
    }

    /**
     * 初始化非切片池
     */
    private void initialPool()
    {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
       //config.setMaxActive(20);
        config.setMaxIdle(5);
        //config.setMaxWait(1000l);
        config.setTestOnBorrow(false);

        jedisPool = new JedisPool(config,"192.168.34.10",6379);
    }

    /**
     * 初始化切片池
     */
    private void initialShardedPool()
    {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        //config.setMaxActive(20);
        config.setMaxIdle(5);
        //config.setMaxWait(1000l);
        config.setTestOnBorrow(false);
        // slave链接
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo("192.168.34.10", 6379, "master"));

        // 构造池
        shardedJedisPool = new ShardedJedisPool(config, shards);
    }

    public void show() {
        //KeyOperate();
        //StringOperate();
        //ListOperate();
        //SetOperate();
        SortedSetOperate();
        //HashOperate();
    }

    public Long getSortCount(String key){
        return shardedJedis.zcard(key);
    }


    public Set<String> getSortList(String key,int page,int pageSize){
        return shardedJedis.zrange(key, (page-1)*pageSize, page*pageSize);
    }

    public void sortAdd(String key,String value,Long score){
        value = value+";"+score;
        shardedJedis.zadd(key, score,value );
    }

    public void SortRemove(String key,String value){
        shardedJedis.zrem(key, value);
    }

    public void close(){
        jedisPool.returnResource(jedis);
        shardedJedisPool.returnResource(shardedJedis);
    }

    private void KeyOperate() {
        System.out.println("======================key==========================");
        // 清空数据
        System.out.println("清空库中所有数据："+jedis.flushDB());
        // 判断key否存在
        System.out.println("判断key999键是否存在："+shardedJedis.exists("key999"));
        System.out.println("新增key001,value001键值对："+shardedJedis.set("key001", "value001"));
        System.out.println("判断key001是否存在："+shardedJedis.exists("key001"));
        // 输出系统中所有的key
        System.out.println("新增key002,value002键值对："+shardedJedis.set("key002", "value002"));
        System.out.println("系统中所有键如下：");
        Set<String> keys = jedis.keys("*");
        Iterator<String> it=keys.iterator() ;
        while(it.hasNext()){
            String key = it.next();
            System.out.println(key);
        }
        // 删除某个key,若key不存在，则忽略该命令。
        System.out.println("系统中删除key002: "+jedis.del("key002"));
        System.out.println("判断key002是否存在："+shardedJedis.exists("key002"));
        // 设置 key001的过期时间
        System.out.println("设置 key001的过期时间为5秒:"+jedis.expire("key001", 5));
        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
        }
        // 查看某个key的剩余生存时间,单位【秒】.永久生存或者不存在的都返回-1
        System.out.println("查看key001的剩余生存时间："+jedis.ttl("key001"));
        // 移除某个key的生存时间
        System.out.println("移除key001的生存时间："+jedis.persist("key001"));
        System.out.println("查看key001的剩余生存时间："+jedis.ttl("key001"));
        // 查看key所储存的值的类型
        System.out.println("查看key所储存的值的类型："+jedis.type("key001"));
        /*
         * 一些其他方法：1、修改键名：jedis.rename("key6", "key0");
         *             2、将当前db的key移动到给定的db当中：jedis.move("foo", 1)
         */
    }

    private void StringOperate() {
    }

    private void ListOperate() {
        System.out.println("======================list==========================");
        // 清空数据
        System.out.println("清空库中所有数据："+jedis.flushDB());

        System.out.println("=============增=============");
        shardedJedis.lpush("stringlists", "vector");
        shardedJedis.lpush("stringlists", "ArrayList");
        shardedJedis.lpush("stringlists", "vector");
        shardedJedis.lpush("stringlists", "vector");
        shardedJedis.lpush("stringlists", "LinkedList");
        shardedJedis.lpush("stringlists", "MapList");
        shardedJedis.lpush("stringlists", "SerialList");
        shardedJedis.lpush("stringlists", "HashList");
        shardedJedis.lpush("numberlists", "3");
        shardedJedis.lpush("numberlists", "1");
        shardedJedis.lpush("numberlists", "5");
        shardedJedis.lpush("numberlists", "2");
        System.out.println("所有元素-stringlists："+shardedJedis.lrange("stringlists", 0, -1));
        System.out.println("所有元素-numberlists："+shardedJedis.lrange("numberlists", 0, -1));

        System.out.println("=============删=============");
        // 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
        System.out.println("成功删除指定元素个数-stringlists："+shardedJedis.lrem("stringlists", 2, "vector"));
        System.out.println("删除指定元素之后-stringlists："+shardedJedis.lrange("stringlists", 0, -1));
        // 删除区间以外的数据
        System.out.println("删除下标0-3区间之外的元素："+shardedJedis.ltrim("stringlists", 0, 3));
        System.out.println("删除指定区间之外元素后-stringlists："+shardedJedis.lrange("stringlists", 0, -1));
        // 列表元素出栈
        System.out.println("出栈元素："+shardedJedis.lpop("stringlists"));
        System.out.println("元素出栈后-stringlists："+shardedJedis.lrange("stringlists", 0, -1));

        System.out.println("=============改=============");
        // 修改列表中指定下标的值
        shardedJedis.lset("stringlists", 0, "hello list!");
        System.out.println("下标为0的值修改后-stringlists："+shardedJedis.lrange("stringlists", 0, -1));
        System.out.println("=============查=============");
        // 数组长度
        System.out.println("长度-stringlists："+shardedJedis.llen("stringlists"));
        System.out.println("长度-numberlists："+shardedJedis.llen("numberlists"));
        // 排序
        /*
         * list中存字符串时必须指定参数为alpha，如果不使用SortingParams，而是直接使用sort("list")，
         * 会出现"ERR One or more scores can't be converted into double"
         */
        SortingParams sortingParameters = new SortingParams();
        sortingParameters.alpha();
        sortingParameters.limit(0, 3);
        System.out.println("返回排序后的结果-stringlists："+shardedJedis.sort("stringlists",sortingParameters));
        System.out.println("返回排序后的结果-numberlists："+shardedJedis.sort("numberlists"));
        // 子串：  start为元素下标，end也为元素下标；-1代表倒数一个元素，-2代表倒数第二个元素
        System.out.println("子串-第二个开始到结束："+shardedJedis.lrange("stringlists", 1, -1));
        // 获取列表指定下标的值
        System.out.println("获取下标为2的元素："+shardedJedis.lindex("stringlists", 2)+"\n");
    }

    private void SetOperate() {
    }

    private void SortedSetOperate() {
        System.out.println("======================zset==========================");
        // 清空数据
        System.out.println(jedis.flushDB());

        System.out.println("=============增=============");
        System.out.println("zset中添加元素element001："+shardedJedis.zadd("zset", 7.0, "element001"));
        System.out.println("zset中添加元素element002："+shardedJedis.zadd("zset", 8.0, "element002"));
        System.out.println("zset中添加元素element003："+shardedJedis.zadd("zset", 2.0, "element003"));
        System.out.println("zset中添加元素element004："+shardedJedis.zadd("zset", 3.0, "element004"));
        System.out.println("zset集合中的所有元素："+shardedJedis.zrange("zset", 0, -1));//按照权重值排序
        System.out.println();

        System.out.println("=============删=============");
        System.out.println("zset中删除元素element002："+shardedJedis.zrem("zset", "element002"));
        System.out.println("zset集合中的所有元素："+shardedJedis.zrange("zset", 0, -1));
        System.out.println();

        System.out.println("=============改=============");
        System.out.println();

        System.out.println("=============查=============");
        System.out.println("统计zset集合中的元素中个数："+shardedJedis.zcard("zset"));
        System.out.println("统计zset集合中权重某个范围内（1.0——5.0），元素的个数："+shardedJedis.zcount("zset", 1.0, 5.0));
        System.out.println("查看zset集合中element004的权重："+shardedJedis.zscore("zset", "element004"));
        System.out.println("查看下标1到2范围内的元素值："+shardedJedis.zrange("zset", 1, 2));
    }

    private void HashOperate() {
    }
}
