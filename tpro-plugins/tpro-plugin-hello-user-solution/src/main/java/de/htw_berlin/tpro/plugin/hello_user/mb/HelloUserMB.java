package de.htw_berlin.tpro.plugin.hello_user.mb;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.framework.DefaultPluginService;
import de.htw_berlin.tpro.framework.PluginService;
import de.htw_berlin.tpro.plugin.hello_user.model.Visitor;
import de.htw_berlin.tpro.plugin.hello_user.persistence.DefaultVisitorFacade;
import de.htw_berlin.tpro.plugin.hello_user.persistence.VisitorFacade;
import de.htw_berlin.tpro.user_management.annotation.LoggedIn;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;

@Named
@RequestScoped
public class HelloUserMB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 *  Liste für die Besucher mit Getter-Methode, damit über EL auf die Besucher zugefriffen 
	 *  werden kann.
	 */
	private @Getter List<Visitor> visitors;

	/**
	 * Injizierung des aktuell eingeloggten Benutzers (s. Producer Method 
	 * in der UserMB des tpro-user-management Moduls) erfolgt so:
	 */
	@Inject @LoggedIn
    private User currentUser;
	
	/**
	 * Injizieren des Service, den die Benutzerverwaltung von TPro bereitstellt. Mit ihr
	 * lässt sich z.B. prüfen, ob Benutzer oder -gruppen eine bestimmte Rolle besitzen und
	 * somit berechtigt sind als diese Rolle im Kontext der Rolle
	 */
	@Inject @DefaultUserService
	private UserService userService;
	
	/**
	 * Injizieren des Service, den die Pluginverwaltung von TPro bereitstellt. Sie bietet z.B.
	 * Funktionalitäten zur Prüfung, ob ein bestimmter Nutzer in einem bestimmten Plugin als
	 * Dienstanbieter berechtigt ist oder ob dieser überhaupt Zugriff auf den Dienst hat. 
	 */
	@Inject @DefaultPluginService
	private PluginService pluginService;
	
	@Inject @DefaultVisitorFacade
	private VisitorFacade visitorFacade;
	
	/**
	 * Wenn ein Benutzer eingeloggt ist wird ein Begrueßungstext zurueckgegeben,
	 * der den Benutzer direkt anspricht. Andernfalls wird "Hallo, Welt!" returnt
	 */
	public String getHelloUserMessage() {
		if (currentUser == null)
			return "Hallo, Welt!";
		
		// Pruefen, ob es sich um einen Benutzer handelt, der den Dienst anbietet, also ein 
		// sog. Dienstanbieter ist. Es besitzt nämlich alle Rollen im Plugin-Kontext.
		if (pluginService.userIsPluginProvider(currentUser.getUsername(), "hello-user"))
			return "Hallo, Dienstanbieter(in) " + currentUser.getPrename() + " " + currentUser.getSurname() + "!";
		
		// Pruefen, ob es sich um einen Benutzer handelt, der als "student" im Plugin-Kontext 
		// berechtigt ist
		if (userService.userIsAuthorized(currentUser.getUsername(), "student", "hello-user"))
			return "Hallo, Student(in) " + currentUser.getPrename() + " " + currentUser.getSurname() + "!";
		
		// Pruefen, ob es sich um einen Benutzer handelt, der als "dozent" im Plugin-Kontext 
		// berechtigt ist
		if (userService.userIsAuthorized(currentUser.getUsername(), "dozent", "hello-user"))
			return "Hallo, Dozent(in) " + currentUser.getPrename() + " " + currentUser.getSurname() + "!";
		
		return "Hallo, Welt!";
	}
	
	/**
	 * Initialisiert die visitors Liste und speichert den aktuellen Benutzer als Besucher, falls dieser
	 * das Plugin zuvor noch nie besucht hat und nicht in der Datenbank ist.
	 */
	@PostConstruct
	public void init() {
		if (currentUser == null)
			return;
		
		String fullname = currentUser.getPrename() + " " + currentUser.getSurname();
		String role = null; 
		
		// Rolle des Besuchers, der gespeichert werden soll herausfinden
		if (pluginService.userIsPluginProvider(currentUser.getUsername(), "hello-user"))
			role = "Dienstanbieter(in)";
		if (role == null && userService.userIsAuthorized(currentUser.getUsername(), "dozent", "hello-user"))
			role = "Dozent(in)";
		if (role == null && userService.userIsAuthorized(currentUser.getUsername(), "student", "hello-user"))
			role = "Student(in)";
		
		// Besucher speichern, wenn keiner mit dem selben vollständigen Namen in der Datenbank existiert
		if (visitorFacade.getVisitorByFullname(fullname) == null) {
			Visitor visitor = new Visitor(fullname, role);
			visitorFacade.saveVisitor(visitor);
		}
		
		visitors = visitorFacade.getAllVisitors();
	}
}