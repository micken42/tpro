package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;

public interface BookFacade extends Serializable {
	
	public List<Book> getAllBooks();
		
}
