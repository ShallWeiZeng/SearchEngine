package com.zsw.spring.springmvc.util;

import java.nio.file.FileSystems;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneSearch {

	/*
	 * @keyWord 根据提供的关键词来进行搜索
	 * @type 依据类型来针对搜索，两种类型filename,content
	 * @return 返回类型Results类
	 */
	
	
	
	public Results search(String keyWord,String type) {  
        DirectoryReader directoryReader = null;  
        String indexpath = "F:\\DataIndex";
        
        SearchResult sr = new SearchResult();
        try {  
            // 1、创建Directory  
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(indexpath));
            // 2、创建IndexReader  
            directoryReader = DirectoryReader.open(directory);  
            // 3、根据IndexReader创建IndexSearch  
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);  

            // 4、创建搜索的Query  
            SmartChineseAnalyzer  analyzer = new SmartChineseAnalyzer ();  
            // 创建parser来确定要搜索文件的内容，第一个参数为搜索的域  
            QueryParser queryParser = new QueryParser(type, analyzer);  
            // 创建Query表示搜索域为content包含UIMA的文档  
            Query query = queryParser.parse(keyWord);  

            // 5、根据searcher搜索并且返回TopDocs  
            TopDocs topDocs = indexSearcher.search(query, 100);  
            System.out.println("查找到的文档总共有："+topDocs.totalHits);
            
            List<SearchResult> results = new Results().getSearchResultList();
            // 6、根据TopDocs获取ScoreDoc对象  
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;  
            for (ScoreDoc scoreDoc : scoreDocs) {  

                // 7、根据searcher和ScoreDoc对象获取具体的Document对象  
                Document document = indexSearcher.doc(scoreDoc.doc);  
                sr = new SearchResult(); 
                // 8、根据Document对象获取需要的值  
                sr.setFilename(document.get("filename"));
                sr.setAnswer_url(document.get("answer_url"));
                sr.setFilepath(document.get("filepath"));
                results.add(sr);
//              System.out.println(document.get("filename") + " " + document.get("answer_url"));
                
            }
            Results res =new Results();
            res.addList(results);
            return res;

        } catch (Exception e) {  
            e.printStackTrace();
            return null;
        } finally {  
            try {  
                if (directoryReader != null) {  
                    directoryReader.close();  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
