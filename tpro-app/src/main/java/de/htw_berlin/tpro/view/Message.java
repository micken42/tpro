package de.htw_berlin.tpro.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

@SessionScoped
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	public void addMessage(Severity level, String message) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	FacesMessage msg = new FacesMessage(level, message,  null);
        context.addMessage(null, msg);
    	context.getExternalContext().getFlash().setKeepMessages(true);
    }
}
