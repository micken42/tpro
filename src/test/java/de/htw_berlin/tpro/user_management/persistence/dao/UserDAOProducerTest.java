package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.user_management.model.User;

@RunWith(Arquillian.class)
public class UserDAOProducerTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
    		.addClass(GenericDAO.class)
    		.addClass(UserDAO.class)
    		.addClass(UserDAOProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/test-data.sql", "META-INF/test-data.sql");
    }

    @Inject
    GenericDAO<User> userDAO;

    @Test
	public void userDAOProducerShouldProducePermissionDAOInstance() {
		Assert.assertNotEquals(null, userDAO);
    }

    @Test
	public void producedUserDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
		Assert.assertNotEquals(null, userDAO.getEntityManager());
    }
}