package ioc;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassPathScanner implements ClassScanner {
	private String[] packagePaths;

	public ClassPathScanner(String... packagePaths) {
		this.packagePaths = packagePaths;
	}

	public Set<Class<?>> loadClasses() {
		Set<Class<?>> detectedClasses = new HashSet<Class<?>>();
		if (packagePaths == null)
			return detectedClasses;
		for (String pckPath : packagePaths) {
			List<Class<?>> classes = loadClasses(pckPath);
			detectedClasses.addAll(classes);
		}
		return detectedClasses;
	}

	private ClassLoader getClassLoader() {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		if (loader == null)
			loader = this.getClass().getClassLoader();
		return loader;
	}

	private List<Class<?>> loadClasses(String pckPath) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader loader = getClassLoader();
		String pathStr = pckPath.replaceAll("\\.", "/");
		URL url = loader.getResource(pathStr);
		if (url == null)
			return classes;
		loadClasses(classes, pckPath, new File(url.getFile()));
		return classes;
	}

	private void loadClasses(List<Class<?>> classes, String pckPath, File curFile) {
		for (File sub : curFile.listFiles()) {
			if (sub.isDirectory()) {
				loadClasses(classes, pckPath + "." + sub.getName(), sub);
				
			} else if (sub.getName().endsWith(".class")) {
				String className = sub.getName();
				String classStr = pckPath + "." + className.substring(0, className.length() - 6);
				try {
					Class<?> clazz = Class.forName(classStr);
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
					System.out.println("class load error: " + classStr);
				}
			}
		}
	}
}
