package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;

@RunWith(Arquillian.class)
public class UserFacadeTest  {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
			.addClasses(GenericDAO.class, UserDAO.class, UserDAOProducer.class)
	    	.addClasses(PermissionDAO.class, PermissionDAOProducer.class)
			.addClasses(ContextDAO.class, ContextDAOProducer.class)
			.addClasses(PermissionFacade.class, PermissionFacadeImpl.class, DefaultPermissionFacade.class)
			.addClasses(ContextFacade.class, ContextFacadeImpl.class, DefaultContextFacade.class)
			.addClasses(UserFacade.class, UserFacadeImpl.class, DefaultUserFacade.class);
	}
	
	@Inject @DefaultUserFacade
	UserFacade userFacade;
	
	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;

	@Test
	public void defaultUserFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, userFacade);
	}
	
	@Test 
	public void getAllUsersShouldReturnUsers() {
		ArrayList<User> users = (ArrayList<User>) userFacade.getAllUsers();
		
		boolean moreThanZeroUsers = users.size() > 0;
		
		Assert.assertTrue(moreThanZeroUsers);	
	}
	
	@Test 
	public void getAllUsernamesShouldReturnUsernames() {
		ArrayList<String> usernames = (ArrayList<String>) userFacade.getAllUsernames();
		
		boolean moreThanZeroUsernames = usernames.size() > 0;
		
		Assert.assertTrue(moreThanZeroUsernames);	
	}
	
	@Test 
	public void getUserByUserameAdminShouldReturnAdminTProUser() {
		User user =  userFacade.getUserByUsername("admin");
		
		boolean isTProUser = user.getUsername().equals("admin");
		
		Assert.assertTrue(isTProUser);	
	}
	
	@Test 
	public void getUserByUnknownNameShouldReturnNoUser() {
		User user =  userFacade.getUserByUsername("unknown");
		
		boolean isNoUser = (user == null);
		Assert.assertTrue(isNoUser);	
	}
	
	@Test
	public void tproUserShouldHaveOnePermission() {
		User user =  userFacade.getUserByUsername("user");
		
		boolean tproUserHasOnePermissions = user.getPermissions().size() == 1;
		
		Assert.assertTrue(tproUserHasOnePermissions);
	}
	
//	@Test // TODO: Fix test
//	public void saveNewUserWithStudentPermissions() {
//		Permission studentPermission = permissionFacade.getPermissionByPermissionAndContextName("Student", "Uni");
//		User user = new User("newStudent", "password");
//		user.addPermission(studentPermission);
//		userFacade.saveUser(user);
//
//		User persistedUser = userFacade.getUserByUsername("newUser");
//		boolean newUserIsPersisted = persistedUser != null;
//		
//		// boolean newUserHasOnePermission = (newUserIsPersisted) 
//		//		? (persistedUser.getPermissions().size() == 1) : false;
//		
//		Assert.assertTrue(newUserIsPersisted);// && newUserHasOnePermission);
//	}
	
	@Test 
	public void renamePersistedUserAbrahamToUserLincoln() {
		User user = new User("Abraham", "password");
		userFacade.saveUser(user);
		
		User abraham = userFacade.getUserByUsername("Abraham");
		if (abraham != null) {
			abraham.setUsername("Lincoln");
			userFacade.updateUser(abraham);
		}
		abraham = userFacade.getUserByUsername("Abraham");
		User lincoln = userFacade.getUserByUsername("Lincoln");
		
		Assert.assertEquals(null, abraham);
		Assert.assertNotEquals(null, lincoln);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistUserWithSameUserameTwiceShouldFail() {
		User user = new User("Duplicate", "password");
		userFacade.saveUser(user);
		User duplicate = new User("Duplicate", "password");
		userFacade.saveUser(duplicate);
	}
	
	@Test(expected=PersistenceException.class)
	public void renameUserToAnAlreadyExistingUsernameShouldFail() {
		User user = new User("oldName", "password");
		userFacade.saveUser(user);
		
		User renamedUser = userFacade.getUserByUsername("oldName");
		renamedUser.setUsername("admin");
		userFacade.updateUser(renamedUser);
	}
	
	@Test
	public void deleteAnExistingUser() {
		User user = new User("toBeDeleted", "password");
		userFacade.saveUser(user);
		
		user = userFacade.getUserByUsername("toBeDeleted");
		userFacade.deleteUser(user);
		boolean noUserFound = (userFacade.getUserByUsername("toBeDeleted") == null);
		
		Assert.assertTrue(noUserFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownNotPersistedUserShouldFail() {
		User user = new User("unknown", "password");
		user.setId(9000);
		userFacade.deleteUser(user);
	}
		
}