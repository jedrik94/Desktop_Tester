package sample;

/**
 * Created by Komputer on 6/24/2017.
 */
public class LinuxPathConverter {

    public static String getPath(String path, boolean slashAtTheEnd) {
        path = "/" + Character.toLowerCase(path.charAt(0)) + path.substring(1).replace(":", "").replace("\\", "/");

        return slashAtTheEnd ? path + "/" : path;
    }
}
