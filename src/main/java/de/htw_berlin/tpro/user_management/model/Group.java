package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "Group.findAll",
            query = "SELECT g FROM Group g"),
    @NamedQuery(name = "Group.findByName",
            query = "SELECT g FROM Group g WHERE g.name = :name"),
    @NamedQuery(name = "Group.findAllNames",
    		query = "SELECT g.name FROM Group g"),
    @NamedQuery(name = "Group.findAllByPermissionAndContextName",
			query = "SELECT g FROM Group g JOIN g.permissions p "
				  + "WHERE p.name = :permission and p.context.name = :context"),
	@NamedQuery(name = "Group.findAllByUsername",
			query = "SELECT g FROM Group g JOIN g.users u WHERE u.username = :username")})
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(unique=true, nullable=false)
	private @Getter @Setter String name;
	
	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="Group_Permission", joinColumns=@JoinColumn(name="group_id"), inverseJoinColumns=@JoinColumn(name="permission_id"))
	private @Getter @Setter Set<Permission> permissions;
	
	@ManyToMany(cascade = CascadeType.PERSIST, fetch=FetchType.LAZY)
    @JoinTable(name="Group_User", joinColumns=@JoinColumn(name="group_id"), inverseJoinColumns=@JoinColumn(name="user_id"))
	private @Getter @Setter Set<User> users;

	public Group(String name, Set<Permission> permissions, Set<User> users) {
		this.name = name;
		this.permissions = permissions;
		this.users = users;
	}
	
	public Group(String name) {
		this(name, new HashSet<Permission>(), new HashSet<User>());
	}
	
	public Group() {
		this("",  new HashSet<Permission>(), new HashSet<User>());
	}
	
	public void addPermission(Permission permission) {
		permissions.add(permission);
	}
	
	public void removePermission(Permission permission) throws EntityNotFoundException {
		Permission groupPermission = getMatchingGroupPermission(permission);
		if (groupPermission != null) {
			permissions.remove(groupPermission);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	public Permission getMatchingGroupPermission(Permission permission) {
		for (Permission groupPermission : permissions) {
			if (groupPermission.getId()==permission.getId())
				return groupPermission;
		}
		return null;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
	public void removeUser(User user) throws EntityNotFoundException {
		User groupUser = getMatchingUser(user);
		if (groupUser != null) {
			users.remove(groupUser);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	public User getMatchingUser(User user) {
		for (User groupUser : users) {
			if (groupUser.getId()==user.getId())
				return groupUser;
		}
		return null;
	}

}
