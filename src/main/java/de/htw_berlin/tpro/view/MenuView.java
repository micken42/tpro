package de.htw_berlin.tpro.view;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import de.htw_berlin.tpro.users.annotation.LoggedIn;
import de.htw_berlin.tpro.users.model.User;
 
@Named
public class MenuView {
     
    private MenuModel model;
    
    @LoggedIn private User currentUser;
 
    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
         
        //First submenu
        DefaultSubMenu firstSubmenu = new DefaultSubMenu("TPro Management");
         
        DefaultMenuItem item = new DefaultMenuItem("Plugins");
        item.setUrl("/pluginApp.xhtml");
        item.setIcon("ui-icon-home");
        firstSubmenu.addElement(item);
         
        model.addElement(firstSubmenu);
         
        //Second submenu
        DefaultSubMenu secondSubmenu = new DefaultSubMenu("Dynamic Actions");
 
        item = new DefaultMenuItem("Save");
        item.setIcon("ui-icon-disk");
        item.setCommand("#{menuView.save}");
        item.setUpdate("messages");
        secondSubmenu.addElement(item);
         
        item = new DefaultMenuItem("Delete");
        item.setIcon("ui-icon-close");
        item.setCommand("#{menuView.delete}");
        item.setAjax(false);
        secondSubmenu.addElement(item);
         
        item = new DefaultMenuItem("Redirect");
        item.setIcon("ui-icon-search");
        item.setCommand("#{menuView.redirect}");
        secondSubmenu.addElement(item);
 
        model.addElement(secondSubmenu);
    }
 
    public MenuModel getModel() {
        return model;
    }   
     
    public void save() {
        addMessage("Success", "Data saved");
    }
     
    public void update() {
        addMessage("Success", "Data updated");
    }
     
    public void delete() {
        addMessage("Success", "Data deleted");
    }
     
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    void renderPluginIntallerMenue() {
    	
    }
    
    void renderUserMenue() {
    	
    }
}