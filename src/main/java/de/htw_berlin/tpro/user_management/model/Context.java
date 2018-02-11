package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Entity
@NamedQueries({
    @NamedQuery(name = "Context.findAll",
            query = "SELECT c FROM Context c"),
    @NamedQuery(name = "Context.findByName",
            query = "SELECT c FROM Context c WHERE c.name = :name"),
    @NamedQuery(name = "Context.findAllNames",
            query = "SELECT c.name FROM Context c") })
public class Context implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private @Getter @Setter Integer id;
	
	@NotNull
	private @Getter @Setter String name;
	
	@OneToMany(mappedBy="context", cascade=CascadeType.ALL, orphanRemoval=true)
	private @Getter @Setter Set<Permission> permissions;
	
	public Context(String name, Set<Permission> permissions) {
		this.name = name;
		this.permissions = permissions;
	}
	
	public Context(String name) {
		this(name, new HashSet<Permission>());
	}
	
	public Context() {
		this("", new HashSet<Permission>());
	}
	
	public void addPermission(Permission permission) {
		permission.setContext(this);
		permissions.add(permission);
	}
	
}
