package de.htw_berlin.tpro.controller;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped 
public class Navigation implements Serializable {  
    private static final long serialVersionUID = 1L;   
   
    public String goToPluginPage(String pluginName) {      
    	return "/plugin/" + pluginName + "/index?faces-redirect=true";    
    } 
   
    public String goToProviderManagementPage(String pluginName) {
		return "/admin/plugin-provider-managment?faces-redirect=true&pluginName=" + pluginName;
    }
   
	public String goToManagePluginProvidersPage(String pluginName) {
		return "/admin/plugin-provider-management?faces-redirect=true&pluginName=" + pluginName;
	}
	
	public String goToSharePluginPage(String pluginName) {
		return "/share-plugins?faces-redirect=true&pluginName=" + pluginName;
	}
	
	public String goToUserHomePage() {
		return "/dashboard?faces-redirect=true";
	}
	
	public String goToAdminHomePage() {
		return "/admin/plugin-management?faces-redirect=true";
	}
	
	public String goToUserManagementPage() {
		return "/admin/user-management?faces-redirect=true";
	}
} 