package whatschat;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class JedisDB {
	
    private static final String REDIS_HOST = "redis-19259.c1.ap-southeast-1-1.ec2.cloud.redislabs.com";
    private static final int REDIS_PORT = 19259;
    private static final int REDIS_TIMEOUT = 10000;
    private static final String REDIS_PASSWORD = "hnL4CUSUmQvmvBe"; 
    
    private static JedisPool pool = null;
    
    public JedisDB() {
    	pool = new JedisPool(new JedisPoolConfig(), REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT, REDIS_PASSWORD);
    	
    }
    
    // (Key) Group Name ; (Value) User ID
    public void addMember(String name, String id) {
    	Jedis jedis = pool.getResource();
    	try {
    		jedis.rpush(name, id);
    	} catch (JedisException e) {
    		if (null != jedis) {
    			jedis.close();
            }
        } finally {
            if (null != jedis)
            	jedis.close();
        }
    }
    
    public void removeMember(String name, String id) {
    	Jedis jedis = pool.getResource();
    	
    	try {
    		jedis.srem(name, id);
    	} catch (JedisException e) {
    		//if something wrong happen, return it back to the pool
    		if (null != jedis) {
    			jedis.close();
    		}
    	} finally {
    		if (null != jedis) {
    			jedis.close();
    		}
    	}
    }
    
    public List<String> getMembers(String name) {
    	Jedis jedis = pool.getResource();
    	try {
    		List<String> memberList = jedis.lrange(name, 0, -1);
    		return memberList;
    	} catch (JedisException e) {
    		if (null != jedis) {
    			jedis.close();
            }
        } finally {
            if (null != jedis)
            	jedis.close();
        }
    	return null;
    }
    
    // (Key) IP Address ; (Value) Chat Message
    public void addMessage(String ip, String msg) {
    	Jedis jedis = pool.getResource();
    	try {
    		jedis.rpush(ip, msg);
    	} catch (JedisException e) {
    		if (null != jedis) {
                jedis.close();
            }
        } finally {
            if (null != jedis) 
            	jedis.close();
        }
    }
    
    public List<String> getMessages(String ip) {
    	Jedis jedis = pool.getResource();
    	try {
    		// To get the last 10 messages
    		List<String> messageList = jedis.lrange(ip, -10, -1);
    		return messageList;
    	} catch (JedisException e) {
    		if (null != jedis) {
    			jedis.close();
            }
        } finally {
            if (null != jedis)
            	jedis.close();
        }
    	return null;
    }
}
