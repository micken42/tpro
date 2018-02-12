package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
            query = "SELECT u.username FROM User u") })
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;

	@NotNull
	private @Getter @Setter String prename;
	@NotNull
	private @Getter @Setter String surname;
	@NotNull
	@Column(unique=true)
	private @Getter @Setter String email;
	@NotNull
	private @Getter @Setter String password;
	@NotNull
	@Column(unique=true)
	private @Getter @Setter String username;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name="User_Permission", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="permission_id"))
	private @Getter @Setter Set<Permission> permissions;
	
	public User(String username, String password, Set<Permission> permissions) {
		this.username = username;
		this.password = password;
		this.permissions = permissions;
	}
	
	public User(String username, String password) {
		this(username, password, new HashSet<Permission>());
	}
	
	public User() {
		this("", "", new HashSet<Permission>());
	}
	
	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

}
