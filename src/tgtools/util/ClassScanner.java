package tgtools.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：21:24
 */
public class ClassScanner {
    private final String CLASS_FILE_SUFFIX = ".class";                       // Java字节码文件后缀
    private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private FilenameFilter javaClassFilter;                                    // 类文件过滤器,只扫描一级类
    private String packPrefix;                                         // 包路径根路劲

    public ClassScanner() {
        javaClassFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // 排除内部内
                return !name.contains("$");
            }
        };
    }

    /**
     * 获取指定路径下 class 数量
     * @param basePath    包所在的根路径
     * @param packagePath 目标包路径
     * @return Integer 被扫描到的类的数量
     * @throws ClassNotFoundException
     * @Title: scanning
     * @Description 扫描指定包(本地)
     */
    public Integer scanning(String basePath, String packagePath) throws ClassNotFoundException {
        packPrefix = basePath;
        String packTmp = packagePath.replace('.', '/');
        File dir = new File(basePath, packTmp);
        // 不是文件夹
        if (dir.isDirectory()) {
            scan0(dir);
        }
        return classes.size();
    }

    /**
     * 获取指定路径下 class 数量
     * @param packagePath 包路径
     * @param recursive   是否扫描子包
     * @return Integer 类数量
     * @Title: scanning
     * @Description 扫描指定包, Jar或本地
     */
    public Integer scanning(String packagePath, boolean recursive) {
        Enumeration<URL> dir;
        String filePackPath = packagePath.replace('.', '/');
        try {
            // 得到指定路径中所有的资源文件
            dir = Thread.currentThread().getContextClassLoader().getResources(filePackPath);
            packPrefix = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            if (System.getProperty("file.separator").equals("\\")) {
                packPrefix = packPrefix.substring(1);
            }
            // 遍历资源文件
            while (dir.hasMoreElements()) {
                URL url = dir.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    File file = new File(url.getPath().substring(1));
                    scan0(file);
                } else if ("jar".equals(protocol)) {
                    scanJ(url, recursive);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classes.size();
    }

    /**
     * 扫描Jar包下所有class
     * @param url       jar-url路径
     * @param recursive 是否递归遍历子包
     * @throws IOException
     * @throws ClassNotFoundException
     * @Title: scanJ
     * @Description 扫描Jar包下所有class
     */
    private void scanJ(URL url, boolean recursive) throws IOException, ClassNotFoundException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        // 遍历Jar包
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            String fileName = jarEntry.getName();
            if (jarEntry.isDirectory()) {
                if (recursive) {
                }
                continue;
            }
            // .class
            if (fileName.endsWith(CLASS_FILE_SUFFIX)) {
                String className = fileName.substring(0, fileName.indexOf('.')).replace('/', '.');
                classes.put(className, Class.forName(className));
            }
        }
    }

    /**
     * 执行扫描
     * @param dir Java包文件夹
     * @throws ClassNotFoundException
     * @Title: scan0
     * @Description 执行扫描
     */
    private void scan0(File dir) throws ClassNotFoundException {
        File[] fs = dir.listFiles(javaClassFilter);
        for (int i = 0; fs != null && i < fs.length; i++) {
            File f = fs[i];
            String path = f.getAbsolutePath();
            // 跳过其他文件
            if (path.endsWith(CLASS_FILE_SUFFIX)) {
                String className = getPackageByPath(f, packPrefix); // 获取包名
                classes.put(className, Class.forName(className));
            }
        }
    }

    /**
     * 获取包中所有类
     * @return Map<String,Class<?>> K:类全名, V:Class字节码
     * @Title: getClasses
     * @Description 获取包中所有类
     */
    public Map<String, Class<?>> getClasses() {
        return classes;
    }

    /**
     * 通过指定文件获取类全名
     * @Title: getPackageByPath
     * @Description 通过指定文件获取类全名
     * @param classFile 类文件
     * @return String 类全名
     */
    public static String getPackageByPath(File classFile, String exclude){
        if(classFile == null || classFile.isDirectory()){
            return null;
        }

        String path = classFile.getAbsolutePath().replace('\\', '/');

        path = path.substring(path.indexOf(exclude) + exclude.length()).replace('/', '.');
        if(path.startsWith(".")){
            path = path.substring(1);
        }
        if(path.endsWith(".")){
            path = path.substring(0, path.length() - 1);
        }

        return path.substring(0, path.lastIndexOf('.'));
    }


        public static void main(String[] args) throws ClassNotFoundException {
        ClassScanner cs = new ClassScanner();
        int c = cs.scanning("W:/IWiFI/Code/trunk/operation/target/classes/", "com/cnp/andromeda/common/util/");
        System.out.println(c);
        System.out.println(cs.getClasses().keySet());
        ClassScanner cs2 = new ClassScanner();
        int c2 = cs2.scanning("com.cnp.swarm", false);
        System.out.println(c2);
        System.out.println(cs2.getClasses().keySet());
    }
}
