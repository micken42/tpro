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
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Permission;

@RunWith(Arquillian.class)
public class GroupFacadeTest  {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addPackage("de.htw_berlin.tpro.user_management.persistence.dao");
	}
	
	@Inject @DefaultGroupFacade
	GroupFacade groupFacade;
	
	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;

	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (1, \"admins\")");
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (2, \"htw\")");
		
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"tpro\")");
		
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (1, \"admin\", 1)");

		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (1, 1)");
		
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (1, \"Max\", \"Mustermann\", \"admin\", \"mustermax@tpro.de\", \"password\");");

		PersistenceHelper.execute("INSERT INTO User_Permission (user_id, permission_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 1)");
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
	public void defaultGroupFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, groupFacade);
	}
	
	@Test 
	public void getAllGroupsShouldReturnGroups() {
		ArrayList<Group> users = (ArrayList<Group>) groupFacade.getAllGroups();
		
		boolean moreThanZeroGroups = users.size() > 0;
		
		Assert.assertTrue(moreThanZeroGroups);	
	}
	
	@Test 
	public void getAllNamesShouldReturnGroupNames() {
		ArrayList<String> names = (ArrayList<String>) groupFacade.getAllNames();
		
		boolean moreThanZeroNames = names.size() > 0;
		
		Assert.assertTrue(moreThanZeroNames);	
	}
	
	@Test 
	public void getGroupByNameAdminsShouldReturnAdminsGroup() {
		Group group =  groupFacade.getGroupByName("admins");
		
		boolean isHtwGroup = group.getName().equals("admins");
		
		Assert.assertTrue(isHtwGroup);	
	}
	
	@Test 
	public void getGroupsContainingUserAdminShouldReturnAdminsGroup() {
		ArrayList<Group> groups =  (ArrayList<Group>) groupFacade.getGroupsByUsername("admin");
		
		boolean adminIsInAdminsGroup = false;
		for (Group group : groups) {
			if (group.getName().equals("admins"))
				adminIsInAdminsGroup = true;
		}
		
		Assert.assertTrue(adminIsInAdminsGroup);	
	}
	
	@Test 
	public void getGroupsContainingPermissionAdminFromContextTproShouldReturnAdminsGroup() {
		ArrayList<Group> groups =  (ArrayList<Group>) groupFacade.getGroupsByPermissionAndContextName("admin", "tpro");
		
		boolean adminsGroupHasAdminPermission = false;
		for (Group group : groups) {
			if (group.getName().equals("admins"))
				adminsGroupHasAdminPermission = true;
		}
		
		Assert.assertTrue(adminsGroupHasAdminPermission);	
	}
	
	@Test 
	public void getGroupByUnknownNameShouldReturnNoGroup() {
		Group group =  groupFacade.getGroupByName("unknown");
		
		boolean isNoGroup = (group == null);
		Assert.assertTrue(isNoGroup);	
	}
	
	@Test
	public void groupAdminsShouldHaveAdminPermissionFromTProContext() {
		Group group =  groupFacade.getGroupByName("admins");
		
		boolean hasAdminPermission = false;
		for (Permission permission : group.getPermissions()) {
			if (permission.getName().equals("admin") 
					&& permission.getContext().getName().equals("tpro"))
				hasAdminPermission = true;
		}

		Assert.assertTrue(hasAdminPermission);
	}
	
	@Test
	public void saveNewGroupWithAdminPermissions() {
		// TODO: Abhängigkeit von permissionFacade lieber aufloesen
		Permission adminPermission = 
				permissionFacade.getPermissionByPermissionAndContextName("admin", "tpro");
		Group group = new Group("Administratoren");
		group.addPermission(adminPermission);
		groupFacade.saveGroup(group);

		Group persistedGroup = groupFacade.getGroupByName("Administratoren");
		boolean newGroupIsPersisted = (persistedGroup != null);

		boolean hasAdminPermission = false;
		for (Permission permission : group.getPermissions()) {
			if (permission.getName().equals("admin") 
					&& permission.getContext().getName().equals("tpro"))
				hasAdminPermission = true;
		}
		
		Assert.assertTrue(newGroupIsPersisted && hasAdminPermission);
	}
	
	@Test 
	public void renameAllPersistedGroups() {
		ArrayList<Group> groups = (ArrayList<Group>) groupFacade.getAllGroups();
		for(Group group : groups) {
			group.setName(group.getName() + group.getId());
			groupFacade.updateGroup(group);
		}
		ArrayList <String> names = (ArrayList<String>) groupFacade.getAllNames();
		boolean groupsAreRenamed = false;
		for(String name : names) {
			if (name.equals("admins1")) 
				groupsAreRenamed = true;
		}
		
		Assert.assertTrue(groupsAreRenamed);
	}
	
	@Test 
	public void renamePersistedGroupHtwToFu() {
		Group group = groupFacade.getGroupByName("htw");
		if (group != null) {
			group.setName("fu");
			groupFacade.updateGroup(group);
		}
		group = groupFacade.getGroupByName("htw");
		Group renamedGroup = groupFacade.getGroupByName("fu");
		
		Assert.assertEquals(null, group);
		Assert.assertNotEquals(null, renamedGroup);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistGroupWithAnAlreadyExistingNameShouldFail() {
		Group duplicate = new Group("htw");
		groupFacade.saveGroup(duplicate);
	}
	
	@Test(expected=PersistenceException.class)
	public void renameGroupToAnAlreadyExistingNameShouldFail() {
		Group renamedGroup = groupFacade.getGroupByName("admins");
		renamedGroup.setName("htw");
		groupFacade.updateGroup(renamedGroup);
	}
	
	@Test
	public void deleteAnExistingGroup() {
		Group group = groupFacade.getGroupByName("htw");
		groupFacade.deleteGroup(group);
		boolean noGroupFound = (groupFacade.getGroupByName("htw") == null);
		
		Assert.assertTrue(noGroupFound);
	}
	
	@Test
	public void deleteAnExistingGroupWithPermissionsAndUsers() {
		Group group = groupFacade.getGroupByName("admins");
		groupFacade.deleteGroup(group);
		boolean noGroupFound = (groupFacade.getGroupByName("admins") == null);
		
		Assert.assertTrue(noGroupFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownNotPersistedGroupShouldFail() {
		Group group = new Group("unknown");
		group.setId(9000);
		groupFacade.deleteGroup(group);
	}
	
	@Test
	public void deleteAllGroups() {
		groupFacade.deleteAllGroups();
		boolean noGroupsFound = (groupFacade.getAllGroups().size() == 0);
		
		Assert.assertTrue(noGroupsFound);
	}
	
}