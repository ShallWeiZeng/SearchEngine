package com.zsw.spring.springmvc.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.zsw.spring.springmvc.bean.JedisSource;
import com.zsw.spring.springmvc.util.ResponseSearch;
import com.zsw.spring.springmvc.util.Results;
import com.zsw.spring.springmvc.util.SearchResult;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

@Repository
public class JedisDao {
	
	@Resource(name = "jedisSource")
	private JedisSource jedisSource;
	
	public void addKey(String key,String value){
		
		Jedis jedis = null;
		try {
			jedis = jedisSource.getJedisInstance();
			jedis.set(key, value);
			System.out.println("insert success");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				jedisSource.returnResource(jedis);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void delKey(String key){
		
		Jedis jedis = null;
		try{
			jedis = this.jedisSource.getJedisInstance();
			System.out.println("jedis key:"+key+" value:"+jedis.get(key));
			jedis.del("test");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Set<ResponseSearch> getKeysByPipeline(Results results){
		
		List<SearchResult> list = results.getSearchResultList();
		Jedis jedis = jedisSource.getJedisInstance();
		Pipeline pipeline=null; 
		try {
			pipeline = jedis.pipelined();
			Map<String,Response<Map<String,String>>> responses = new HashMap<String,Response<Map<String,String>>>(list.size());
			for(SearchResult searchResult:list){
				System.out.println(searchResult.getAnswer_url());
				responses.put(searchResult.getAnswer_url(), pipeline.hgetAll(searchResult.getAnswer_url()));
				System.out.println();
//				pipeline.hg
			}
			pipeline.sync();
			ResponseSearch responseSearch = new ResponseSearch();
			Set<ResponseSearch> res = new HashSet<ResponseSearch>();
			for(String k:responses.keySet()){
				Map<String,String> map = responses.get(k).get();
				responseSearch.setUrl(k);
				for (Map.Entry<String, String> entry : map.entrySet()){
					System.out.println(entry.getKey());
					if(entry.getKey().equalsIgnoreCase("content")){
						responseSearch.setContent(entry.getValue());
					}
					if(entry.getKey().equals("filename")){
						responseSearch.setFilename(entry.getValue());
					}
				}
				res.add(responseSearch);
				responseSearch = new ResponseSearch();
			}
			return res;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(jedis!=null){
				jedisSource.returnResource(jedis);
			}
		}
		
	}
	
	

}
