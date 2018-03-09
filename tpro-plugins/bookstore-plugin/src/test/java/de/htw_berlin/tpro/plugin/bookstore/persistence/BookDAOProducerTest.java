package de.htw_berlin.tpro.plugin.bookstore.persistence;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;
import de.htw_berlin.tpro.test_utils.DeploymentHelper;

@RunWith(Arquillian.class)
public class BookDAOProducerTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
			.addClasses(GenericDAO.class, BookDAO.class, BookDAOProducer.class);
	}
	
    @Inject @DefaultBookDAO
    GenericDAO<Book> bookDAO;

    @Test
	public void bookDAOProducerShouldProduceBookDAOInstance() {
		Assert.assertNotEquals(null, bookDAO);
    }

    @Test
	public void producedBookDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
    	bookDAO.beginTransaction();
    	EntityManager em = bookDAO.getEntityManager();
		Assert.assertNotEquals(null, em);

    	bookDAO.closeTransaction();
    }
}