package de.hska.lkit.demo.redis.model;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String postId;
	private String message;
	private User user;
	private Date date;
	
	public Post() {
		
	}
	
	public String getPostId() {
		return postId;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate() {
		return this.date;
	}
	

}
