package com.jack.dataobject;

public class MemberInfoObject {

	private int id;
	private String bio;
	private String avatar_large;
	
	public void setId(int id){
		this.id=id;
	}
	
	public int getId(){
		return this.id;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public void setAvator_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}
	
}
