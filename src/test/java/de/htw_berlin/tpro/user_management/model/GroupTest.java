package de.htw_berlin.tpro.user_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

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
		AssertAnnotations.assertType(Group.class, Entity.class, NamedQueries.class);
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
	
	/* TODO: Tests anpassen
	@Test(expected=EntityNotFoundException.class)
	public void removeNonExistingGroupFromUserShouldFail() {
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
	public void removeNonExistingPermissionFromUserShouldFail() {
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
	}*/
}