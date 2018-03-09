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
    @NamedQuery(name = "Role.findAll",
            query = "SELECT r FROM Role r"),
    @NamedQuery(name = "Role.findByRoleAndContextName",
            query = "SELECT r FROM Role r WHERE r.name = :name and r.context.name = :context"),
    @NamedQuery(name = "Role.findByContextName",
            query = "SELECT r FROM Role r WHERE r.context.name = :context") })
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(nullable=false)
	private @Getter @Setter String name;
	
	@ManyToOne
	private @Getter @Setter Context context;

	public Role(String name, Context context) {
		super();
		this.name = name;
		this.context = context;
	}

	public Role(String name) {
		this(name, new Context());
	}
	
	public Role() {
		this("", new Context());
	}
	
	@Override
	public String toString() {
		return name + " (" + context.getName() + ")";
	}
}
