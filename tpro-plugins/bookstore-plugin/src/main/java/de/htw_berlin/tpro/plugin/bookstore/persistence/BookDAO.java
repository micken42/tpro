package de.htw_berlin.tpro.plugin.bookstore.persistence;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;

public class BookDAO extends GenericDAO<Book> {

	private static final long serialVersionUID = 1L;

	public BookDAO() {
		super(Book.class);
	}
}
