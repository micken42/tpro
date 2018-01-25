package de.htw_berlin.tpro.users.model;

import java.io.Serializable;

public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private Context context;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	
}
