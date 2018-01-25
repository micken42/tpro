package de.htw_berlin.tpro.users.model;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id; // will be generated later on
	private String username;
	private String password;
	
	private HashMap<String, Permission> permissions;
	
	public User(String username, String password, HashMap<String, Permission> permissions) {
		this.username = username;
		this.password = password;
		this.permissions = permissions;
	}
	
	public User() {
		this("", "", new HashMap<String, Permission>());
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public HashMap<String, Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(HashMap<String, Permission> permissions) {
		this.permissions = permissions;
	}
	
	public boolean isPermittedInContext(String permissionName, String contextName) {
		for (Permission perm: permissions.values()) {
			if (!perm.getContext().getName().equals(contextName))
				continue;
			if (!perm.getName().contentEquals(permissionName))
				continue;
			return true;
		}
		return false;
	}
	
	/*
		public boolean addPermission(Permission permission) {
			if (!permissions.containsKey(permission.getName())) {
				permissions.put(permission.getName(), permission);
			}
			return false;
		}
		public boolean removePermission(String permissionName) {
			try {
				permissions.remove(permissionName);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
	*/
}
