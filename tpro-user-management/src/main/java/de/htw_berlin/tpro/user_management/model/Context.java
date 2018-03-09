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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(unique=true, nullable=false)
	private @Getter @Setter String name;
	
	@OneToMany(mappedBy="context", fetch=FetchType.EAGER, // Could also define a namedEntityGraph
			cascade=CascadeType.ALL, orphanRemoval=true)
	private @Getter @Setter Set<Role> roles;
	
	public Context(String name, Set<Role> roles) {
		this.name = name;
		this.roles = roles;
	}
	
	public Context(String name) {
		this(name, new HashSet<Role>());
	}
	
	public Context() {
		this("", new HashSet<Role>());
	}
	
	public void addRole(Role role) {
		role.setContext(this);
		roles.add(role);
	}
	
	public void removeRole(Role role) throws EntityNotFoundException {
		if (roles.contains(role)) {
			role.setContext(null);
			roles.remove(role);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
}
