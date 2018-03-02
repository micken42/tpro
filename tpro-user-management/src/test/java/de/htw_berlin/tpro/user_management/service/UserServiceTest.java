package de.htw_berlin.tpro.user_management.service;

import java.util.List;

import javax.inject.Inject;
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
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.DefaultGroupFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.GroupFacade;
import de.htw_berlin.tpro.user_management.persistence.UserFacade;

@RunWith(Arquillian.class)
public class UserServiceTest {
	
	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addPackage("de.htw_berlin.tpro.user_management.model")
    			.addPackage("de.htw_berlin.tpro.user_management.persistence")
    			.addPackage("de.htw_berlin.tpro.user_management.service");
	}
	
	@Inject @DefaultUserService
	UserService userService;
	
	@Inject @DefaultUserFacade
	UserFacade userFacade;
	
	@Inject @DefaultGroupFacade
	GroupFacade groupFacade;
	
	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (1, \"admins\")");
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (2, \"presidents\")");
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (3, \"users\")");
		
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"tpro\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (1, \"admin\", 1)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (2, \"user\", 1)");
		
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (1, \"Max\", \"Mustermann\", \"admin\", \"mustermax@tpro.de\", \"password\");");
		
		PersistenceHelper.execute("INSERT INTO User_Permission (user_id, permission_id) VALUES (1, 1)");

		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (2, 2)");
		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (3, 2)");

		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (2, \"Abraham\", \"Lincoln\", \"abraham\", \"lincoln@tpro.de\", \"password\")");

		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 2)");
		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (2, 2)");
		
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (3, \"Peter\", \"Hausmann\", \"peter\", \"hausmann@tpro.de\", \"password\");");
	}
	
	@After
	public void clearTestData() {		
		PersistenceHelper.execute("DELETE FROM `Group`");
		PersistenceHelper.execute("DELETE FROM Context");
		PersistenceHelper.execute("DELETE FROM Permission");
		PersistenceHelper.execute("DELETE FROM User");		
		PersistenceHelper.execute("DELETE FROM User_Permission");
		PersistenceHelper.execute("DELETE FROM Group_Permission");
		PersistenceHelper.execute("DELETE FROM Group_User");
	}

	@Test
	public void defaultUserServiceShouldBeInjected() {
		Assert.assertNotNull(userService);
	}
	
	@Test
	public void signUpUserHans() {
		User hans = new User("Hans", "Bauer", "hans", "hans@tpro.de", "password");
		
		Assert.assertNotNull(userService.signUp(hans));
	}
	
	@Test(expected=PersistenceException.class)
	public void signUpUserHansTwiceShouldFail() {
		User hans = new User("Hans", "Bauer", "hans", "hans@tpro.de", "password");
		userService.signUp(hans);
		userService.signUp(hans);
	}
	
	@Test
	public void userShouldBeAbleToLoginWithCorrectCredentials() {
		User loggedInUser = userService.login("admin", "password");
		
		Assert.assertNotNull(loggedInUser);
	}
	
	@Test
	public void userShouldNotBeAbleLoginWithWrongCredentials() {
		User loggedInUser = userService.login("admin", "wrongPassword");
		
		Assert.assertNull(loggedInUser);
	}
	
	@Test
	public void userAdminWithAdminPermissionShouldBeAuthorizedAsAdminInContextTpro() {
		Assert.assertTrue(userService.userIsAuthorized("admin", "admin", "tpro"));
	}

	@Test
	public void asMemberOfAdminsGroupUserAbrahamShouldBeAuthorizedAsAdminInContextTpro() {
		Assert.assertTrue(userService.userIsAuthorized("abraham", "admin", "tpro"));
	}

	@Test
	public void asNoMemberOfAdminsGroupAndNoAdminPermissionUserPeterShouldBeAuthorizedAsAdminInContextTpro() {
		Assert.assertTrue(!userService.userIsAuthorized("peter", "admin", "tpro"));
	}

	@Test
	public void gettingAuthorizedUsersForAdminPermissionInTproContextShouldReturnTwoUsers() {
		List<User> adminUsers = userService.getAuthorizedUsers("admin", "tpro");
		
		boolean twoUsersAsResult = false;
		if (adminUsers != null)
			twoUsersAsResult = (adminUsers.size() == 2) ? true : false;
		
		Assert.assertTrue(twoUsersAsResult);
	}
	
	@Test
	public void gettingAuthorizedUsersForUnknownPermissionShouldReturnNoUsers() {
		List<User> adminUsers = userService.getAuthorizedUsers("unknown", "tpro");
		
		boolean noUsersAsResult = false;
		if (adminUsers != null)
			noUsersAsResult = (adminUsers.size() == 0) ? true : false;
		
		Assert.assertTrue(noUsersAsResult);
	}
	
	@Test
	public void authorizingUserPeterAsAdminShouldAddAdminPermissionToUserPeter() {
		Context tproContext = new Context("tpro");
		Permission adminPermission = new Permission("admin", tproContext);
		
		userService.authorizeUser("peter", "admin", "tpro");
		User adminPeter = userFacade.getUserByUsername("peter");

	    Assert.assertTrue(adminPeter.hasPermission(adminPermission));
	}
	
	@Test
	public void deauthorizingUserPeterAsAdminShouldRemoveAdminPermissionFromUserPeter() {
		Context tproContext = new Context("tpro");
		Permission adminPermission = new Permission("admin", tproContext);
		
		userService.deauthorizeUser("peter", "admin", "tpro");
		User adminPeter = userFacade.getUserByUsername("peter");

	    Assert.assertTrue(!adminPeter.hasPermission(adminPermission));
	}
	
	@Test
	public void deauthorizingUserAdminAsAdminShouldRemoveAdminPermissionFromUserAdminAndRemoveHimFromAdminsGroup() {
		Context tproContext = new Context("tpro");
		Permission adminPermission = new Permission("admin", tproContext);
		
		userService.deauthorizeUser("admin", "admin", "tpro");
		User admin = userFacade.getUserByUsername("admin");
		boolean isMemberOfGroupWithAdminPermissions = false;
		for(Group group : admin.getGroups()) {
			if (group.hasPermission(adminPermission)){
				isMemberOfGroupWithAdminPermissions = true;
			}
		}

	    Assert.assertTrue(!admin.hasPermission(adminPermission));
	    Assert.assertTrue(!isMemberOfGroupWithAdminPermissions);
	}


	@Test
	public void gettingAuthorizedGroupsForPermissionInTproContextShouldReturnTwoGroups() {
		List<Group> userGroups = userService.getAuthorizedGroups("user", "tpro");
		
		boolean twoGroupsAsResult = false;
		if (userGroups != null)
			twoGroupsAsResult = (userGroups.size() == 2) ? true : false;
		
		Assert.assertTrue(twoGroupsAsResult);
	}
	
	@Test
	public void gettingAuthorizedGroupsForUnknownPermissionShouldReturnNoGroups() {
		List<Group> groups = userService.getAuthorizedGroups("unknown", "tpro");
		
		boolean noGroupsAsResult = false;
		if (groups != null)
			noGroupsAsResult = (groups.size() == 0) ? true : false;
		
		Assert.assertTrue(noGroupsAsResult);
	}
	
	@Test
	public void authorizingGroupUsersAsAdminShouldAddAdminPermissionToGroupUsers() {
		Context tproContext = new Context("tpro");
		Permission adminPermission = new Permission("admin", tproContext);
		
		userService.authorizeGroup("users", "admin", "tpro");
		Group userGroup = groupFacade.getGroupByName("users");

	    Assert.assertTrue(userGroup.hasPermission(adminPermission));
	}
	
	@Test
	public void deauthorizingGroupAdminsAsAdminShouldRemoveAdminPermissionFromGroupAdmins() {
		Context tproContext = new Context("tpro");
		Permission adminPermission = new Permission("admin", tproContext);
		
		userService.deauthorizeGroup("admins", "admin", "tpro");
		Group adminsGroup = groupFacade.getGroupByName("admins");

	    Assert.assertTrue(!adminsGroup.hasPermission(adminPermission));
	}
	
}