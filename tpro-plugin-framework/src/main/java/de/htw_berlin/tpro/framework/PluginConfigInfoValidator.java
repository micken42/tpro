package de.htw_berlin.tpro.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class PluginConfigInfoValidator {
	// TODO: Beschreibung
	private static final String NAME_VALUE_REGEX = "^[a-zA-Z-]*$";
	// TODO: Beschreibung
	private static final String PERMISSIONS_VALUE_REGEX = "^[a-zA-Z, -]*$";
	// TODO: All reg expressions
	
	public static boolean isValid(Map<String, String> pluginConfigInfo) {
		if (keyIsMissing(pluginConfigInfo))
			return false;
		if (!pluginConfigInfo.get("name").matches(NAME_VALUE_REGEX))
			return false;
		if (!pluginConfigInfo.get("permissions").matches(PERMISSIONS_VALUE_REGEX))
			return false;
		return true;
	}
	
	private static boolean keyIsMissing(Map<String, String> pluginConfigInfo) {
		String auth = pluginConfigInfo.get("author");
    	String vers = pluginConfigInfo.get("version");
    	String titl = pluginConfigInfo.get("title");
    	String desc = pluginConfigInfo.get("description");
    	String thum = pluginConfigInfo.get("thumbnail");
    	String perm = pluginConfigInfo.get("permissions");
    	if (auth == null || vers == null || titl == null || desc == null 
    			|| thum == null || perm == null)
			return true;
		return false;
	}
	
    public static List<String> getPermissionNamesFromCommaSeperatedPermissionsValue(String permissionsValue) {
    	if (!permissionsValue.matches(PERMISSIONS_VALUE_REGEX))
			return null;
    	List<String> permissionNames = new ArrayList<String>();
    	StringTokenizer tokenizer = new StringTokenizer(permissionsValue, ",");
        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken().trim();
            permissionNames.add(value);
        }
        return permissionNames;
    }
		
}
