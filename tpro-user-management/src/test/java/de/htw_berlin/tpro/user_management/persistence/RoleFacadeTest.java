package de.htw_berlin.tpro.user_management.persistence;

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
import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.persistence.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultRoleFacade;
import de.htw_berlin.tpro.user_management.persistence.RoleFacade;

@RunWith(Arquillian.class)
public class RoleFacadeTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addPackage("de.htw_berlin.tpro.user_management.persistence");
	}
	
	@Inject @DefaultRoleFacade
	RoleFacade roleFacade;
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (1, \"htw\")");
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"Uni\")");
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (1, \"Teacher\", 1)");
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (2, \"Student\", 1)");
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (3, \"Guest\", 1)");
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (4, \"Caretaker\", 1)");
		PersistenceHelper.execute("INSERT INTO Role(id, name, context_id) VALUES (5, \"Unnecessary\", 1)");
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (1, \"Lisa\", \"Musterfrau\", \"lisa\", \"musterlisa@tpro.de\", \"lisa\")");
		PersistenceHelper.execute("INSERT INTO User_Role (user_id, role_id) VALUES (1, 4)");
		PersistenceHelper.execute("INSERT INTO Group_Role (group_id, role_id) VALUES (1, 4)");
	}
	
	@After
	public void clearTestData() {	
		PersistenceHelper.execute("DELETE FROM Group_Role");
		PersistenceHelper.execute("DELETE FROM User_Role");
		PersistenceHelper.execute("DELETE FROM Group_User");
		PersistenceHelper.execute("DELETE FROM Role");
		PersistenceHelper.execute("DELETE FROM Context");
		PersistenceHelper.execute("DELETE FROM User");		
		PersistenceHelper.execute("DELETE FROM `Group`");
	}
	
	@Test
	public void defaultRoleFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, roleFacade);
	}
	
	@Test 
	public void getAllRolesShouldReturnRoles() {
		ArrayList<Role> roles = (ArrayList<Role>) roleFacade.getAllRoles();
		
		boolean moreThanZeroRoles = roles.size() > 0;
		
		Assert.assertTrue(moreThanZeroRoles);	
	}
	
	@Test 
	public void getRolesByContextNameTproShouldReturnTProRoles() {
		ArrayList<Role> roles = 
				(ArrayList<Role>) roleFacade.getRolesByContextName("tpro");
		
		boolean onlyTproRoles = true;
		for(Role role : roles) {
			onlyTproRoles = role.getContext().getName().equals("tpro");
			if (!onlyTproRoles) break;
		}
		
		Assert.assertTrue(onlyTproRoles);	
	}
	
	@Test 
	public void getRoleByUnknownContextNameShouldReturnNoRoles() {
		ArrayList<Role> roles = 
				(ArrayList<Role>) roleFacade.getRolesByContextName("unknown");
		
		boolean noRoles = (roles.size() == 0);
		
		Assert.assertTrue(noRoles);	
	}
	
	@Test 
	public void renameAllPersistedRoles() {
		ArrayList<Role> roles = (ArrayList<Role>) roleFacade.getAllRoles();
		for(Role role : roles) {
			role.setName(role.getName() + role.getId());
			roleFacade.updateRole(role);
		}
		roles = (ArrayList<Role>) roleFacade.getAllRoles();
		boolean rolesAreRenamed = false;
		for(Role role : roles) {
			if (role.getName().equals("Teacher1")) 
				rolesAreRenamed = true;
		}
		
		Assert.assertTrue(rolesAreRenamed);
	}
	
	@Test 
	public void renamePersistedRoleTeacherFromContextUniToRoleProfessor() {
		Role teacher = roleFacade.getRoleByRoleAndContextName("Teacher", "Uni");
		
		if (teacher != null) {
			teacher.setName("Professor");
			roleFacade.updateRole(teacher);
		}
		teacher = roleFacade.getRoleByRoleAndContextName("Teacher", "Uni");
		Role professor = roleFacade.getRoleByRoleAndContextName("Professor", "Uni");
		
		Assert.assertEquals(null, teacher);
		Assert.assertNotEquals(null, professor);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistRoleWithSameNameInSameContextTwiceShouldFail() {
		Role student = roleFacade.getRoleByRoleAndContextName("Student", "Uni");
		Role studentDouble = new Role("Student");
		studentDouble.setContext(student.getContext());
		roleFacade.saveRole(studentDouble);
	}
	
	@Test(expected=PersistenceException.class)
	public void renameRoleToAnAlreadyExistingRoleNameInSameContextShouldFail() {
		Role guest = roleFacade.getRoleByRoleAndContextName("Guest", "Uni");
		guest.setName("Student");
		roleFacade.updateRole(guest);
	}
	
	@Test
	public void persistRolePresidentWithinContextUni() {
		Context context = contextFacade.getContextByName("Uni");
		Role role = new Role("President");
		role.setContext(context);
		
		roleFacade.saveRole(role);
		boolean roleWasSaved = 
				(roleFacade.getRoleByRoleAndContextName("President", "Uni") != null);
		
		Assert.assertTrue(roleWasSaved);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistRolePresidentWithNoContextShouldFail() {
		Role role = new Role("President");
		
		roleFacade.saveRole(role);
	}
	
	@Test(expected=PersistenceException.class)
	public void persistRolePresidentWithNotExistingContextShouldFail() {
		Context context = new Context("unknown");
		Role role = new Role("Admin");
		role.setContext(context);
		
		roleFacade.saveRole(role);
	}
	
	@Test
	public void deleteAnExistingRole() {
		roleFacade.deleteRoleByRoleAndContextName("Unnecessary", "Uni");
		boolean noRoleFound = 
				(roleFacade.getRoleByRoleAndContextName("Unnecessary", "Uni") == null);
		
		Assert.assertTrue(noRoleFound);
	}
	
	@Test
	public void deleteAnExistingRoleWhichIsAssignedToAnUser() {
		roleFacade.deleteRoleByRoleAndContextName("Caretaker", "Uni");
		boolean noRoleFound = 
				(roleFacade.getRoleByRoleAndContextName("Caretaker", "Uni") == null);
		
		Assert.assertTrue(noRoleFound);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void deleteAnUnknownNotPersistedRoleShouldFail() {
		roleFacade.deleteRoleByRoleAndContextName("unknown", "Uni");
	}
	
	@Test
	public void deleteAllRoles() {
		roleFacade.deleteAllRoles();
		boolean noGroupsFound = (roleFacade.getAllRoles().size() == 0);
		
		Assert.assertTrue(noGroupsFound);
	}
	
}