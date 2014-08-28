package ioc;

import ioc.util.ClassScannerException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathScanner implements ClassScanner {
	private String[] packagePaths;
	private ClassLoader classLoader;

	/**
	 * 
	 * @param packagePaths
	 *            java包路径,例如"java.io"
	 */
	public ClassPathScanner(String... packagePaths) {
		this.packagePaths = packagePaths;
		classLoader = getClassLoader();
	}

	public Set<Class<?>> loadClasses() throws ClassScannerException {
		Set<Class<?>> detectedClasses = new HashSet<Class<?>>();
		if (packagePaths == null)
			throw new ClassScannerException("packagePaths is null");
		for (String pckPath : packagePaths) {
			List<Class<?>> classes = loadClasses(pckPath);
			detectedClasses.addAll(classes);
		}
		return detectedClasses;
	}

	private ClassLoader getClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = this.getClass().getClassLoader();
		return loader;
	}

	private List<Class<?>> loadClasses(String pckPath) throws ClassScannerException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String pathStr = pckPath.replaceAll("\\.", "/");
		URL url = classLoader.getResource(pathStr);
		if (url == null)
			throw new ClassScannerException("package could not be found : " + pckPath);
		if (isJarFile(url.toString()))
			loadJarClasses(classes, url.getFile(), pathStr);
		else
			loadFileClasses(classes, pckPath, new File(url.getFile()));
		return classes;
	}

	public void loadJarClasses(List<Class<?>> classes, String url, String pathStr)
			throws ClassScannerException {
		String jarStr = url.substring(0, url.length() - pathStr.length() - 2);
		jarStr = jarStr.substring(jarStr.indexOf(':') + 1);
		Enumeration<JarEntry> jarEntries = null;
		try {
			JarFile jarFile = new JarFile(jarStr);
			jarEntries = jarFile.entries();
		} catch (IOException e) {
			throw new ClassScannerException("jar parse exception " + jarStr + "   :for class "
					+ pathStr.replaceAll("/", "."));
		}
		while (jarEntries.hasMoreElements()) {
			JarEntry entry = jarEntries.nextElement();
			String fileStr = entry.toString();
			if (fileStr.endsWith(".class")) {
				String classStr = fileStr.substring(0, fileStr.length() - 6).replaceAll("/", ".");
				try {
					classes.add(Class.forName(classStr));
				} catch (ClassNotFoundException e) {
					throw new ClassScannerException("class not found : " + classStr);
				}
			}
		}
	}

	private boolean isJarFile(String url) {
		if (url.startsWith("jar:"))
			return true;
		return false;
	}

	private void loadFileClasses(List<Class<?>> classes, String pckPath, File pckFile)
			throws ClassScannerException {
		for (File sub : pckFile.listFiles()) {
			if (sub.isDirectory()) {
				loadFileClasses(classes, pckPath + "." + sub.getName(), sub);

			} else if (sub.getName().endsWith(".class")) {
				String className = sub.getName();
				String classStr = pckPath + "." + className.substring(0, className.length() - 6);
				try {
					Class<?> clazz = Class.forName(classStr);
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
					throw new ClassScannerException("class not found " + classStr);
				}
			}
		}
	}
}
