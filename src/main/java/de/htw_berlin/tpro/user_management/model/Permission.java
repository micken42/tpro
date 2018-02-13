package de.htw_berlin.tpro.user_management.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "Permission.findAll",
            query = "SELECT p FROM Permission p"),
    @NamedQuery(name = "Permission.findByPermissionAndContextName",
            query = "SELECT p FROM Permission p WHERE p.name = :name and p.context.name = :context"),
    @NamedQuery(name = "Permission.findByContextName",
            query = "SELECT p FROM Permission p WHERE p.context.name = :context") })
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(unique=true, nullable=false)
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
