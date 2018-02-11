package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "Permission.findAll",
            query = "SELECT p FROM Permission p"),
    @NamedQuery(name = "Permission.findByPermissionAndContextName",
            query = "SELECT p FROM Permission p WHERE p.name = :name and p.context.name = :context"),
    @NamedQuery(name = "Permission.findByName",
            query = "SELECT p.name FROM Permission p") })
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private @Getter @Setter Integer id;
	
	@NotNull
	private @Getter @Setter String name;
	
	@ManyToOne
	private @Getter @Setter Context context;

	public Permission(String name, Context context) {
		super();
		this.name = name;
		this.context = context;
	}

	public Permission(String name) {
		this(name, new Context());
	}
	
	public Permission() {
		this("", new Context());
	}
	
}
