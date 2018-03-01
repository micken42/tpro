package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;

@Dependent
@DefaultBookFacade
public class BookFacadeImpl implements BookFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultBookDAO
	GenericDAO<Book> bookDAO;

	@SuppressWarnings("unchecked")
	@Override
	public List<Book> getAllBooks() {
		bookDAO.beginTransaction();
		List<Book> books;
		try {
			books = (List<Book>) bookDAO.getEntityManager().createNamedQuery("Book.findAll")
					.getResultList();
		} catch (NoResultException e) {
			books = null;
		}
		bookDAO.commitAndCloseTransaction();
		return books;
	}


}