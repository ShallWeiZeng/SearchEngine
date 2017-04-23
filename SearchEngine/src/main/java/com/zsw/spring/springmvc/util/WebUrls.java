package com.zsw.spring.springmvc.util;

import java.util.List;

public class WebUrls {
	
	public int length;
	public List<String> urlList;
	public String content;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public List<String> getUrlList() {
		return urlList;
	}
	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
	
	public void addToList(String url){
		if(url!= null||"".equalsIgnoreCase(url)){
			urlList.add(url);
		}
		else{
			throw new NullPointerException(); 
		}
	}

}
