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
import de.htw_berlin.tpro.user_management.model.Role;
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
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (1, \"admin\", 1)");
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (2, \"user\", 1)");
		
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (1, \"Max\", \"Mustermann\", \"admin\", \"mustermax@tpro.de\", \"password\");");
		
		PersistenceHelper.execute("INSERT INTO User_Role (user_id, role_id) VALUES (1, 1)");

		PersistenceHelper.execute("INSERT INTO Group_Role (group_id, role_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_Role (group_id, role_id) VALUES (2, 2)");
		PersistenceHelper.execute("INSERT INTO Group_Role (group_id, role_id) VALUES (3, 2)");

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
		PersistenceHelper.execute("DELETE FROM Role");
		PersistenceHelper.execute("DELETE FROM User");		
		PersistenceHelper.execute("DELETE FROM User_Role");
		PersistenceHelper.execute("DELETE FROM Group_Role");
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
	public void userAdminWithAdminRoleShouldBeAuthorizedAsAdminInContextTpro() {
		Assert.assertTrue(userService.userIsAuthorized("admin", "admin", "tpro"));
	}

	@Test
	public void asMemberOfAdminsGroupUserAbrahamShouldBeAuthorizedAsAdminInContextTpro() {
		Assert.assertTrue(userService.userIsAuthorized("abraham", "admin", "tpro"));
	}

	@Test
	public void asNoMemberOfAdminsGroupAndNoAdminRoleUserPeterShouldBeAuthorizedAsAdminInContextTpro() {
		Assert.assertTrue(!userService.userIsAuthorized("peter", "admin", "tpro"));
	}

	@Test
	public void gettingAuthorizedUsersForAdminRoleInTproContextShouldReturnTwoUsers() {
		List<User> adminUsers = userService.getAuthorizedUsers("admin", "tpro");
		
		boolean twoUsersAsResult = false;
		if (adminUsers != null)
			twoUsersAsResult = (adminUsers.size() == 2) ? true : false;
		
		Assert.assertTrue(twoUsersAsResult);
	}
	
	@Test
	public void gettingAuthorizedUsersForUnknownRoleShouldReturnNoUsers() {
		List<User> adminUsers = userService.getAuthorizedUsers("unknown", "tpro");
		
		boolean noUsersAsResult = false;
		if (adminUsers != null)
			noUsersAsResult = (adminUsers.size() == 0) ? true : false;
		
		Assert.assertTrue(noUsersAsResult);
	}
	
	@Test
	public void authorizingUserPeterAsAdminShouldAddAdminRoleToUserPeter() {
		Context tproContext = new Context("tpro");
		Role adminRole = new Role("admin", tproContext);
		
		userService.authorizeUser("peter", "admin", "tpro");
		User adminPeter = userFacade.getUserByUsername("peter");

	    Assert.assertTrue(adminPeter.hasRole(adminRole));
	}
	
	@Test
	public void deauthorizingUserPeterAsAdminShouldRemoveAdminRoleFromUserPeter() {
		Context tproContext = new Context("tpro");
		Role adminRole = new Role("admin", tproContext);
		
		userService.deauthorizeUser("peter", "admin", "tpro");
		User adminPeter = userFacade.getUserByUsername("peter");

	    Assert.assertTrue(!adminPeter.hasRole(adminRole));
	}
	
	@Test
	public void deauthorizingUserAdminAsAdminShouldRemoveAdminRoleFromUserAdminAndRemoveHimFromAdminsGroup() {
		Context tproContext = new Context("tpro");
		Role adminRole = new Role("admin", tproContext);
		
		userService.deauthorizeUser("admin", "admin", "tpro");
		User admin = userFacade.getUserByUsername("admin");
		boolean isMemberOfGroupWithAdminRoles = false;
		for(Group group : admin.getGroups()) {
			if (group.hasRole(adminRole)){
				isMemberOfGroupWithAdminRoles = true;
			}
		}

	    Assert.assertTrue(!admin.hasRole(adminRole));
	    Assert.assertTrue(!isMemberOfGroupWithAdminRoles);
	}


	@Test
	public void gettingAuthorizedGroupsForRoleInTproContextShouldReturnTwoGroups() {
		List<Group> userGroups = userService.getAuthorizedGroups("user", "tpro");
		
		boolean twoGroupsAsResult = false;
		if (userGroups != null)
			twoGroupsAsResult = (userGroups.size() == 2) ? true : false;
		
		Assert.assertTrue(twoGroupsAsResult);
	}
	
	@Test
	public void gettingAuthorizedGroupsForUnknownRoleShouldReturnNoGroups() {
		List<Group> groups = userService.getAuthorizedGroups("unknown", "tpro");
		
		boolean noGroupsAsResult = false;
		if (groups != null)
			noGroupsAsResult = (groups.size() == 0) ? true : false;
		
		Assert.assertTrue(noGroupsAsResult);
	}
	
	@Test
	public void authorizingGroupUsersAsAdminShouldAddAdminRoleToGroupUsers() {
		Context tproContext = new Context("tpro");
		Role adminRole = new Role("admin", tproContext);
		
		userService.authorizeGroup("users", "admin", "tpro");
		Group userGroup = groupFacade.getGroupByName("users");

	    Assert.assertTrue(userGroup.hasRole(adminRole));
	}
	
	@Test
	public void deauthorizingGroupAdminsAsAdminShouldRemoveAdminRoleFromGroupAdmins() {
		Context tproContext = new Context("tpro");
		Role adminRole = new Role("admin", tproContext);
		
		userService.deauthorizeGroup("admins", "admin", "tpro");
		Group adminsGroup = groupFacade.getGroupByName("admins");

	    Assert.assertTrue(!adminsGroup.hasRole(adminRole));
	}
	
}