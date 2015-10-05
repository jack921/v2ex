package com.jack.dataobject;


public class AllNodeObject {

	private int id;
	private String name; 
	private String title;
	private String header;
	private int saveState=0;
	
	public AllNodeObject(int id,String title,String header,int saveState){
		this.id=id;
		this.title=title;
		this.header=header;
		this.saveState=saveState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSaveState() {
		return saveState;
	}

	public void setSaveState(int saveState) {
		this.saveState = saveState;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
}
