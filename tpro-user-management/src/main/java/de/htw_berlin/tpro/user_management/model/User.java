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
    @NamedQuery(name = "User.findAllByRoleAndContextName",
    		query = "SELECT u FROM User u JOIN u.roles p "
    			  + "WHERE p.name = :role and p.context.name = :context")})
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
	
	@NotNull
	private @Getter @Setter String password;
	
	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="User_Role", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
	private @Getter @Setter Set<Role> roles;
	
	@ManyToMany(fetch=FetchType.EAGER, mappedBy="users")
	private @Getter @Setter Set<Group> groups;

	public User(String prename, String surname,  String email, String username, String password, Set<Role> roles, Set<Group> groups) {
		this.prename = prename;
		this.surname = surname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.groups = groups;
	}
	
	public User(String prename, String surname, String email, String username, String password) {
		this(prename, surname, email ,username, password, new HashSet<Role>(), new HashSet<Group>());
	}
	
	public User(String prename, String surname, String username, String password) {
		this(prename, surname, username + "@mail.de",username, password, new HashSet<Role>(), new HashSet<Group>());
	}
	
	public User(String username, String password) {
		this("", "", username + "@mail.de",username, password, new HashSet<Role>(), new HashSet<Group>());
	}
	
	public User() {
		this("", "", "" + "@mail.de", "", "", new HashSet<Role>(), new HashSet<Group>());
	}
	
	public void addRole(Role role) {
		roles.add(role);
	}
	
	public void removeRole(Role role) throws EntityNotFoundException {
		Role userRole = getMatchingUserRole(role);
		if (userRole != null) {
			roles.remove(userRole);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	public Role getMatchingUserRole(Role role) {
		for (Role userRole : roles) {
			if (userRole.getId()==role.getId())
				return userRole;
		}
		return null;
	}
	
	public void addGroup(Group group) {
		groups.add(group);
	}
	
	public void removeGroup(Group group) {
		if (!groups.contains(group))
			throw new EntityNotFoundException();
		groups.remove(group);
	}
	
	public boolean hasRole(Role role) {
		for (Role userRole : roles) {
			if (userRole.getName().equals(role.getName()) 
					&& userRole.getContext().getName().equals(role.getContext().getName()))
				return true;
		}
		return false;
	}
	
	public boolean isMember(Group group) {
		for (Group usersGroup : groups) {
			if (usersGroup.getName().equals(group.getName()))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return username + " (" + email + ")";
	}

}
