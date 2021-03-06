package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;
import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.test_utils.PersistenceHelper;

@RunWith(Arquillian.class)
public class BookFacadeTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
			.addClasses(GenericDAO.class, BookDAO.class, BookDAOProducer.class, Book.class)
	    	.addClasses(BookFacade.class, BookFacadeImpl.class, DefaultBookFacade.class);
	}
	
	@Inject @DefaultBookFacade
	BookFacade bookFacade;
	
	@Test
	public void defaultBookFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, bookFacade);
	}
	
	@Test 
	public void getAllBooksShouldReturn5Books() {
		List<Book> books = bookFacade.getAllBooks();

		boolean sevenBooksReturned = (books.size() == 5);
		
		Assert.assertTrue(sevenBooksReturned);	
	}
}