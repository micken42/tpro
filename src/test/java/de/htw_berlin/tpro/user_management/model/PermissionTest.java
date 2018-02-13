package de.htw_berlin.tpro.user_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.junit.Assert;
import org.junit.Test;

import de.htw_berlin.tpro.test_utils.AssertAnnotations;
import de.htw_berlin.tpro.test_utils.ReflectionHelper;;

/**
 * @author Michael Baumert
 */
public class PermissionTest {
	@Test
	public void permissionTypeShouldBeAnnotated() {
		AssertAnnotations.assertType(Permission.class, Entity.class, NamedQueries.class);
	}

	@Test
	public void permissionTypeAnnotatedNamedQueriesShouldBeDefined() {
		// build
		NamedQuery queries[] = 
				ReflectionHelper.getClassAnnotation(Permission.class, NamedQueries.class).value();
		NamedQuery findAll = null; 
		NamedQuery findByPermissionAndContextName = null;
		NamedQuery findByContextName = null;
		// initialize each NamedQuery
		for (NamedQuery query : queries) {
			switch (query.name()) {
			case "Permission.findAll":
				findAll = query;
				break;
			case "Permission.findByPermissionAndContextName":
				findByPermissionAndContextName = query;
				break;
			case "Permission.findByContextName":
				findByContextName = query;
				break;
			default:
				continue;
			}
		}
		// check
		try {
			Assert.assertEquals(
					findAll.query(), "SELECT p FROM Permission p");
			Assert.assertEquals(
					findByPermissionAndContextName.query(), 
					"SELECT p FROM Permission p WHERE p.name = :name and p.context.name = :context");
			Assert.assertEquals(
					findByContextName.query(), "SELECT p FROM Permission p WHERE p.context.name = :context");
		} catch (NullPointerException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void permissionFieldsShouldBeAnnotated() {
		AssertAnnotations.assertField(
				Permission.class, "id", Id.class, GeneratedValue.class, Column.class);
		AssertAnnotations.assertField(
				Permission.class, "name", Column.class);
		AssertAnnotations.assertField(
				Permission.class, "context", ManyToOne.class);
	}

}