package de.htw_berlin.tpro.user_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.junit.Assert;
import org.junit.Test;

import de.htw_berlin.tpro.test_utils.AssertAnnotations;
import de.htw_berlin.tpro.test_utils.ReflectionHelper;;

/**
 * @author Michael Baumert
 */
public class ContextTest {
	@Test
	public void contextTypeShouldBeAnnotated() {
		AssertAnnotations.assertType(Context.class, Entity.class, NamedQueries.class);
	}

	@Test
	public void contextTypeAnnotatedNamedQueriesShouldBeDefined() {
		// build
		NamedQuery queries[] = 
				ReflectionHelper.getClassAnnotation(Context.class, NamedQueries.class).value();
		NamedQuery findAll = null; 
		NamedQuery findByName = null;
		NamedQuery findAllNames = null;
		// initialize each NamedQuery
		for (NamedQuery query : queries) {
			switch (query.name()) {
			case "Context.findAll":
				findAll = query;
				break;
			case "Context.findByName":
				findByName = query;
				break;
			case "Context.findAllNames":
				findAllNames = query;
				break;
			default:
				continue;
			}
		}
		// check
		try {
			Assert.assertEquals(
					findAll.query(), "SELECT c FROM Context c");
			Assert.assertEquals(
					findByName.query(), "SELECT c FROM Context c WHERE c.name = :name");
			Assert.assertEquals(
					findAllNames.query(), "SELECT c.name FROM Context c");
		} catch (NullPointerException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void contextFieldsShouldBeAnnotated() {
		AssertAnnotations.assertField(
				Context.class, "id", Id.class, GeneratedValue.class, Column.class);
		AssertAnnotations.assertField(
				Context.class, "name", Column.class);
		AssertAnnotations.assertField(
				Context.class, "roles", OneToMany.class);
	}
	
	@Test
	public void addRoleToContext() {
		Context context = new Context("htwBerlin");
		Role studentRole = new Role("student");
	
		context.addRole(studentRole);
		boolean roleHasBeenAdded = false;
		for (Role role : context.getRoles()) {
			if (role.getName().equals("student"))
				roleHasBeenAdded = true;
		}
		
		Assert.assertTrue(roleHasBeenAdded);
	}
	
	@Test
	public void removeRoleFromContext() {
		Context context = new Context("htwBerlin");
		Role studentRole = new Role("student");
		context.addRole(studentRole);
		int initialNumberOfRoles = context.getRoles().size();
		
		context.removeRole(studentRole);
		int actualNumberOfRoles = context.getRoles().size();
		boolean roleHasBeenDeleted = 
				(initialNumberOfRoles > actualNumberOfRoles) ? true : false;
		
		Assert.assertTrue(roleHasBeenDeleted);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void removeNotExistingRoleFromContextShouldFail() {
		Context context = new Context("htwBerlin");
		Role studentRole = new Role("student");
		studentRole.setId(1);
		context.addRole(studentRole);
		
		Role roleToBeDeleted = new Role("unknown");
		roleToBeDeleted.setId(2);
		context.removeRole(roleToBeDeleted);
	}

}