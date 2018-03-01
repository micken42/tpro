package de.htw_berlin.tpro.plugin.bookstore.model;

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

/**
 * TODO: prefix of table name should be plugin name
 * @author baumert
 *
 */
//@Table(name="bookstoreBook")
@Entity
@NamedQueries({
    @NamedQuery(name = "Book.findAll",
            query = "SELECT b FROM Book b")})
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(unique=true, nullable=false)
	private @Getter @Setter String title;
	
	@NotNull
	private @Getter @Setter String author;
	
	
	public Book(String title, String author) {
		this.title = title;
		this.author = author;
	}
	
	public Book(String title) {
		this(title, "");
	}
	
	public Book() {
		this("", "");
	}

}
