package com.zsw.spring.springmvc.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.swing.text.AbstractDocument.Content;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import com.zsw.spring.springmvc.dao.JedisDao;
import com.zsw.spring.springmvc.util.LuceneSearch;
import com.zsw.spring.springmvc.util.ResponseSearch;
import com.zsw.spring.springmvc.util.Results;
import com.zsw.spring.springmvc.util.SearchResult;
import com.zsw.spring.springmvc.util.WebUrls;

@Service
public class SearchEngineService {

	@Resource(name = "jedisDao")
	private JedisDao jedisDao;
	
	public void test(){
		this.jedisDao.delKey("test");
	}
	
	/*
	 * 先在Redis里面查找是否有缓存，有缓存则不进行搜索
	 * 
	 */
	public Set<ResponseSearch> getResponseSearch(String Key){
		
		Results originalResult = doSearch(Key);
		Set<ResponseSearch> res = this.jedisDao.getKeysByPipeline(originalResult);
		
		return res;
		
	}
	
	/*
	 * 中文分词获取Token
	 */
	public void getToken(String token){
		
		try {
			
			System.out.println("searching");
			
			SmartChineseAnalyzer sca = new SmartChineseAnalyzer( );
			
			TokenStream ts = sca.tokenStream("field", token);  
	        CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
	        
	        ts.reset();  
	        while (ts.incrementToken()) {  
	            System.out.println(ch.toString());  
	        }  
	        ts.end();  
	        ts.close();  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 直接进行查询
	 */
	public Results doSearch(String key){
		Results filenameResults = null;
		Results contentResults = null;
		Results finalResults = null;
		LuceneSearch LS = new LuceneSearch();
		filenameResults = LS.search(key, "filename");
		contentResults = LS.search(key, "content");
		finalResults = doInterSection(filenameResults,contentResults);
		
		return finalResults;
	}
	
	/*
	 * 获取结果集，并对结果集进行求并集操作
	 * 暂定：先从索引中获取，然后求交集
	 * 先判断Redis是否缓存，有缓存则先从Redis里面获取，采用scan的方式来解决
	 * 不能用keys命令来查询
	 */
	public Results doSearch(String[] keys){
		
		Results filenameResults = null;
		Results contentResults = null;
		Results finalResults = null;
		LuceneSearch LS = new LuceneSearch();
		for(String key:keys){
			
			filenameResults = LS.search(key, "filename");
			contentResults = LS.search(key, "content");
			
			if(finalResults!= null){
				finalResults=doInterSectionWithPreResult(finalResults,doInterSection(filenameResults,contentResults));
			}
			else{
				finalResults = doInterSection(filenameResults,contentResults);
			}
		}
		
		
		return finalResults;
	}
	
	/*
	 * 两次针对Filename和content查询结果做交集
	 * 
	 */
	public Results doInterSection(Results filename,Results content){
		
		Set<SearchResult> setResult = new HashSet<SearchResult>(filename.getSearchResultList());
		List<SearchResult> list = content.getSearchResultList();
		Results results = new Results();
		for(SearchResult sr:list){
			if(setResult.contains(sr)){
				results.addToList(sr);
				filename.deleteListNode(sr);
			}
		}
		results.addList(filename.getSearchResultList());
		return results;
	}
	
	/*
	 * 与之前的结果做交集
	 */
	public Results doInterSectionWithPreResult(Results preResult,Results nowResult){
		Set<SearchResult> setResult = new HashSet<SearchResult>(preResult.getSearchResultList());
		List<SearchResult> list = nowResult.getSearchResultList();
		Results results = new Results();
		for(SearchResult sr:list){
			if(setResult.contains(sr)){
				results.addToList(sr);
				preResult.deleteListNode(sr);
			}
		}
		results.addList(preResult.getSearchResultList());
		return results;
	}
	
}
