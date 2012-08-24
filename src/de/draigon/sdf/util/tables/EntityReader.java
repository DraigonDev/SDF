package de.draigon.sdf.util.tables;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.draigon.sdf.annotation.Table;
import de.draigon.sdf.util.tables.exceptions.TableCreationException;

public class EntityReader {
    private List<Class<?>> entities = new ArrayList<Class<?>>();
    
    public EntityReader() {
        entities = parseEntities();
    }
    
    public List<Class<?>> getEntities() {
        return entities;
    }
        
    private List<Class<?>> parseEntities() {
        List<Class<?>> entities = new ArrayList<Class<?>>();
        
        try {
            String jarname = Class.forName(Thread.currentThread().getStackTrace()[4].getClassName()).getProtectionDomain().getCodeSource().getLocation().getPath();
            
            if(jarname.toLowerCase().endsWith(".jar")){
                for (Class<?> clazz : getClassesFromJar(jarname)) {
                    if(clazz.isAnnotationPresent(Table.class)){
                        entities.add(clazz);
                    }
                }       
            }else{
                for (Class<?> clazz : getClassesFromDir("")) {
                    if(clazz.isAnnotationPresent(Table.class)){
                        entities.add(clazz);
                    }
                }   
            }
        } catch (Exception e) {
            throw new TableCreationException("error reading entities in project", e);
        } 
        
        return entities;
    }

    private List<Class<?>> getClassesFromJar(String name) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
        FileInputStream fis = new FileInputStream(name);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
         
        //
        // Read each entry from the ZipInputStream until no more entry found
        // indicated by a null return value of the getNextEntry() method.
        //
        while ((entry = zis.getNextEntry()) != null) {
            if(entry.getName().endsWith(".class")){
                Class<?> _class;
                String classname = entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6);
                try {
                    _class = Class.forName(classname);
                } catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on 
                    // Spring to inject some beans, and which fail, 
                    // if dependency is not fulfilled
                    _class = Class.forName(classname,
                            false, Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
         
        zis.close();
        fis.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
        
        return classes;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private List<Class<?>> getClassesFromDir(String packageName)
            throws ClassNotFoundException, IOException 
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClassesInDir(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private List<Class<?>> findClassesInDir(File directory, String packageName) throws ClassNotFoundException 
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClassesInDir(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class<?> _class;
                String classname = packageName.substring(1).replaceAll("/", ".") + '.' + fileName.substring(0, fileName.length() - 6);
                try {
                    _class = Class.forName(classname);
                } catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on 
                    // Spring to inject some beans, and which fail, 
                    // if dependency is not fulfilled
                    _class = Class.forName(classname,
                            false, Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
        return classes;
    }
}
