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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="\"Group\"")
@NamedQueries({
    @NamedQuery(name = "Group.findAll",
            query = "SELECT g FROM Group g"),
    @NamedQuery(name = "Group.findByName",
            query = "SELECT g FROM Group g WHERE g.name = :name"),
    @NamedQuery(name = "Group.findAllNames",
    		query = "SELECT g.name FROM Group g"),
    @NamedQuery(name = "Group.findAllByRoleAndContextName",
			query = "SELECT g FROM Group g JOIN g.roles p "
				  + "WHERE p.name = :role and p.context.name = :context"),
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
    @JoinTable(name="Group_Role", joinColumns=@JoinColumn(name="group_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
	private @Getter @Setter Set<Role> roles;
	
	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="Group_User", joinColumns=@JoinColumn(name="group_id"), inverseJoinColumns=@JoinColumn(name="user_id"))
	private @Getter @Setter Set<User> users;

	public Group(String name, Set<Role> roles, Set<User> users) {
		this.name = name;
		this.roles = roles;
		this.users = users;
	}
	
	public Group(String name) {
		this(name, new HashSet<Role>(), new HashSet<User>());
	}
	
	public Group() {
		this("",  new HashSet<Role>(), new HashSet<User>());
	}
	
	public void addRole(Role role) {
		roles.add(role);
	}
	
	public void removeRole(Role role) throws EntityNotFoundException {
		Role groupRole = getMatchingGroupRole(role);
		if (groupRole != null) {
			roles.remove(groupRole);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	public Role getMatchingGroupRole(Role role) {
		for (Role groupRole : roles) {
			if (groupRole.getId()==role.getId())
				return groupRole;
		}
		return null;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
	public void removeUser(User user) throws EntityNotFoundException {
		if (!users.contains(user)) 
			throw new EntityNotFoundException();
		users.remove(user);
	}

	public boolean hasRole(Role role) {
		for (Role p : roles) {
			if (p.getContext().getName().equals(role.getContext().getName()) 
					&& p.getName().equals(role.getName()))
				return true;
		}
		return false;
	}
	
	public boolean hasMember(User user) {
		for (User groupMember : users) {
			if (groupMember.getUsername().equals(user.getUsername()))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
