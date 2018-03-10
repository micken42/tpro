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
		NamedQuery findAllByRoleAndContextName = null;
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
			case "User.findAllByRoleAndContextName":
				findAllByRoleAndContextName = query;
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
					findAllByRoleAndContextName.query(), "SELECT u FROM User u JOIN u.roles p "
		    				                                 + "WHERE p.name = :role and p.context.name = :context");
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
				User.class, "roles", ManyToMany.class, JoinTable.class);
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
	public void addRoleToUser() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
	
		user.addRole(studentRole);
		boolean roleHasBeenAdded = false;
		for (Role role : user.getRoles()) {
			if (role.getName().equals("student"))
				roleHasBeenAdded = true;
		}
		
		Assert.assertTrue(roleHasBeenAdded);
	}
	
	@Test
	public void removeRoleFromUser() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
		user.addRole(studentRole);
		int initialNumberOfRoles = user.getRoles().size();
		
		user.removeRole(studentRole);
		int actualNumberOfRoles = user.getRoles().size();
		boolean roleHasBeenDeleted = 
				(initialNumberOfRoles > actualNumberOfRoles) ? true : false;
		
		Assert.assertTrue(roleHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingRoleFromUserShouldFail() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
		studentRole.setId(1);
		user.addRole(studentRole);
		
		Role roleToBeDeleted = new Role("unknown");
		roleToBeDeleted.setId(2);
		user.removeRole(roleToBeDeleted);
	}

	@Test
	public void roleShouldMatchUsersRole() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
		studentRole.setId(1);
	
		user.addRole(studentRole);
		boolean roleMatchesUsersRole = 
				(user.getMatchingUserRole(studentRole) != null) ? true : false;
		
		Assert.assertTrue(roleMatchesUsersRole);
	}
	
	@Test
	public void notExistingRoleShouldNotMatchUsersRole() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
		studentRole.setId(1);
	
		Role notExistingRole = new Role("unknown");
		user.addRole(studentRole);
		boolean roleDoesNotMatchUsersRole = 
				(user.getMatchingUserRole(notExistingRole) == null) ? true : false;
		
		Assert.assertTrue(roleDoesNotMatchUsersRole);
	}
	
	@Test
	public void hasRoleShouldBeTrueIfUserHasTheGivenRole() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
		user.addRole(studentRole);
		Assert.assertTrue(user.hasRole(studentRole));
	}
	
	@Test
	public void hasRoleShouldBeFalseIfUserHasNotTheGivenRole() {
		User user = new User("aiStudent", "password");
		Role studentRole = new Role("student");
		Assert.assertTrue(!user.hasRole(studentRole));
	}
	
	@Test
	public void isMemberShouldBeTrueIfUserIsMemberOfTheGivenGroup() {
		User user = new User("aiStudent", "password");
		Group studentGroup = new Group("student");
		user.addGroup(studentGroup);
		Assert.assertTrue(user.isMember(studentGroup));
	}
	
	@Test
	public void isMemberShouldBeFalseIfUserIsNoMemberOfTheGivenGroup() {
		User user = new User("aiStudent", "password");
		Group studentGroup = new Group("studenten");
		Assert.assertTrue(!user.isMember(studentGroup));
	}
}