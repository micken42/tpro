package de.htw_berlin.tpro.users.model;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
/*@Access(AccessType.FIELD)
@Table(name="User")*/
@NamedQueries({
    @NamedQuery(name = "User.findAll",
            query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUsername",
            query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.getAllUsernames",
            query = "SELECT u.username FROM User u") })
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private @Getter @Setter Integer id; // will be generated later on

	private @Getter @Setter String username;
	private @Getter @Setter String password;
	
	private @Getter @Setter HashMap<String, Permission> permissions;
	
	public User(String username, String password, HashMap<String, Permission> permissions) {
		this.username = username;
		this.password = password;
		this.permissions = permissions;
	}
	
	public User() {
		this("", "", new HashMap<String, Permission>());
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
