package com.jack.dataobject;

public class Member {

    private String username;
	private String avatar_normal;
	
	public Member(String username,String avatar_normal){
		this.username=username;
		this.avatar_normal=avatar_normal;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getAvatar_normal() {
		return avatar_normal;
	}
	public void setAvatar_normal(String avatar_normal) {
		this.avatar_normal = avatar_normal;
	}
	
}
