package com.zsw.spring.springmvc.bean;

import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Repository
public class JedisSource {

	private static JedisPool jedisPool = null;

    private int maxIdle = 10;
    public void setMaxIdle(int maxIdle){
        this.maxIdle = maxIdle;
    }

    private int maxTotal= 50;
    public void setMaxTotal(int maxTotal){
        this.maxTotal = maxTotal;
    }

    private long maxWaitMills = 5000;
    public void setMaxWaitMills(long maxWaitMills){
        this.maxWaitMills = maxWaitMills;
    }

    private String host="localhost";
    public void setHost(String host){
        this.host = host;
    }

    private int port = 6379;
    public void setPort(int port){
        this.port = port;
    }

    private int timeOut=18000;
    public void setTimeOut(int timeOut){
        this.timeOut = timeOut;
    }

    private String password  ="";
    public void setPassword(String password){
        this.password=password;
    }
    public void initialPool(){
        try{

            final JedisPoolConfig config=new JedisPoolConfig();
            config.setMaxTotal(maxTotal);
            config.setMaxIdle(maxIdle);
            config.setMaxWaitMillis(maxWaitMills);
            config.setTestOnBorrow(false);

            jedisPool = new JedisPool(config,host,port,timeOut);

            System.out.println("************************************");
            System.out.println("JedisPool is created :host:"+host+" port:"+port+" password:"+password);
            System.out.println("************************************");
            


        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }
    
    public synchronized Jedis getJedisInstance(){
        try{
            if(jedisPool != null){
                Jedis resource = jedisPool.getResource();
                
                return resource;
            }
            else{
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    /*
     * 释放Jedis资源
     */
    @SuppressWarnings("deprecation")
	public void returnResource(final Jedis jedis){
        if(jedis != null){
            jedisPool.returnResource(jedis);
        }
    }
}
