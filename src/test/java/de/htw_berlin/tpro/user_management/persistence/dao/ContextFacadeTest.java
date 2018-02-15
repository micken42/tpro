package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.test_utils.PersistenceHelper;
import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;

@RunWith(Arquillian.class)
public class ContextFacadeTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
			.addClasses(GenericDAO.class, ContextDAO.class, ContextDAOProducer.class)
	    	.addClasses(ContextFacade.class, ContextFacadeImpl.class, DefaultContextFacade.class);
	}
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;

	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"tpro\")");
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (2, \"plugin\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (1, \"admin\", 1)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (2, \"user\", 1)");
	}
	
	@After
	public void clearTestData() {
		PersistenceHelper.execute("DELETE FROM Context");
		PersistenceHelper.execute("DELETE FROM Permission");
	}
	
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
	public void getAllContextsNamesShouldReturnContextNames() {
		ArrayList<String> contextNames = (ArrayList<String>) contextFacade.getAllNames();
		
		boolean moreThanZeroContextNames = contextNames.size() > 0;
		
		Assert.assertTrue(moreThanZeroContextNames);	
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
	public void saveNewContextWithANewPermissionShouldPersistBothPermissionAndContext() {
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
	public void renamePersistedContextTProToTProApp() {
		Context contextOld = contextFacade.getContextByName("tpro");
		if (contextOld != null) {
			contextOld.setName("tproApp");
			contextFacade.updateContext(contextOld);
		}
		contextOld = contextFacade.getContextByName("tpro");
		Context contextNew = contextFacade.getContextByName("tproApp");
		
		Assert.assertEquals(null, contextOld);
		Assert.assertNotEquals(null, contextNew);
	}
	
	@Test 
	public void renameAllPersistedContexts() {
		ArrayList<Context> contexts = (ArrayList<Context>) contextFacade.getAllContexts();
		for(Context context : contexts) {
			context.setName(context.getName() + context.getId());
			contextFacade.updateContext(context);
		}
		ArrayList <String> names = (ArrayList<String>) contextFacade.getAllNames();
		boolean groupsAreRenamed = false;
		for(String name : names) {
			if (name.equals("tpro1")) 
				groupsAreRenamed = true;
		}
		
		Assert.assertTrue(groupsAreRenamed);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistContextWithSameNameTwiceShouldFail() {
		Context duplicate = new Context("tpro");
		contextFacade.saveContext(duplicate);
	}
	
	@Test(expected=PersistenceException.class)
	public void renameContextToAnAlreadyExistingContextNameShouldFail() {
		Context renamedContext = contextFacade.getContextByName("tpro");
		renamedContext.setName("plugin");
		contextFacade.updateContext(renamedContext);
	}
	
	@Test
	public void deleteAnExistingContext() {
		contextFacade.deleteContextByName("plugin");
		boolean noContextFound = (contextFacade.getContextByName("plugin") == null);
		
		Assert.assertTrue(noContextFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownNotPersistedContextShouldFail() {
		contextFacade.deleteContextByName("unknown");
	}
	
	@Test
	public void deleteAllContexts() {
		contextFacade.deleteAllContexts();
		boolean noContextsFound = (contextFacade.getAllContexts().size() == 0);
		
		Assert.assertTrue(noContextsFound);
	}
	
}