package de.hska.lkit.demo.web;

import java.util.ArrayList;

public class LoginResponse {
	private String token;
	private String name;
	private ArrayList<String> following;
	
	public LoginResponse(String token, String name, ArrayList<String> following) {
		this.token = token;
		this.name = name;
		this.following = following;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getFollowing() {
		return following;
	}
}