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
import javax.validation.constraints.NotNull;

import org.junit.Assert;
import org.junit.Test;

import de.htw_berlin.tpro.test_utils.AssertAnnotations;
import de.htw_berlin.tpro.test_utils.ReflectionHelper;;

/**
 * @author Michael Baumert
 */
public class UserTest {
	@Test
	public void userTypeShouldBeAnnotated() {
		AssertAnnotations.assertType(User.class, Entity.class, NamedQueries.class);
	}

	@Test
	public void userTypeAnnotatedNamedQueriesShouldBeDefined() {
		// build
		NamedQuery queries[] = 
				ReflectionHelper.getClassAnnotation(User.class, NamedQueries.class).value();
		NamedQuery findAll = null; 
		NamedQuery findByUsername = null;
		NamedQuery findAllUsernames = null;
		NamedQuery findAllByPermissionAndContextName = null;
		// initialize each NamedQuery
		for (NamedQuery query : queries) {
			switch (query.name()) {
			case "User.findAll":
				findAll = query;
				break;
			case "User.findByUsername":
				findByUsername = query;
				break;
			case "User.findAllUsernames":
				findAllUsernames = query;
				break;
			case "User.findAllByPermissionAndContextName":
				findAllByPermissionAndContextName = query;
				break;
			default:
				continue;
			}
		}
		// check
		try {
			Assert.assertEquals(
					findAll.query(), "SELECT u FROM User u");
			Assert.assertEquals(
					findByUsername.query(), "SELECT u FROM User u WHERE u.username = :username");
			Assert.assertEquals(
					findAllUsernames.query(), "SELECT u.username FROM User u");
			Assert.assertEquals(
					findAllByPermissionAndContextName.query(), "SELECT u FROM User u JOIN u.permissions p "
		    				                                 + "WHERE p.name = :permission and p.context.name = :context");
		} catch (NullPointerException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void userFieldsShouldBeAnnotated() {
		AssertAnnotations.assertField(
				User.class, "id", Id.class, GeneratedValue.class, Column.class);
		AssertAnnotations.assertField(
				User.class, "username", Column.class);
		AssertAnnotations.assertField(
				User.class, "password", NotNull.class);
		AssertAnnotations.assertField(
				User.class, "prename", NotNull.class);
		AssertAnnotations.assertField(
				User.class, "surname", NotNull.class);
		AssertAnnotations.assertField(
				User.class, "email", Column.class);
		AssertAnnotations.assertField(
				User.class, "permissions", ManyToMany.class, JoinTable.class);
		AssertAnnotations.assertField(
				User.class, "groups", ManyToMany.class);
	}
	
	@Test
	public void addGroupToUser() {
		User user = new User("aiStudent", "password");
		Group fb4Group = new Group("fb4");
	
		user.addGroup(fb4Group);
		boolean groupHasBeenAdded = false;
		for (Group group : user.getGroups()) {
			if (group.getName().equals("fb4"))
				groupHasBeenAdded = true;
		}
		
		Assert.assertTrue(groupHasBeenAdded);
	}
	
	@Test
	public void removeGroupFromUser() {
		User user = new User("aiStudent", "password");
		Group fb4Group = new Group("fb4");
		user.addGroup(fb4Group);
		int initialNumberOfGroups = user.getGroups().size();
		
		user.removeGroup(fb4Group);
		int actualNumberOfGroups = user.getGroups().size();
		boolean groupHasBeenDeleted = 
				(initialNumberOfGroups > actualNumberOfGroups) ? true : false;
		
		Assert.assertTrue(groupHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingGroupFromUserShouldFail() {
		User user = new User("aiStudent", "password");
		Group fb4Group = new Group("fb4");
		fb4Group.setId(1);
		user.addGroup(fb4Group);
		
		Group groupToBeDeleted = new Group("unknown");
		groupToBeDeleted.setId(2);
		user.removeGroup(groupToBeDeleted);
	}
	
	@Test
	public void existingGroupShouldMatchUsersGroup() {
		User user = new User("aiStudent", "password");
		Group fb4Group = new Group("fb4");
		fb4Group.setId(1);
	
		user.addGroup(fb4Group);
		boolean groupMatchesUsersGroups = 
				(user.getMatchingGroup(fb4Group) != null) ? true : false;
		
		Assert.assertTrue(groupMatchesUsersGroups);
	}
	
	@Test
	public void notExistingGroupShouldNotMatchUsersGroup() {
		User user = new User("aiStudent", "password");
		Group fb4Group = new Group("fb4");
		fb4Group.setId(1);
	
		Group notExistingGroup = new Group("unknown");
		notExistingGroup.setId(2);
		user.addGroup(fb4Group);
		boolean groupDoesNotMatchUsersGroups = 
				(user.getMatchingGroup(notExistingGroup) == null) ? true : false;
		
		Assert.assertTrue(groupDoesNotMatchUsersGroups);
	}
	
	@Test
	public void addPermissionToUser() {
		User user = new User("aiStudent", "password");
		Permission studentPermission = new Permission("student");
	
		user.addPermission(studentPermission);
		boolean permissionHasBeenAdded = false;
		for (Permission permission : user.getPermissions()) {
			if (permission.getName().equals("student"))
				permissionHasBeenAdded = true;
		}
		
		Assert.assertTrue(permissionHasBeenAdded);
	}
	
	@Test
	public void removePermissionFromUser() {
		User user = new User("aiStudent", "password");
		Permission studentPermission = new Permission("student");
		user.addPermission(studentPermission);
		int initialNumberOfPermissions = user.getPermissions().size();
		
		user.removePermission(studentPermission);
		int actualNumberOfPermissions = user.getPermissions().size();
		boolean permissionHasBeenDeleted = 
				(initialNumberOfPermissions > actualNumberOfPermissions) ? true : false;
		
		Assert.assertTrue(permissionHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingPermissionFromUserShouldFail() {
		User user = new User("aiStudent", "password");
		Permission studentPermission = new Permission("student");
		studentPermission.setId(1);
		user.addPermission(studentPermission);
		
		Permission permissionToBeDeleted = new Permission("unknown");
		permissionToBeDeleted.setId(2);
		user.removePermission(permissionToBeDeleted);
	}

	@Test
	public void permissionShouldMatchUsersPermission() {
		User user = new User("aiStudent", "password");
		Permission studentPermission = new Permission("student");
		studentPermission.setId(1);
	
		user.addPermission(studentPermission);
		boolean permissionMatchesUsersPermission = 
				(user.getMatchingUserPermission(studentPermission) != null) ? true : false;
		
		Assert.assertTrue(permissionMatchesUsersPermission);
	}
	
	@Test
	public void notExistingPermissionShouldNotMatchUsersPermission() {
		User user = new User("aiStudent", "password");
		Permission studentPermission = new Permission("student");
		studentPermission.setId(1);
	
		Permission notExistingPermission = new Permission("unknown");
		user.addPermission(studentPermission);
		boolean permissionDoesNotMatchUsersPermission = 
				(user.getMatchingUserPermission(notExistingPermission) == null) ? true : false;
		
		Assert.assertTrue(permissionDoesNotMatchUsersPermission);
	}
}