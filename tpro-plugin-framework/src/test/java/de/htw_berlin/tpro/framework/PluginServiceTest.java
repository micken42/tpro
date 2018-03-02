package de.htw_berlin.tpro.framework;

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
		Assert.assertNotNull(pluginService);
	}

//	TODO: WHY THE HELL IS EACH METHOD CALL OF pluginFinder CLEARING SOME DB TABLES????
	
//	@Test
//	public void getAllPluginsShouldReturnTwoPlugins() {
//		List<Plugin> plugins = pluginService.getAllPlugins();
//		System.out.println(pluginService.getAllPlugins());
//		boolean twoPluginsReturned = (plugins.size() == 2) ? true : false;
//		
//		Assert.assertTrue(twoPluginsReturned);
//	}
//
//	@Test
//	public void getPluginByNameExamplePlugin1ShouldReturnExamplePlugin1() {
//		Plugin plugin = pluginService.getPluginByName("example-plugin-1");
//		Assert.assertNotNull(plugin);
//	}
//
//	@Test
//	public void getPluginByUnknownNameShouldReturnNoPlugin() {
//		Plugin plugin = pluginService.getPluginByName("unknown");
//		Assert.assertNull(plugin);
//	}
//
//	@Test
//	public void userProfessorShouldBePluginProviderOfExamplePlugin1() {
//		Assert.assertTrue(pluginService.userIsPluginProvider("professor", "example-plugin-1"));
//	}
//
//	@Test
//	public void getPluginsProvidableByUserStudentShouldReturnNoPlugins() {
//		List<Plugin> plugins = pluginService.getPluginsProvidableByUserWithUsername("student");
//		
//		Assert.assertNull(plugins);
//	}
//
//	@Test
//	public void getPluginsProvidableByUserProfessorShouldReturnOnePlugin() {
//		List<String> pluginNames = pluginService.getNamesOfPluginsAcessableByUserWithUsername("professor");
//		Assert.assertNotNull(pluginNames);
//		
//		boolean onePluginReturned = (pluginNames.size() == 1) ? true : false;
//
//		Assert.assertTrue(onePluginReturned);
//	}
//
//	@Test
//	public void getPluginsAccessableByUserStudentShouldReturnOnePluginName() {
//		List<String> pluginNames = pluginService.getNamesOfPluginsAcessableByUserWithUsername("student");
//		Assert.assertNotNull(pluginNames);
//		
//		boolean onePluginReturned = (pluginNames.size() == 1) ? true : false;
//
//		Assert.assertTrue(onePluginReturned);
//	}
//
//	@Test
//	public void dummy() {
//		Assert.assertNotNull(pluginService);
//	}
}

//
//List<Plugin> getPluginsProvidableByUserWithUsername(String username);
//
//List<String> getNamesOfPluginsAcessableByUserWithUsername(String username); // TODO: User hat eine Liste seiner verfügbaren pluginnamen pro session
//
//boolean userIsPluginProvider(String username, String pluginName); 
//
//void assignUserToPluginAsPluginProvider(String pluginName, String username);
//
//void assignUsersToPluginAsPluginProviders(String pluginName, List<String> usernames);
//
//void assignGroupToPluginAsPluginProviderGroup(String pluginName, String groupName);
//
//void removePluginProviderFromPlugin(String pluginName, String username);
//
//void removePluginProvidersFromPlugin(String pluginName, List<String> usernames);
//
//void removePluginProviderGroupFromPlugin(String pluginName, String groupName);
//
//void removeAllPluginProvidersFromPlugin(String pluginName);