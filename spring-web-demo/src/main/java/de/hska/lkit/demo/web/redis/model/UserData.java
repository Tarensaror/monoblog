package de.hska.lkit.demo.web.redis.model;

import java.io.Serializable;

public class UserData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String password;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}