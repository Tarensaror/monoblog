package de.hska.lkit.demo.web.redis.model;

import java.io.Serializable;

public class UUIDSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String uuid;
	private String userid;
	private String name;
	private String password;
	
	public String getUUID() {
		return uuid;
	}
	
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	public String getUserID() {
		return userid;
	}
	
	public void setUserID(String userid) {
		this.userid = userid;
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