package de.htw_berlin.tpro.user_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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
				User.class, "permissions", ManyToMany.class, JoinTable.class);
	}

}