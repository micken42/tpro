package de.htw_berlin.tpro.framework;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.test_utils.DeploymentHelper;

@RunWith(Arquillian.class)
public class PluginServiceTest {
	
	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addPackage("de.htw_berlin.tpro.framework")
    			.addPackage("de.htw_berlin.tpro.user_management.model")
    			.addPackage("de.htw_berlin.tpro.user_management.persistence")
    			.addPackage("de.htw_berlin.tpro.user_management.service")
    	        .addAsResource("META-INF/TEST_PLUGIN_1.MF", "META-INF/PLUGIN_MANIFEST_1.MF")
    	        .addAsResource("META-INF/TEST_PLUGIN_2.MF", "META-INF/PLUGIN_MANIFEST_2.MF");
	}
	
	@Inject @DefaultPluginService
	PluginService pluginService;
	
//	TODO: PluginService Tests !!!
	
//	@Inject @DefaultUserFacade
//	UserFacade userFacade;
//	
//	@Inject @DefaultGroupFacade
//	GroupFacade groupFacade;
//	
//	@Before
//	public void initTestData() {
//		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (1, \"admins\")");
//		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (2, \"presidents\")");
//		PersistenceHelper.execute("INSERT INTO `Group` (id, name) VALUES (3, \"users\")");
//		
//		PersistenceHelper.execute("INSERT INTO Context (id, name) VALUES (1, \"tpro\")");
//		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (1, \"admin\", 1)");
//		PersistenceHelper.execute("INSERT INTO Permission(id, name, context_id) VALUES (2, \"user\", 1)");
//		
//		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
//				+ "VALUES (1, \"Max\", \"Mustermann\", \"admin\", \"mustermax@tpro.de\", \"password\");");
//		
//		PersistenceHelper.execute("INSERT INTO User_Permission (user_id, permission_id) VALUES (1, 1)");
//
//		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (1, 1)");
//		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (2, 2)");
//		PersistenceHelper.execute("INSERT INTO Group_Permission (group_id, permission_id) VALUES (3, 2)");
//
//		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
//				+ "VALUES (2, \"Abraham\", \"Lincoln\", \"abraham\", \"lincoln@tpro.de\", \"password\")");
//
//		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 1)");
//		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (1, 2)");
//		PersistenceHelper.execute("INSERT INTO Group_User (group_id, user_id) VALUES (2, 2)");
//		
//		PersistenceHelper.execute("INSERT INTO User (id, prename, surname, username, email, password) "
//				+ "VALUES (3, \"Peter\", \"Hausmann\", \"peter\", \"hausmann@tpro.de\", \"password\");");
//	}
//	
//	@After
//	public void clearTestData() {		
//		PersistenceHelper.execute("DELETE FROM `Group`");
//		PersistenceHelper.execute("DELETE FROM Context");
//		PersistenceHelper.execute("DELETE FROM Permission");
//		PersistenceHelper.execute("DELETE FROM User");		
//		PersistenceHelper.execute("DELETE FROM User_Permission");
//		PersistenceHelper.execute("DELETE FROM Group_Permission");
//		PersistenceHelper.execute("DELETE FROM Group_User");
//	}

	@Test
	public void defaultUserServiceShouldBeInjected() {
		Assert.assertNotNull(pluginService);
	}
	
}

//List<Plugin> getAllPlugins();
//
//Plugin getPluginByName(String pluginName);
//
//List<Plugin> getPluginsProvidableByUserWithUsername(String username);
//
//List<String> getNamesOfPluginsAcessableByUserWithUsername(String username); // TODO: User hat eine Liste seiner pluginnamen pro session
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