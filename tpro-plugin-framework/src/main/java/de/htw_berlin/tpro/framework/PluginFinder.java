package de.htw_berlin.tpro.framework;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginFinder {

	public List<URL> getRootUrls() {
		List<URL> result = new ArrayList<>();

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		System.out.println(cl.toString());
		while (cl != null) {
			if (cl instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) cl).getURLs();
				result.addAll(Arrays.asList(urls));
			}
			cl = cl.getParent();
		}
		return result;
	}

}
