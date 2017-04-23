package com.zsw.spring.springmvc.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zsw.spring.springmvc.bean.User;
import com.zsw.spring.springmvc.dao.UserDao;

@Service
public class UserService {

	@Resource
	UserDao dao;

	public User doLogin(String username, String password) throws Exception{


		if(username == null || "".equals(username)){
			throw new Exception("username cannot be empty");
		}
		
		if(password == null ||"".equals(password)){
			throw new Exception("password cannot be empty");
		}
		
		
		User user  = dao.selectByUsername(username);
		
		if(user == null){
			throw new Exception("username is not exist");
		}
		
		if (!user.getPassword().equals(password)) {
			throw new Exception("password error");
		}
		
		return user;
	}
}
