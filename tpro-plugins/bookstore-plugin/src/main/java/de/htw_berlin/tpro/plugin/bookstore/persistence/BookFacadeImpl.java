package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;

@Dependent
@DefaultBookFacade
public class BookFacadeImpl implements BookFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultBookDAO
	GenericDAO<Book> bookDAO;
	
	// @PostConstruct
	// private void init() {
	// 	// Properties props = new Properties();
	// 	// props.setProperty("javax.persistence.schema-generation.create-script-source", "META-INF/plugins/bookstore/create.sql");
	// 	// props.setProperty("javax.persistence.schema-generation.drop-script-source", "META-INF/plugins/bookstore/drop.sql");
	// 	// props.setProperty("javax.persistence.sql-load-script-source", "META-INF/plugins/bookstore/dev-data.sql");
	// 	// bookDAO.setEntityManagerFactory(Persistence.createEntityManagerFactory("tpro-plugin-database", props));
	// }

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