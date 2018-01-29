package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
	
	@OneToMany(cascade=CascadeType.ALL)
	private @Getter @Setter Set<Permission> permissions;
	
	public Context(String name) {
		super();
		this.name = name;
	}

	public Context() {
		this("");
	}
	
}
