package de.htw_berlin.tpro.user_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.junit.Assert;
import org.junit.Test;

import de.htw_berlin.tpro.test_utils.AssertAnnotations;
import de.htw_berlin.tpro.test_utils.ReflectionHelper;;

/**
 * @author Michael Baumert
 */
public class GroupTest {
	@Test
	public void groupTypeShouldBeAnnotated() {
		AssertAnnotations.assertType(Group.class, Entity.class, Table.class, NamedQueries.class);
	}

	@Test
	public void groupTypeAnnotatedNamedQueriesShouldBeDefined() {
		// build
		NamedQuery queries[] = 
				ReflectionHelper.getClassAnnotation(Group.class, NamedQueries.class).value();
		NamedQuery findAll = null; 
		NamedQuery findByName = null;
		NamedQuery findAllNames = null;
		NamedQuery findAllByPermissionAndContextName = null;
		NamedQuery findAllByUsername = null;
		// initialize each NamedQuery
		for (NamedQuery query : queries) {
			switch (query.name()) {
			case "Group.findAll":
				findAll = query;
				break;
			case "Group.findByName":
				findByName = query;
				break;
			case "Group.findAllNames":
				findAllNames = query;
				break;
			case "Group.findAllByPermissionAndContextName":
				findAllByPermissionAndContextName = query;
				break;
			case "Group.findAllByUsername":
				findAllByUsername = query;
				break;
			default:
				continue;
			}
		}
		// check
		try {
			Assert.assertEquals(
					findAll.query(), "SELECT g FROM Group g");
			Assert.assertEquals(
					findByName.query(), "SELECT g FROM Group g WHERE g.name = :name");
			Assert.assertEquals(
					findAllNames.query(), "SELECT g.name FROM Group g");
			Assert.assertEquals(
					findAllByPermissionAndContextName.query(), "SELECT g FROM Group g JOIN g.permissions p "
		    				                                 + "WHERE p.name = :permission and p.context.name = :context");
			Assert.assertEquals(
					findAllByUsername.query(), "SELECT g FROM Group g JOIN g.users u WHERE u.username = :username");
		} catch (NullPointerException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void groupFieldsShouldBeAnnotated() {
		AssertAnnotations.assertField(
				Group.class, "id", Id.class, GeneratedValue.class, Column.class);
		AssertAnnotations.assertField(
				Group.class, "name", Column.class);
		AssertAnnotations.assertField(
				Group.class, "permissions", ManyToMany.class, JoinTable.class);
		AssertAnnotations.assertField(
				Group.class, "users", ManyToMany.class, JoinTable.class);
	}
	
	@Test
	public void addUserToGroup() {
		Group group = new Group("fb4");
		User studentUser = new User("aiStudent", "password");
	
		group.addUser(studentUser);
		boolean userHasBeenAdded = false;
		for (User user : group.getUsers()) {
			if (user.getUsername().equals("aiStudent"))
				userHasBeenAdded = true;
		}
		
		Assert.assertTrue(userHasBeenAdded);
	}
	
	@Test
	public void removeUserFromGroup() {
		Group group = new Group("fb4");
		User studentUser = new User("aiStudent", "password");
		group.addUser(studentUser);
		int initialNumberOfGroups = group.getUsers().size();
		
		group.removeUser(studentUser);
		int actualNumberOfGroups = group.getUsers().size();
		boolean userHasBeenDeleted = 
				(initialNumberOfGroups > actualNumberOfGroups) ? true : false;
		
		Assert.assertTrue(userHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingUserFromGroupShouldFail() {
		Group group = new Group("fb4");
		User studentUser = new User("aiStudent", "password");
		studentUser.setId(1);
		group.addUser(studentUser);
		
		User userToBeDeleted = new User("unknown", "password");
		userToBeDeleted.setId(2);
		group.removeUser(userToBeDeleted);
	}
	
	@Test
	public void existingUserShouldMatchGroupsUser() {
		Group group = new Group("fb4");
		User studentUser = new User("aiStudent", "password");
		studentUser.setId(1);
	
		group.addUser(studentUser);
		boolean userMatchesGroupsUser = 
				(group.getMatchingUser(studentUser) != null) ? true : false;
		
		Assert.assertTrue(userMatchesGroupsUser);
	}
	
	
	
	@Test
	public void notExistingUserShouldNotMatchGroupsUser() {
		Group group = new Group("fb4");
		User studentUser = new User("aiStudent", "password");
		studentUser.setId(1);
	
		User notExistingUser = new User("unknown", "password");
		notExistingUser.setId(2);
		group.addUser(studentUser);
		boolean userDoesNotMatchGroupsUser = 
				(group.getMatchingUser(notExistingUser) == null) ? true : false;
		
		Assert.assertTrue(userDoesNotMatchGroupsUser);
	}
	
	@Test
	public void addPermissionToGroup() {
		Group group = new Group("fb4");
		Permission studentPermission = new Permission("student");
	
		group.addPermission(studentPermission);
		boolean permissionHasBeenAdded = false;
		for (Permission permission : group.getPermissions()) {
			if (permission.getName().equals("student"))
				permissionHasBeenAdded = true;
		}
		
		Assert.assertTrue(permissionHasBeenAdded);
	}
	
	@Test
	public void removePermissionFromGroup() {
		Group group = new Group("fb4");
		Permission studentPermission = new Permission("student");
		group.addPermission(studentPermission);
		int initialNumberOfPermissions = group.getPermissions().size();
		
		group.removePermission(studentPermission);
		int actualNumberOfPermissions = group.getPermissions().size();
		boolean permissionHasBeenDeleted = 
				(initialNumberOfPermissions > actualNumberOfPermissions) ? true : false;
		
		Assert.assertTrue(permissionHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingPermissionFromGroupShouldFail() {
		Group group = new Group("fb4");
		Permission studentPermission = new Permission("student");
		studentPermission.setId(1);
		group.addPermission(studentPermission);
		
		Permission permissionToBeDeleted = new Permission("unknown");
		permissionToBeDeleted.setId(2);
		group.removePermission(permissionToBeDeleted);
	}

	@Test
	public void permissionShouldMatchGroupsPermission() {
		Group group = new Group("fb4");
		Permission studentPermission = new Permission("student");
		studentPermission.setId(1);
	
		group.addPermission(studentPermission);
		boolean permissionMatchesUsersPermission = 
				(group.getMatchingGroupPermission(studentPermission) != null) ? true : false;
		
		Assert.assertTrue(permissionMatchesUsersPermission);
	}
	
	@Test
	public void notExistingPermissionShouldNotMatchGroupsPermission() {
		Group group = new Group("fb4");
		Permission studentPermission = new Permission("student");
		studentPermission.setId(1);
	
		Permission notExistingPermission = new Permission("unknown");
		group.addPermission(studentPermission);
		boolean permissionDoesNotMatchUsersPermission = 
				(group.getMatchingGroupPermission(notExistingPermission) == null) ? true : false;
		
		Assert.assertTrue(permissionDoesNotMatchUsersPermission);
	}
}