package codesquad.webserver.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
    private ClassFinder() {}

    public static List<Class<?>> getClassesForPackage(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<Class<?>> classes = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                classes.addAll(findClasses(directory, packageName));
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                classes.addAll(findClassesInJar(jarURLConnection.getJarFile(), path));
            }
        }

        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    private static List<Class<?>> findClassesInJar(JarFile jarFile, String packagePath) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.startsWith(packagePath) && entryName.endsWith(".class") && !entry.isDirectory()) {
                String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }
}
