package com.zsw.spring.springmvc.controller;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zsw.spring.springmvc.service.SearchEngineService;
import com.zsw.spring.springmvc.util.ResponseSearch;

@Controller
public class SearchEngine {

	@Resource(name = "searchEngineService")
	private SearchEngineService searchEngineService;
	
	@RequestMapping("/search")
	public void doSearch(HttpServletRequest req,HttpServletResponse resp){
		try {
			req.setCharacterEncoding("UTF-8");
			System.out.println(req.getParameter("Search")); 
			Set<ResponseSearch> res= this.searchEngineService.getResponseSearch(req.getParameter("Search"));
			for (ResponseSearch responseSearch : res) {
				System.out.println(responseSearch.getFilename());
				System.out.println(responseSearch.getContent());
				System.out.println(responseSearch.getUrl());
			}
//			return res;
		} catch (Exception e) {
			e.printStackTrace(); 
//			return null;
		}
	}
}
