package de.htw_berlin.tpro.plugin.bookstore.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class BookDAOProducer {
	
	private static BookDAO bookDAO = new BookDAO();

	@Produces @DefaultBookDAO
	public BookDAO getBookDAO() {
		return bookDAO;
	}
	
}