package de.htw_berlin.tpro.framework;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
public class PluginManagerMB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String DEPLOYMENT_DIRECTORY = "/Users/baumert/var/tpro/plugins";
    private static final String WORKING_DIRECTORY = DEPLOYMENT_DIRECTORY + "/work";

    private @Getter @Setter ComponentManager compManager;
    private @Getter @Setter CheckedDirectory deployDir;
	private @Getter @Setter WorkSpace workSpace;
	
	public PluginManagerMB() {      
        deployDir = new CheckedDirectory(DEPLOYMENT_DIRECTORY);
        workSpace = new WorkSpace(WORKING_DIRECTORY);
        compManager = new ComponentManager();
        DeploymentDirectoryListener listener =
                        new DeploymentDirectoryListener(workSpace, compManager);
        deployDir.addDirectoryListener(listener);

        deployDir.check();
    
	}
}