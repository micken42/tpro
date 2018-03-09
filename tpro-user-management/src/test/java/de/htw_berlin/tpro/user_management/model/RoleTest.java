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
public class RoleTest {
	@Test
	public void roleTypeShouldBeAnnotated() {
		AssertAnnotations.assertType(Role.class, Entity.class, NamedQueries.class);
	}

	@Test
	public void roleTypeAnnotatedNamedQueriesShouldBeDefined() {
		// build
		NamedQuery queries[] = 
				ReflectionHelper.getClassAnnotation(Role.class, NamedQueries.class).value();
		NamedQuery findAll = null; 
		NamedQuery findByRoleAndContextName = null;
		NamedQuery findByContextName = null;
		// initialize each NamedQuery
		for (NamedQuery query : queries) {
			switch (query.name()) {
			case "Role.findAll":
				findAll = query;
				break;
			case "Role.findByRoleAndContextName":
				findByRoleAndContextName = query;
				break;
			case "Role.findByContextName":
				findByContextName = query;
				break;
			default:
				continue;
			}
		}
		// check
		try {
			Assert.assertEquals(
					findAll.query(), "SELECT r FROM Role r");
			Assert.assertEquals(
					findByRoleAndContextName.query(), 
					"SELECT r FROM Role r WHERE r.name = :name and r.context.name = :context");
			Assert.assertEquals(
					findByContextName.query(), "SELECT r FROM Role r WHERE r.context.name = :context");
		} catch (NullPointerException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void roleFieldsShouldBeAnnotated() {
		AssertAnnotations.assertField(
				Role.class, "id", Id.class, GeneratedValue.class, Column.class);
		AssertAnnotations.assertField(
				Role.class, "name", Column.class);
		AssertAnnotations.assertField(
				Role.class, "context", ManyToOne.class);
	}

}