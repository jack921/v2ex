package com.jack.dataobject;

public class DataObject {

	private int id;
	private String title;
	private String content_rendered;
	private int replies;
	private String time;
	private Member member;
	private Node node;
	
	public DataObject(int id,String title,String content_rendered,int replies,String time,Member member,Node node){
		this.id=id;
		this.title=title;
		this.content_rendered=content_rendered;
		this.replies=replies;
		this.time=time;
		this.member=member;
		this.node=node;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent_rendered() {
		return content_rendered;
	}

	public void setContent_rendered(String content_rendered) {
		this.content_rendered = content_rendered;
	}

	public int getReplies() {
		return replies;
	}
	public void setReplies(int replies) {
		this.replies = replies;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	
	
}
