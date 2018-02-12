package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextDAO;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextDAOProducer;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextFacadeImpl;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.GenericDAO;

@RunWith(Arquillian.class)
public class ContextFacadeTest {

	@Deployment
	public static JavaArchive createDeployment() {
	    return ShrinkWrap.create(JavaArchive.class)
			.addClasses(GenericDAO.class, ContextDAO.class, ContextDAOProducer.class)
	    	.addClasses(ContextFacade.class, ContextFacadeImpl.class, DefaultContextFacade.class)
	        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
	        .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
	        .addAsResource("META-INF/test-data.sql", "META-INF/test-data.sql");
	}
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Test
	public void defaultContextFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, contextFacade);
	}
	
	@Test 
	public void getAllContextsShouldReturnContexts() {
		ArrayList<Context> contexts = (ArrayList<Context>) contextFacade.getAllContexts();
		
		boolean moreThanZeroContexts = contexts.size() > 0;
		
		Assert.assertTrue(moreThanZeroContexts);	
	}
	
	@Test 
	public void getContextByNameTproShouldReturnTProContext() {
		Context context =  contextFacade.getContextByName("tpro");
		
		boolean isTProContext = context.getName().equals("tpro");
		
		Assert.assertTrue(isTProContext);	
	}
	
	@Test 
	public void getContextByUnknownNameShouldReturnNoContext() {
		Context context =  contextFacade.getContextByName("unknown");
		
		boolean isNoContext = (context == null);
		
		Assert.assertTrue(isNoContext);	
	}
	
	@Test
	public void tproContextShouldContainTwoPermissions() {
		Context context =  contextFacade.getContextByName("tpro");
		
		boolean tproContextHasTwoPermissions = context.getPermissions().size() == 2;
		
		Assert.assertTrue(tproContextHasTwoPermissions);
	}
	
	@Test
	public void saveNewContextWithOnePermissionShouldPersistBothPermissionAndContext() {
		Context context = new Context("newContext");
		context.addPermission(new Permission("newPermission"));
		contextFacade.saveContext(context);

		Context persistedContext = contextFacade.getContextByName("newContext");
		boolean newContextIsPersisted = persistedContext != null;
		boolean newContextHasOnePermission = (newContextIsPersisted) 
				? (persistedContext.getPermissions().size() == 1) : false;
		
		Assert.assertTrue(newContextIsPersisted && newContextHasOnePermission);
	}
	
	@Test 
	public void renamePersistedContextAbrahamToContextLincoln() {
		Context context = new Context("Abraham");
		context.addPermission(new Permission("president"));
		contextFacade.saveContext(context);
		
		Context abraham = contextFacade.getContextByName("Abraham");
		if (abraham != null) {
			abraham.setName("Lincoln");
			contextFacade.updateContext(abraham);
			contextFacade.getContextByName("Lincoln");
		}
		Context lincoln = contextFacade.getContextByName("Lincoln");
		
		Assert.assertNotEquals(null, lincoln);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistContextWithSameNameTwiceShouldFail() {
		Context context = new Context("Duplicate");
		contextFacade.saveContext(context);
		Context duplicate = new Context("Duplicate");
		contextFacade.saveContext(duplicate);
	}
	
	@Test(expected=PersistenceException.class)
	public void renameContextToAnAlreadyExistingContextNameShouldFail() {
		Context context = new Context("oldName");
		contextFacade.saveContext(context);
		
		Context renamedContext = contextFacade.getContextByName("oldName");
		renamedContext.setName("tpro");
		contextFacade.saveContext(renamedContext);
	}
	
	@Test
	public void deleteAnExistingContext() {
		Context context = new Context("toBeDeleted");
		contextFacade.saveContext(context);
		
		context = contextFacade.getContextByName("toBeDeleted");
		contextFacade.deleteContext(context);
		boolean noContextFound = (contextFacade.getContextByName("toBeDeleted") == null);
		
		Assert.assertTrue(noContextFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownContextShouldFail() {
		Context context = new Context("unknown");
		context.setId(9000);
		contextFacade.deleteContext(context);
	}
	
}


//public void updateAllContexts(List<Context> contexts);
//
//public void updateContext(Context context);
//
//public void saveContext(Context context);
//
//public List<Context> getAllContexts();
//
//public Context getContextByName(String name);
//
//public List<String> getAllNames();