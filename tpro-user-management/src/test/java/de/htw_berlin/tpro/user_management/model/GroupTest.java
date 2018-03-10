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
		NamedQuery findAllByRoleAndContextName = null;
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
			case "Group.findAllByRoleAndContextName":
				findAllByRoleAndContextName = query;
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
					findAllByRoleAndContextName.query(), "SELECT g FROM Group g JOIN g.roles p "
		    				                                 + "WHERE p.name = :role and p.context.name = :context");
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
				Group.class, "roles", ManyToMany.class, JoinTable.class);
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
	public void removeNotExistingUserFromGroupShould() {
		Group group = new Group("fb4");
		User studentUser = new User("aiStudent", "password");
		studentUser.setId(1);
		group.addUser(studentUser);
		
		User userToBeDeleted = new User("unknown", "password");
		userToBeDeleted.setId(2);
		group.removeUser(userToBeDeleted);
	}
	
	@Test
	public void addRoleToGroup() {
		Group group = new Group("fb4");
		Role studentRole = new Role("student");
	
		group.addRole(studentRole);
		boolean roleHasBeenAdded = false;
		for (Role role : group.getRoles()) {
			if (role.getName().equals("student"))
				roleHasBeenAdded = true;
		}
		
		Assert.assertTrue(roleHasBeenAdded);
	}
	
	@Test
	public void removeRoleFromGroup() {
		Group group = new Group("fb4");
		Role studentRole = new Role("student");
		group.addRole(studentRole);
		int initialNumberOfRoles = group.getRoles().size();
		
		group.removeRole(studentRole);
		int actualNumberOfRoles = group.getRoles().size();
		boolean roleHasBeenDeleted = 
				(initialNumberOfRoles > actualNumberOfRoles) ? true : false;
		
		Assert.assertTrue(roleHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingRoleFromGroupShouldFail() {
		Group group = new Group("fb4");
		Role studentRole = new Role("student");
		studentRole.setId(1);
		group.addRole(studentRole);
		
		Role roleToBeDeleted = new Role("unknown");
		roleToBeDeleted.setId(2);
		group.removeRole(roleToBeDeleted);
	}

	@Test
	public void roleShouldMatchGroupsRole() {
		Group group = new Group("fb4");
		Role studentRole = new Role("student");
		studentRole.setId(1);
	
		group.addRole(studentRole);
		boolean roleMatchesUsersRole = 
				(group.getMatchingGroupRole(studentRole) != null) ? true : false;
		
		Assert.assertTrue(roleMatchesUsersRole);
	}
	
	@Test
	public void notExistingRoleShouldNotMatchGroupsRole() {
		Group group = new Group("fb4");
		Role studentRole = new Role("student");
		studentRole.setId(1);
	
		Role notExistingRole = new Role("unknown");
		group.addRole(studentRole);
		boolean roleDoesNotMatchUsersRole = 
				(group.getMatchingGroupRole(notExistingRole) == null) ? true : false;
		
		Assert.assertTrue(roleDoesNotMatchUsersRole);
	}
	
	@Test
	public void hasRoleShouldBeTrueIfGroupHasTheGivenRole() {
		Group group = new Group("studenten");
		Role studentRole = new Role("student");
		group.addRole(studentRole);
		Assert.assertTrue(group.hasRole(studentRole));
	}
	
	@Test
	public void hasRoleShouldBeFalseIfUserHasNotTheGivenRole() {
		Group group = new Group("studenten");
		Role studentRole = new Role("student");
		Assert.assertTrue(!group.hasRole(studentRole));
	}
	
	@Test
	public void hasMemberShouldBeTrueIfTheGivenUserIsInTheGroup() {
		Group group = new Group("studenten");
		User student = new User("aiStudent", "password");
		group.addUser(student);
		Assert.assertTrue(group.hasMember(student));
	}
	
	@Test
	public void hasMemberShouldBeFalseIfTheGivenUserIsNotInTheGroup() {
		Group group = new Group("studenten");
		User student = new User("aiStudent", "password");
		Assert.assertTrue(!group.hasMember(student));
	}
}