package com.zsw.spring.springmvc.dao;

import org.springframework.stereotype.Repository;

import com.zsw.spring.springmvc.bean.User;

@Repository
public class UserDao {

	public User selectByUsername(String username) {
		
		
		if ("admin".equals(username)) {
			
			User user = new User();
			user.setPassword("123");
			user.setUsername(username);
			return user;
		}
		return null;
	}

}
