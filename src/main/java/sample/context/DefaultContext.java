package sample.context;

import java.io.File;

public class DefaultContext {
    public static final String FILE_SEPARATOR = File.separator;
    private static final String USER_DIR = "user.dir";
    private static final String STORE = FILE_SEPARATOR + "store" + FILE_SEPARATOR;
    private static final String STORE_PATH = System.getProperty(USER_DIR) + STORE;
    public static final String PIC = "pic";
    public static final String TRAILS_FILE = "trails.txt";
    public static final String SHAPES_FILE = "shapes.txt";
    public static final String FILE_NOT_FOUND = "file not found";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String IMAGE_FORMAT = "png";
    public static final String DOT = ".";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String ID = "id";
    public static final String POINTS = "points";
    public static final String TRAILS = "trails";
    public static final String SELECT_COUNTER = "selectCounter";
    public static final String MARK = "mark";
    public static final String LOCATION_PICTURE = STORE_PATH + "pictures" + FILE_SEPARATOR;
    public static final String DATA_PATH = STORE_PATH + "data"+ FILE_SEPARATOR;
    public static final String FILE_HEAD = "file:";
    public static final String VIEW_BOX = "view-box";
    public static final double SCALE_RATE = 0.8;
}
