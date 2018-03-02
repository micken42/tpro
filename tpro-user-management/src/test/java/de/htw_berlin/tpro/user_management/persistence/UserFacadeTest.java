package de.htw_berlin.tpro.user_management.persistence;

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
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.DefaultPermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.PermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.UserFacade;

@RunWith(Arquillian.class)
public class UserFacadeTest  {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addPackage("de.htw_berlin.tpro.user_management.persistence");
	}
	
	@Inject @DefaultUserFacade
	UserFacade userFacade;
	
	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;

	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (1, \"admins\")");
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (2, \"presidents\")");
		
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"tpro\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (1, \"admin\", 1)");
		
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (1, \"Max\", \"Mustermann\", \"admin\", \"mustermax@tpro.de\", \"password\");");
		
		PersistenceHelper.execute("INSERT INTO User_Permission (user_id, permission_id) VALUES (1, 1)");

		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (2, \"Abraham\", \"Lincoln\", \"abraham\", \"lincoln@tpro.de\", \"password\")");

		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (2, 2)");
	}
	
	@After
	public void clearTestData() {		
		PersistenceHelper.execute("DELETE FROM `Group`");
		PersistenceHelper.execute("DELETE FROM Context");
		PersistenceHelper.execute("DELETE FROM Permission");
		PersistenceHelper.execute("DELETE FROM User");		
		PersistenceHelper.execute("DELETE FROM User_Permission");
		PersistenceHelper.execute("DELETE FROM Group_User");
	}
	
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
	public void getUsersFromGroupAdminsShouldReturnUserAdmin() {
		ArrayList<User> users =  (ArrayList<User>) userFacade.getUsersByGroupName("admins");
		
		boolean wasGettingAdminFromAdminsGroup = false;
		for (User user : users) {
			if (user.getUsername().equals("admin"))
				wasGettingAdminFromAdminsGroup = true;
		}
		
		Assert.assertTrue(wasGettingAdminFromAdminsGroup);	
	}
	
	@Test 
	public void getGroupsContainingPermissionAdminFromContextTproShouldReturnUserAdmin() {
		ArrayList<User> users =  (ArrayList<User>) userFacade.getUsersByPermissionAndContextName("admin", "tpro");
		
		boolean wasGettingAdminWithAdminPermission = false;
		for (User user : users) {
			if (user.getUsername().equals("admin"))
				wasGettingAdminWithAdminPermission = true;
		}
		
		Assert.assertTrue(wasGettingAdminWithAdminPermission);	
	}
	
	@Test 
	public void getUserByUnknownNameShouldReturnNoUser() {
		User user =  userFacade.getUserByUsername("unknown");
		
		boolean isNoUser = (user == null);
		Assert.assertTrue(isNoUser);	
	}
	
	@Test
	public void userAdminShouldHaveAdminPermissionFromTProContext() {
		User user =  userFacade.getUserByUsername("admin");
		
		boolean hasAdminPermission = false;
		for (Permission permission : user.getPermissions()) {
			if (permission.getName().equals("admin") 
					&& permission.getContext().getName().equals("tpro"))
				hasAdminPermission = true;
		}

		Assert.assertTrue(hasAdminPermission);
	}
	
	@Test
	public void saveNewUserWithAdminPermissions() {
		// TODO: Abhängigkeit von permissionFacade lieber aufloesen
		Permission studentPermission = 
				permissionFacade.getPermissionByPermissionAndContextName("admin", "tpro");
		User user = new User("Tim", "Administrator", "newAdmin", "password");
		user.addPermission(studentPermission);
		userFacade.saveUser(user);

		User persistedUser = userFacade.getUserByUsername("newAdmin");
		boolean newUserIsPersisted = (persistedUser != null);

		boolean hasAdminPermission = false;
		for (Permission permission : user.getPermissions()) {
			if (permission.getName().equals("admin") 
					&& permission.getContext().getName().equals("tpro"))
				hasAdminPermission = true;
		}
		
		Assert.assertTrue(newUserIsPersisted && hasAdminPermission);
	}
	
	@Test 
	public void renamePersistedUserAbrahamToUserLincoln() {
		User abraham = userFacade.getUserByUsername("abraham");
		if (abraham != null) {
			abraham.setUsername("lincoln");
			userFacade.updateUser(abraham);
		}
		abraham = userFacade.getUserByUsername("abraham");
		User lincoln = userFacade.getUserByUsername("lincoln");
		
		Assert.assertEquals(null, abraham);
		Assert.assertNotEquals(null, lincoln);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistUserWithAnAlreadyExistingUserameShouldFail() {
		User duplicate = new User("abraham", "abraham");
		userFacade.saveUser(duplicate);
	}
	
	@Test(expected=PersistenceException.class)
	public void renameUserToAnAlreadyExistingUsernameShouldFail() {
		User renamedUser = userFacade.getUserByUsername("admin");
		renamedUser.setUsername("abraham");
		userFacade.updateUser(renamedUser);
	}
	
	@Test 
	public void renameAllPersistedUsers() {
		ArrayList<User> users = (ArrayList<User>) userFacade.getAllUsers();
		for(User user : users) {
			user.setUsername(user.getUsername() + user.getId());
			userFacade.updateUser(user);
		}
		ArrayList <String> names = (ArrayList<String>) userFacade.getAllUsernames();
		boolean usersAreRenamed = false;
		for(String name : names) {
			if (name.equals("admin1")) 
				usersAreRenamed = true;
		}
		
		Assert.assertTrue(usersAreRenamed);
	}
	
	@Test
	public void deleteAnExistingUser() {
		userFacade.deleteUserByUsername("abraham");
		boolean noUserFound = (userFacade.getUserByUsername("abraham") == null);
		
		Assert.assertTrue(noUserFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownNotPersistedUserShouldFail() {
		userFacade.deleteUserByUsername("unknown");
	}
	
	@Test
	public void deleteAllUsers() {
		userFacade.deleteAllUsers();
		boolean noUsersFound = (userFacade.getAllUsers().size() == 0);
		
		Assert.assertTrue(noUsersFound);
	}
		
}