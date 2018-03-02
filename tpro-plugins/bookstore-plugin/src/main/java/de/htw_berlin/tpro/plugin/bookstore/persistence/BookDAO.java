package de.htw_berlin.tpro.plugin.bookstore.persistence;

import de.htw_berlin.tpro.plugin.bookstore.model.BookstoreBook;

public class BookDAO extends GenericDAO<BookstoreBook> {

	private static final long serialVersionUID = 1L;

	public BookDAO() {
		super(BookstoreBook.class);
	}
}
