package com.jack.dataobject;


public class ReplyObject {

	private int id;
	private String content_rendered;
	private Member member;
	
	public ReplyObject(int id,String content_rendered,Member member){
		this.id=id;
		this.content_rendered=content_rendered;
		this.member=member;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent_rendered() {
		return content_rendered;
	}

	public void setContent_rendered(String content_rendered) {
		this.content_rendered = content_rendered;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
}
  