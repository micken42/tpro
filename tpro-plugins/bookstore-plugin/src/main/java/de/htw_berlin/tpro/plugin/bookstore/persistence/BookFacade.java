package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.plugin.bookstore.model.BookstoreBook;

public interface BookFacade extends Serializable {
	
	public List<BookstoreBook> getAllBooks();
		
}
