package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.test_utils.PersistenceHelper;
import de.htw_berlin.tpro.user_management.model.Permission;

@RunWith(Arquillian.class)
public class PermissionFacadeTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
			.addClasses(GenericDAO.class, PermissionDAO.class, PermissionDAOProducer.class)
			.addClasses(ContextDAO.class, ContextDAOProducer.class)
			.addClasses(UserDAO.class, UserDAOProducer.class)
			.addClasses(PermissionFacade.class, PermissionFacadeImpl.class, DefaultPermissionFacade.class)
			.addClasses(ContextFacade.class, ContextFacadeImpl.class, DefaultContextFacade.class)
			.addClasses(UserFacade.class, UserFacadeImpl.class, DefaultUserFacade.class);
	}
	
	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;
	
	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (2, \"Uni\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (3, \"Teacher\", 2)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (4, \"Student\", 2)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (5, \"Guest\", 2)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (6, \"Caretaker\", 2)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (7, \"Unnecessary\", 2)");
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (3, \"Lisa\", \"Musterfrau\", \"lisa\", \"musterlisa@tpro.de\", \"lisa\")");
		PersistenceHelper.execute("INSERT INTO User_Permission (user_id, permission_id) VALUES (3, 6)");
	}
	
	@After
	public void clearTestData() {
		PersistenceHelper.execute("DELETE FROM User_Permission");
		PersistenceHelper.execute("DELETE FROM Group_Permission");
		PersistenceHelper.execute("DELETE FROM Group_User");
		PersistenceHelper.execute("DELETE FROM User");		
		PersistenceHelper.execute("DELETE FROM `Group`");
		PersistenceHelper.execute("DELETE FROM Permission");
		PersistenceHelper.execute("DELETE FROM Context");
	}
	
	@Test
	public void defaultPermissionFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, permissionFacade);
	}
	
	@Test 
	public void getAllPermissionsShouldReturnPermissions() {
		ArrayList<Permission> permissions = (ArrayList<Permission>) permissionFacade.getAllPermissions();
		
		boolean moreThanZeroPermissions = permissions.size() > 0;
		
		Assert.assertTrue(moreThanZeroPermissions);	
	}
	
	@Test 
	public void getPermissionsByContextNameTproShouldReturnTProPermissions() {
		ArrayList<Permission> permissions = 
				(ArrayList<Permission>) permissionFacade.getPermissionsByContextName("tpro");
		
		boolean onlyTproPermissions = true;
		for(Permission permission : permissions) {
			onlyTproPermissions = permission.getContext().getName().equals("tpro");
			if (!onlyTproPermissions) break;
		}
		
		Assert.assertTrue(onlyTproPermissions);	
	}
	
	@Test 
	public void getPermissionByUnknownContextNameShouldReturnNoPermissions() {
		ArrayList<Permission> permissions = 
				(ArrayList<Permission>) permissionFacade.getPermissionsByContextName("unknown");
		
		boolean noPermissions = (permissions.size() == 0);
		
		Assert.assertTrue(noPermissions);	
	}
	
	@Test 
	public void renamePersistedPermissionTeacherFromContextUniToPermissionProfessor() {
		Permission teacher = permissionFacade.getPermissionByPermissionAndContextName("Teacher", "Uni");
		
		if (teacher != null) {
			teacher.setName("Professor");
			permissionFacade.updatePermission(teacher);
		}
		teacher = permissionFacade.getPermissionByPermissionAndContextName("Teacher", "Uni");
		Permission professor = permissionFacade.getPermissionByPermissionAndContextName("Professor", "Uni");
		
		Assert.assertEquals(null, teacher);
		Assert.assertNotEquals(null, professor);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistPermissionWithSameNameInSameContextTwiceShouldFail() {
		Permission student = permissionFacade.getPermissionByPermissionAndContextName("Student", "Uni");
		Permission studentDouble = new Permission("Student");
		studentDouble.setContext(student.getContext());
		permissionFacade.savePermission(studentDouble);
	}
	
	@Test(expected=PersistenceException.class)
	public void renamePermissionToAnAlreadyExistingPermissionNameInSameContextShouldFail() {
		Permission guest = permissionFacade.getPermissionByPermissionAndContextName("Guest", "Uni");
		guest.setName("Student");
		permissionFacade.updatePermission(guest);
	}
	
	@Test
	public void deleteAnExistingPermission() {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName("Unnecessary", "Uni");
		permissionFacade.deletePermission(permission);
		boolean noPermissionFound = 
				(permissionFacade.getPermissionByPermissionAndContextName("Unnecessary", "Uni") == null);
		
		Assert.assertTrue(noPermissionFound);
	}
	
	@Test
	public void deleteAnExistingPermissionWhichIsAssignedToAnUser() {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName("Caretaker", "Uni");
		permissionFacade.deletePermission(permission);
		boolean noPermissionFound = 
				(permissionFacade.getPermissionByPermissionAndContextName("Caretaker", "Uni") == null);
		
		Assert.assertTrue(noPermissionFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownNotPersistedPermissionShouldFail() {
		Permission permission = new Permission("unknown");
		permission.setId(9000);
		permissionFacade.deletePermission(permission);
	}
	
}