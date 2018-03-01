package de.htw_berlin.tpro.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped 
public class NavigationController implements Serializable {  
   private static final long serialVersionUID = 1L;   
   
   public String goToPlugin(String plugin) {      
      return "plugin/" + plugin + "/index";    
   }  
} 