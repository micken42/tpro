package de.htw_berlin.tpro.plugin.PLUGIN_NAME;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.user_management.annotation.LoggedIn;
import de.htw_berlin.tpro.user_management.model.User;

@Named
@SessionScoped
public class HelloUserMB implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Injizierung des aktuell eingeloggten Benutzers (s. Producer Method 
	 * in der UserMB des tpro-user-management Moduls) erfolgt so:
	 */
	@Inject @LoggedIn
    private User currentUser;
	
	/**
	 * Wenn ein Benutzer eingeloggt ist wird ein Begrueßungstext zurueckgegeben,
	 * der den Benutzer direkt anspricht. Andernfalls wird "Hallo, Welt!" returnt
	 */
	public String getHelloUserMessage() {
		String msg = "";
		if (currentUser != null)
			msg = "Hallo, " + currentUser.getPrename() + " " + currentUser.getSurname() + "!";
		return msg;
	}
}