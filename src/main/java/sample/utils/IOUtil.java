package sample.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.context.DefaultContext;
import sample.entities.Point;
import sample.entities.Shape;
import sample.entities.Trail;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class IOUtil {
    public static boolean saveFile(Image image, ArrayList<Trail> trails, HashSet<Shape> shapes, String dirName) {
        String beginPath = DefaultContext.DATA_PATH;
        String path = beginPath + dirName;

        File file = new File(path);
        if (!file.exists()) {
            boolean createDirSuccessful = file.mkdirs();
            if (!createDirSuccessful) {
                return false;
            }
        }

        File dirImage = new File(DefaultContext.LOCATION_PICTURE);
        if (!dirImage.exists()) {
            boolean createDirSuccessful = dirImage.mkdirs();
            if (!createDirSuccessful) {
                return false;
            }
        }

        IOUtil.saveImageToFile(image, DefaultContext.LOCATION_PICTURE + DefaultContext.FILE_SEPARATOR + dirName + DefaultContext.DOT + DefaultContext.IMAGE_FORMAT);
        String trailPath = path + DefaultContext.FILE_SEPARATOR + DefaultContext.TRAILS_FILE;
        String shapesPath = path + DefaultContext.FILE_SEPARATOR + DefaultContext.SHAPES_FILE;

        clearFile(trailPath);
        clearFile(shapesPath);

        for (Trail trail : trails) {
            JSONObject jsonObject = IOUtil.trailToJSON(trail);
            addMessage(trailPath, jsonObject + DefaultContext.NEW_LINE);
        }
        for (Shape shape : shapes) {
            JSONObject jsonObject = IOUtil.shapeToJSON(shape);
            addMessage(shapesPath, jsonObject + DefaultContext.NEW_LINE);
        }
        return true;
    }

    private static void saveImageToFile(Image image, String path) {
        File outputFile = new File(path);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, DefaultContext.IMAGE_FORMAT, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addMessage(String path, String data) {
        File file = new File(path);
        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(data);
        } catch (IOException e) {
            System.out.println(DefaultContext.FILE_NOT_FOUND);
        }
    }

    private static JSONObject trailToJSON(Trail trail) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DefaultContext.ID, trail.getId());
        jsonObject.put(DefaultContext.SELECT_COUNTER, trail.getSelectCounter());
        JSONArray jsonArray = new JSONArray();
        for (Point point : trail.getPoints()) {
            jsonArray.put(IOUtil.pointToJSON(point));
        }
        jsonObject.put(DefaultContext.POINTS, jsonArray);
        return jsonObject;
    }

    private static JSONObject shapeToJSON(Shape shape) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DefaultContext.ID, shape.getId());
        jsonObject.put(DefaultContext.MARK, shape.getMark());
        JSONArray jsonArray = new JSONArray();
        for (Trail trail : shape.getTrails()) {
            jsonArray.put(trail.getId());
        }
        jsonObject.put(DefaultContext.TRAILS, jsonArray);
        return jsonObject;
    }

    private static JSONObject pointToJSON(Point point) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DefaultContext.X, point.getX());
        jsonObject.put(DefaultContext.Y, point.getY());
        return jsonObject;
    }

    public static ArrayList<ImageWrapper> getImageList() {
        File file = new File(DefaultContext.LOCATION_PICTURE);
        ArrayList<ImageWrapper> result = new ArrayList<>();
        String[] pictureList = file.list();
        if (pictureList != null) {
            for (String string : pictureList) {
//                System.out.println(DefaultContext.LOCATION_PICTURE + string);
                //要加file头
                result.add(new ImageWrapper(new Image(DefaultContext.FILE_HEAD + DefaultContext.LOCATION_PICTURE + string), string));
            }
        }
        return result;
    }

    public static MessageGetter getMessageByImageName(String imageName) {
        String dirName = imageName.replaceAll(DefaultContext.DOT + DefaultContext.IMAGE_FORMAT, "");
        String path = DefaultContext.DATA_PATH + dirName + DefaultContext.FILE_SEPARATOR;
        String trailPath = path + DefaultContext.TRAILS_FILE;
        String shapePath = path + DefaultContext.SHAPES_FILE;
        ArrayList<Trail> trails = new ArrayList<>();
        HashSet<Shape> shapes = new HashSet<>();
        File trailFile = new File(trailPath);
        File shapeFile = new File(shapePath);
        if (trailFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(trailFile));
                String oneLine;
                while ((oneLine = reader.readLine()) != null) {
                    ArrayList<Point> points = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(oneLine);
                    long id = jsonObject.getLong(DefaultContext.ID);
                    int selectCounter = jsonObject.getInt(DefaultContext.SELECT_COUNTER);
                    JSONArray jsonArray = jsonObject.getJSONArray(DefaultContext.POINTS);
                    for (Object object : jsonArray) {
                        JSONObject pointJsonObject = (JSONObject) object;
                        Point point = new Point(pointJsonObject.getDouble(DefaultContext.X), pointJsonObject.getDouble(DefaultContext.Y));
                        points.add(point);
                    }
                    Trail trail = new Trail(points, id, selectCounter);
                    trails.add(trail);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (shapeFile.exists()) {
                try {
                    reader = new BufferedReader(new FileReader(shapeFile));
                    String oneLine;
                    while ((oneLine = reader.readLine()) != null) {
                        HashSet<Trail> shapeTrails = new HashSet<>();
                        JSONObject jsonObject = new JSONObject(oneLine);
                        long id = jsonObject.getLong(DefaultContext.ID);
                        String mark = jsonObject.getString(DefaultContext.MARK);
                        JSONArray jsonArray = jsonObject.getJSONArray(DefaultContext.TRAILS);
                        for (Object object : jsonArray) {
                            long trailId = (long) object;
                            for (Trail trail : trails) {
                                if (trail.getId() == trailId) {
                                    shapeTrails.add(trail);
                                }
                            }
                        }
                        Shape shape = new Shape(shapeTrails, id, mark);
                        shapes.add(shape);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return new MessageWrapper(trails, shapes);
    }

    private static void clearFile(String fileName) {
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteDir(String path) {
        File file = new File(path);
        if (!file.exists()) {//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
        }

        String[] content = file.list();//取得当前目录下所有文件和文件夹
        if (content != null) {
            for (String name : content) {
                File temp = new File(path, name);
                if (temp.isDirectory()) {//判断是否是目录
                    deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                    if (!temp.delete()) {  //删除空目录
                        System.err.println("Failed to delete " + name);
                    }
                } else {
                    if (!temp.delete()) {//直接删除文件
                        System.err.println("Failed to delete " + name);
                    }
                }
            }
        }
    }

}
