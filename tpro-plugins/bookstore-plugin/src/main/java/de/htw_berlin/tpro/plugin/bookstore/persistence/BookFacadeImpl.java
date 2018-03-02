package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.plugin.bookstore.model.BookstoreBook;

@Dependent
@DefaultBookFacade
public class BookFacadeImpl implements BookFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultBookDAO
	GenericDAO<BookstoreBook> bookDAO;

	@SuppressWarnings("unchecked")
	@Override
	public List<BookstoreBook> getAllBooks() {
		bookDAO.beginTransaction();
		List<BookstoreBook> books;
		try {
			books = (List<BookstoreBook>) bookDAO.getEntityManager().createNamedQuery("BookstoreBook.findAll")
					.getResultList();
		} catch (NoResultException e) {
			books = null;
		}
		bookDAO.commitAndCloseTransaction();
		return books;
	}


}