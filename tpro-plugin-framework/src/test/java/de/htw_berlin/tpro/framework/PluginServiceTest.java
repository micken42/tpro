package de.htw_berlin.tpro.framework;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

@RunWith(Arquillian.class)
public class PluginServiceTest {
	
	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addClasses(PluginService.class, PluginServiceImpl.class, 
    					PluginFinder.class, MockPluginFinderImpl.class )
    			.addPackage("de.htw_berlin.tpro.user_management.model")
    			.addPackage("de.htw_berlin.tpro.user_management.persistence")
    			.addPackage("de.htw_berlin.tpro.user_management.service");
	}
	
	@Inject @DefaultPluginService
	PluginService pluginService;
	
	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (1, \"professors\")");
		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (2, \"students\")");

		// first example plugin context and permissions
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"example-plugin-1\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (1, \"provider\", 1)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (2, \"consumer\", 1)");
		// second example plugin context and permissions
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (2, \"example-plugin-2\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (3, \"provider\", 2)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (4, \"consumer\", 2)");
		// third example plugin context and permissions
		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (3, \"example-plugin-3\")");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (5, \"provider\", 3)");
		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (6, \"consumer\", 3)");

		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (1, \"Prof Dr Ferdinand\", \"Lange\", \"professor\", \"lange@tpro.de\", \"password\")");
		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
				+ "VALUES (2, \"Max\", \"Mustermännchen\", \"student\", \"mustermaennchen@tpro.de\", \"password\");");
		
		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (2, 2)");
		
		// professor is provider in example plugin 1 because his group professors has all permissions in plugin context
		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (1, 1)");
		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (1, 2)");

		// student is consumer in example plugin 1 because he has the permission
		PersistenceHelper.execute("INSERT INTO User_Permission (user_id, permission_id) VALUES (2, 2)");
	}
	
	@After
	public void clearTestData() {		
		PersistenceHelper.execute("DELETE FROM `Group`");
		PersistenceHelper.execute("DELETE FROM Context");
		PersistenceHelper.execute("DELETE FROM Permission");
		PersistenceHelper.execute("DELETE FROM User");		
		PersistenceHelper.execute("DELETE FROM User_Permission");
		PersistenceHelper.execute("DELETE FROM Group_Permission");
		PersistenceHelper.execute("DELETE FROM Group_User");
	}

	@Test
	public void defaultUserServiceShouldBeInjected() {
		Assert.assertNotNull(pluginService.getAllPlugins());
	}

//	TODO: WHY THE HELL IS EACH METHOD CALL OF pluginFinder CLEARING SOME DB TABLES????
	
	@Test
	public void getAllPluginsShouldReturnThreePlugins() {
		List<Plugin> plugins = pluginService.getAllPlugins();
		boolean twoPluginsReturned = (plugins.size() == 3) ? true : false;
		
		Assert.assertTrue(twoPluginsReturned);
	}

	@Test
	public void getPluginByNameExamplePlugin1ShouldReturnExamplePlugin1() {
		Plugin plugin = pluginService.getPluginByName("example-plugin-1");
		Assert.assertNotNull(plugin);
	}

	@Test
	public void getPluginByUnknownNameShouldReturnNoPlugin() {
		Plugin plugin = pluginService.getPluginByName("unknown");
		Assert.assertNull(plugin);
	}

	@Test
	public void userProfessorShouldBePluginProviderOfExamplePlugin1() {
		Assert.assertTrue(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
	}

	@Test
	public void getPluginsProvidableByUserStudentShouldReturnNoPlugins() {
		List<Plugin> plugins = pluginService.getPluginsProvidableByUserWithUsername("student");
		
		Assert.assertNull(plugins);
	}

	@Test
	public void getPluginsProvidableByUserProfessorShouldReturnOnePlugin() {
		List<String> pluginNames = pluginService.getNamesOfPluginsAcessableByUserWithUsername("professor");
		Assert.assertNotNull(pluginNames);
		
		boolean onePluginReturned = (pluginNames.size() == 1) ? true : false;

		Assert.assertTrue(onePluginReturned);
	}

	@Test
	public void getPluginsAccessableByUserStudentShouldReturnOnePluginName() {
		List<String> pluginNames = pluginService.getNamesOfPluginsAcessableByUserWithUsername("student");
		Assert.assertNotNull(pluginNames);
		
		boolean onePluginReturned = (pluginNames.size() == 1) ? true : false;

		Assert.assertTrue(onePluginReturned);
	}

	@Test
	public void assignUserStudentToExamplePlugin1AsProvider() {
		Assert.assertFalse(pluginService.userIsPluginProvider("student", "example-plugin-3"));
		pluginService.assignUserToPluginAsPluginProvider("example-plugin-3", "student");
		Assert.assertTrue(pluginService.userIsPluginProvider("student", "example-plugin-3"));
	}

	@Test
	public void assignUsersStudentAndProfessorToExamplePlugin3AsPluginProviders() {
		Assert.assertFalse(pluginService.userIsPluginProvider("student", "example-plugin-3"));
		Assert.assertFalse(pluginService.userIsPluginProvider("professor", "example-plugin-3"));
		
		List<String> providers = new ArrayList<String>();
		providers.add("student");
		providers.add("professor");
		
		pluginService.assignUsersToPluginAsPluginProviders("example-plugin-3", providers);
		Assert.assertTrue(pluginService.userIsPluginProvider("student", "example-plugin-3"));
	}

	@Test
	public void assignGroupStudentsToPluginAsPluginProviderGroup() {
		Assert.assertFalse(pluginService.userIsPluginProvider("student", "example-plugin-3"));
		pluginService.assignGroupToPluginAsPluginProviderGroup("example-plugin-3", "students");
		Assert.assertTrue(pluginService.userIsPluginProvider("student", "example-plugin-3"));
	}
	
	@Test
	public void removePluginProviderProfessorFromExamplePlugin1() {
		Assert.assertTrue(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
		pluginService.removePluginProviderFromPlugin("example-plugin-1", "professor");
		Assert.assertFalse(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
	}

	@Test
	public void removePluginProvidersProfessorFromExamplePlugin1() {
		Assert.assertTrue(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
		
		List<String> providers = new ArrayList<String>();
		providers.add("professor");
		
		pluginService.removePluginProvidersFromPlugin("example-plugin-1", providers);
		Assert.assertFalse(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
	}
	
	@Test
	public void removePluginProviderGroupProfessorsFromExamplePlugin1() {
		Assert.assertTrue(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
		pluginService.removePluginProviderGroupFromPlugin("example-plugin-1", "professors");
		Assert.assertFalse(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
	}
	
//	@Test
//	public void removeAllPluginProvidersFromPlugin() {}
	
}