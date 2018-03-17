package de.htw_berlin.tpro.plugin.hello_user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "Visitor.findAll",
            query = "SELECT v FROM Visitor v"),
    @NamedQuery(name = "Visitor.findByFullname",
    		query = "SELECT v FROM Visitor v WHERE v.fullname = :fullname")})
public class Visitor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;

	@Column(unique=true, nullable=false)
	private @Getter @Setter String fullname;
	@NotNull
	private @Getter @Setter String role;
	
	public Visitor(String fullname, String role) {
		this.fullname = fullname;
		this.role = role;
	}
	
	public Visitor(String fullname) {
		this(fullname, "");
	}
	
	public Visitor() {
		this("", "");
	}

}
