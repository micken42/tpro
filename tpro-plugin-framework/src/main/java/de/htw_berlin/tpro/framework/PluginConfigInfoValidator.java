package de.htw_berlin.tpro.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class PluginConfigInfoValidator {
	/**
	 * Der Name des Plugins darf Groß- und Kleinbuchstaben,
	 * Ziffern und Bindestriche in beliebiger Reihenfolge enthalten.
	 */
	private static final String NAME_VALUE_REGEX = "^[a-zA-Z0-9-]*$";
	/**
	 * Der Name des Plugins darf Groß- und Kleinbuchstaben, Leerzeichen, Kommata
	 * Ziffern und Bindestriche in beliebiger Reihenfolge enthalten.
	 */
	private static final String ROLES_VALUE_REGEX = "^[a-zA-Z0-9, -]*$";
	// TODO: More reg expressions
	
	public static boolean isValid(Map<String, String> pluginConfigInfo) {
		if (keyIsMissing(pluginConfigInfo))
			return false;
		if (!pluginConfigInfo.get("name").matches(NAME_VALUE_REGEX))
			return false;
		if (!pluginConfigInfo.get("roles").matches(ROLES_VALUE_REGEX))
			return false;
		return true;
	}
	
	private static boolean keyIsMissing(Map<String, String> pluginConfigInfo) {
		String auth = pluginConfigInfo.get("author");
    	String vers = pluginConfigInfo.get("version");
    	String titl = pluginConfigInfo.get("title");
    	String desc = pluginConfigInfo.get("description");
    	String thum = pluginConfigInfo.get("thumbnail");
    	String perm = pluginConfigInfo.get("roles");
    	if (auth == null || vers == null || titl == null || desc == null 
    			|| thum == null || perm == null)
			return true;
		return false;
	}
	
    public static List<String> getRoleNamesFromCommaSeperatedRolesValue(String rolesValue) {
    	if (!rolesValue.matches(ROLES_VALUE_REGEX))
			return null;
    	List<String> roleNames = new ArrayList<String>();
    	StringTokenizer tokenizer = new StringTokenizer(rolesValue, ",");
        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken().trim();
            roleNames.add(value);
        }
        return roleNames;
    }
		
}
