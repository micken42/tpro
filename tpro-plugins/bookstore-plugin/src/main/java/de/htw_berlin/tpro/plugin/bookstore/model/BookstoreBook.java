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
 * TODO: class name prefix should be plugin name to avoid table name collisions
 * @author baumert
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "BookstoreBook.findAll",
            query = "SELECT b FROM BookstoreBook b")})
public class BookstoreBook implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
	private @Getter @Setter Integer id;
	
	@Column(unique=true, nullable=false)
	private @Getter @Setter String title;
	
	@NotNull
	private @Getter @Setter String author;
	
	
	public BookstoreBook(String title, String author) {
		this.title = title;
		this.author = author;
	}
	
	public BookstoreBook(String title) {
		this(title, "");
	}
	
	public BookstoreBook() {
		this("", "");
	}

}
