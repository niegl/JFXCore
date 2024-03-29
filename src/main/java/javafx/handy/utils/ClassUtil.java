package javafx.handy.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static javafx.handy.utils.StringUtil.trimLeft;

/**
 * @author jack
 * @version 1.0
 * @date 2019/6/25 5:20
 * @since JavaFX2.0 JDK1.8
 */
public class ClassUtil {
    private ClassLoader classLoader;

    public ClassUtil() {
        classLoader = getClass().getClassLoader();
    }

    /**
     * 获取所有FxController的类名
     *
     * @param base     基础目录路径
     * @param nameList 类名列表
     * @return 所有FXController的类名列表
     */
    private List<String> scanAllClassName(String base, @NotNull List<String> nameList) {
        // 处理"."路径和”aclass.bclass“路径
        String splashPath = base;
        if (!base.equals(".")) {
            splashPath = StringUtil.dotToSplash(base);
        }

        URL url = classLoader.getResource(splashPath);
        if (url == null) {
            return null;
        }

        String filePath = null;
        try {
            filePath = StringUtil.getRootPath(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return nameList;
        }
        if (filePath.endsWith("jar")) {
            nameList = readFromJarDirectory(filePath, trimLeft(base, "."));
        } else {
            List<String> names = readFromDirectory(filePath);
            for (String name : names) {
                if (isClassFile(name)) {
                    nameList.add(toFullyQualifiedName(name, trimLeft(base, ".")));
                } else if (isDirectory(name)) {
                    nameList = scanAllClassName(trimLeft(base + "." + name, "."), nameList);
                }
            }
        }

        return nameList;
    }

    public List<String> scanAllClassName(String base) {
        return scanAllClassName(base, new LinkedList<>());
    }

    private static String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(shortName));
        return sb.toString();
    }

    private static boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private static boolean isDirectory(String name) {
        return !name.contains(".");
    }

    private static List<String> readFromDirectory(String path) {
        if (path == null) {
            return null;
        }
        return readFromFileDirectory(path);
    }

    private static List<String> readFromJarDirectory(String path, String packageName) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<JarEntry> entrys = jarFile.entries();
        List<String> classNames = new ArrayList<>();
        while (entrys.hasMoreElements()) {
            JarEntry jarEntry = entrys.nextElement();
            if (!jarEntry.getName().endsWith(".class")) continue;
            int packageNameIndex = jarEntry.getName().indexOf("/");
            if ("".equals(packageName)) {
                classNames.add(jarEntry.getName());
            } else {
                if (packageNameIndex == -1) continue;
                String baseName = jarEntry.getName().substring(0, packageNameIndex);
                if (baseName.equals(packageName)) {
                    classNames.add(StringUtil.trimExtension(jarEntry.getName()).replaceAll("/", "."));
                }
            }
        }
        return classNames;
    }

    private static List<String> readFromFileDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();
        if (null == names) {
            return null;
        } else {
            return Arrays.asList(names);
        }
    }


    public static boolean hasDeclaredAnnotation(Class clazz, Class annotation) {
        if (annotation == null) {
            return false;
        }
        if (hasAnnotationInList(annotation, clazz.getDeclaredAnnotations())) return true;
        return false;
    }

    public static boolean hasAnnotation(Class clazz, Class annotation) {
        if (annotation == null) {
            return false;
        }
        if (hasAnnotationInList(annotation, clazz.getAnnotations())) return true;
        return false;
    }

    public static boolean hasAnnotationInList(Class annotation, Annotation[] annotations2) {
        if (getAnnotationInList(annotation, annotations2) == null) {
            return false;
        } else {
            return true;
        }
    }

    public static Annotation getAnnotationInList(Class annotation, Annotation[] annotations) {
        if (annotations == null || annotation == null) {
            return null;
        }
        for (Annotation annotation1 : annotations) {
            if (annotation1.annotationType().equals(annotation)) {
                return annotation1;
            }
        }
        return null;
    }

    public static void copyField(Object target, Object base) {
        Class clazz = base.getClass();
        Class targetClass = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
    }
}
