package com.zsw.spring.springmvc.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

@Repository
public class Results {

	private int MaxSize;
	
	@Resource
	private List<SearchResult> searchResultList;
	
	public Results(){
		this.MaxSize = 100;
		searchResultList = new ArrayList<SearchResult>();
	}
	Results(int size){
		
		this.MaxSize = size;
		
	}
	
	public List<SearchResult> getSearchResultList() {
		return searchResultList;
	}
	public void setSearchResultList(List<SearchResult> searchResultList) {
		this.searchResultList = searchResultList;
	}
	
	public void addToList(SearchResult searchResult){
		if(searchResult!=null)
			this.searchResultList.add(searchResult);
		else{
			throw new NullPointerException();
		}
	}
	
	public void addList(List<SearchResult> s){
		searchResultList.addAll(s);
	}
	
	public void deleteListNode(SearchResult searchResult){
		searchResultList.remove(searchResult);
	}
}
