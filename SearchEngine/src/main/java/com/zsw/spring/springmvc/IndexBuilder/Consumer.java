package com.zsw.spring.springmvc.IndexBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class Consumer implements Runnable{

	private final String indexpath="E:\\DataIndex";
	private IndexWriter indexWriter;
	private Directory directory;
	private SmartChineseAnalyzer analyzer;
	private IndexWriterConfig indexWriterConfig;
	Consumer(){
		
	}
	@Override
	public  void run() {
		while(true){
			
				indexWriter = null;
				File root  = null;
				try {
					directory = FSDirectory.open(FileSystems.getDefault().getPath(indexpath));
					analyzer = new SmartChineseAnalyzer();
		            indexWriterConfig = new IndexWriterConfig(analyzer);
		            indexWriter = new IndexWriter(directory, indexWriterConfig);
					root = IndexBuilder.pools.poll();
					File[] file = root.listFiles();
					System.out.println(Thread.currentThread()+" 正在处理文件夹："+root.getName());
					for(File f:file){
						Document document = new Document();
						String[] t = readTextFile(f);
	            		document.add(new Field("content", t[1], TextField.TYPE_STORED));
	            		document.add(new Field("filename", f.getName(), TextField.TYPE_STORED));
	            		document.add(new Field("filepath",f.getAbsolutePath(),TextField.TYPE_STORED));
	            		document.add(new Field("answer_url",t[0],TextField.TYPE_STORED));
	            		indexWriter.addDocument(document);
//	            		System.out.println(Thread.currentThread()+" 正在处理文件："+f.getName()+" 文件夹为："+root.getName());
					}
	//				System.out.println("Filename:"+file.getName());
	//				Thread.sleep(100);
				} catch (Exception e) {
					
					e.printStackTrace();
				}finally {
		            try {
		                if (indexWriter != null) {
		                    indexWriter.close();
		                }
//		                System.out.println(Thread.currentThread());
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
				}
		}
		
	}
	
	public static synchronized String[] readTextFile(File file){
		String[] t = new String[2];
		try {
			String txt="";
			String answer_url="";
			String encoding = "gbk";
			if(file.isFile()&&file.exists()){
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				
				
				answer_url = bufferedReader.readLine();
				//System.out.println("answer_url:"+bufferedReader.readLine());
				//Thread.sleep(50000);
				String lineText = null;
				while((lineText = bufferedReader.readLine())!=null){
					txt+=lineText;
				}
				//System.out.println(txt);
				read.close();
				t[0]=answer_url;
				t[1]=txt;
				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		return t;
	}

	
}