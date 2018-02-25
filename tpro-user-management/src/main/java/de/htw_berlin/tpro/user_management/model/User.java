package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "User.findAll",
            query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUsername",
            query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findAllUsernames",
    		query = "SELECT u.username FROM User u"),
	@NamedQuery(name = "Group.findAllByGroupName",
			query = "SELECT u FROM User u JOIN u.groups g WHERE g.name = :name"),
    @NamedQuery(name = "User.findAllByPermissionAndContextName",
    		query = "SELECT u FROM User u JOIN u.permissions p "
    			  + "WHERE p.name = :permission and p.context.name = :context")})
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(unique=true, nullable=false)
	private @Getter @Setter String username;

	@NotNull
	private @Getter @Setter String prename;
	
	@NotNull
	private @Getter @Setter String surname;
	
	@Column(unique=true, nullable=false)
	private @Getter @Setter String email;
	
	// TODO: Passwort sicher speichern + SSL enablen
	@NotNull
	private @Getter @Setter String password;
	
	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="User_Permission", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="permission_id"))
	private @Getter @Setter Set<Permission> permissions;
	
	@ManyToMany(fetch=FetchType.EAGER, mappedBy="users")
	private @Getter @Setter Set<Group> groups;

	public User(String prename, String surname,  String email, String username, String password, Set<Permission> permissions, Set<Group> groups) {
		this.prename = prename;
		this.surname = surname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.permissions = permissions;
		this.groups = groups;
	}
	
	public User(String prename, String surname, String email, String username, String password) {
		this(prename, surname, email ,username, password, new HashSet<Permission>(), new HashSet<Group>());
	}
	
	public User(String prename, String surname, String username, String password) {
		this(prename, surname, username + "@mail.de",username, password, new HashSet<Permission>(), new HashSet<Group>());
	}
	
	public User(String username, String password) {
		this("", "", username + "@mail.de",username, password, new HashSet<Permission>(), new HashSet<Group>());
	}
	
	public User() {
		this("", "", "" + "@mail.de", "", "", new HashSet<Permission>(), new HashSet<Group>());
	}
	
	public void addPermission(Permission permission) {
		permissions.add(permission);
	}
	
	public void removePermission(Permission permission) throws EntityNotFoundException {
		Permission userPermission = getMatchingUserPermission(permission);
		if (userPermission != null) {
			permissions.remove(userPermission);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	public Permission getMatchingUserPermission(Permission permission) {
		for (Permission userPermission : permissions) {
			if (userPermission.getId()==permission.getId())
				return userPermission;
		}
		return null;
	}
	
	public void addGroup(Group group) {
		groups.add(group);
	}
	
	public void removeGroup(Group group) throws EntityNotFoundException {
		Group userGroup = getMatchingGroup(group);
		if (userGroup != null) {
			groups.remove(userGroup);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	public Group getMatchingGroup(Group group) {
		for (Group userGroup : groups) {
			if (userGroup.getId()==group.getId())
				return userGroup;
		}
		return null;
	}

}
