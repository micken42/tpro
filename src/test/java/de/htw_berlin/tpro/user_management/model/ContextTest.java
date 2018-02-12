package de.htw_berlin.tpro.user_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

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
				Context.class, "name", NotNull.class, Column.class);
		AssertAnnotations.assertField(
				Context.class, "permissions", OneToMany.class);
	}

}