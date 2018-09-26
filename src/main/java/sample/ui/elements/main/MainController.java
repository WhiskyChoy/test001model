package sample.ui.elements.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sample.context.DefaultContext;
import sample.entities.Point;
import sample.entities.Shape;
import sample.entities.Trail;
import sample.utils.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

//千万注意不要引入错误的MouseEvent,如awt和swing的

public class MainController extends AnchorPane implements Initializable {

    private GraphicsContext context;

    //注意初始化比较特殊
    @Override
    public void initialize(URL loc, ResourceBundle rs) {
        context = canvas.getGraphicsContext2D();
        bindMode();
        renderFileImage();
    }

    @FXML
    private ToggleGroup mode;

    @FXML
    private Canvas canvas;
    @FXML
    private AnchorPane trailSaver;
    @FXML
    private JFXRadioButton btDraw;
    @FXML
    private JFXRadioButton btSelect;
    @FXML
    private JFXTextField tfMark;
    @FXML
    private JFXButton btConfirm;
    @FXML
    private VBox shapeSaver;
    @FXML
    private VBox fileSaver;
    @FXML
    private AnchorPane mainCanvasSaver;

    private Trail currentTrail;
    private Point startPoint;
    private ArrayList<Trail> trails = new ArrayList<>();
    private Shape currentShape;
    private HashSet<Shape> shapes = new HashSet<>();
    private String currentDirName = null;

    private void renderFileImage() {
        fileSaver.getChildren().clear();
        ArrayList<ImageWrapper> imageList = IOUtil.getImageList();
        for (ImageWrapper imageWrapper : imageList) {
            Image image = imageWrapper.getImage();
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
//            System.out.println(fileSaver.getWidth());
            imageView.setFitWidth(fileSaver.getPrefWidth() * DefaultContext.SCALE_RATE);
            imageView.getStyleClass().add(DefaultContext.VIEW_BOX);
            imageView.setOnMouseClicked(e -> {
                DrawUtil.clearCanvas(canvas);
                String imageName = imageWrapper.getImageName();
                MessageGetter getter = IOUtil.getMessageByImageName(imageName);
                currentDirName = imageName.replaceAll(DefaultContext.DOT + DefaultContext.IMAGE_FORMAT, "");
                trails = getter.getTrails();
                shapes = getter.getShapes();
                renderTrails();
                renderShapes();
            });
            fileSaver.getChildren().add(imageView);
        }
    }

    private void renderTrails() {
        trailSaver.getChildren().clear();
        for (Trail trail : trails) {
            Canvas tempCanvas = new Canvas(trailSaver.getWidth(), trailSaver.getHeight());
            trailSaver.getChildren().add(tempCanvas);

            AnchorPane.setTopAnchor(tempCanvas, 0.0);
            AnchorPane.setBottomAnchor(tempCanvas, 0.0);
            AnchorPane.setLeftAnchor(tempCanvas, 0.0);
            AnchorPane.setRightAnchor(tempCanvas, 0.0);
            trail.setCanvas(tempCanvas);

            DrawUtil.drawOneTrail(tempCanvas.getGraphicsContext2D(), trail);
        }
    }

    private void bindMode() {
        mode.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
                    DrawUtil.clearCanvas(canvas);
                    setAllTrailsNormal();
                    if (new_toggle == btDraw) {
                        closeEdit();
                    }
                }
        );
    }

    private boolean isDrawing() {
        return mode.getSelectedToggle() == btDraw;
    }

    private boolean isSelecting() {
        return mode.getSelectedToggle() == btSelect;
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (isDrawing()) {
            handleDrawPressed(event);
        }
        if (isSelecting()) {
            handleSelectPressed(event);
        }
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        if (isDrawing()) {
            handleDrawDragged(event);
        }
        if (isSelecting()) {
            handleSelectDragged(event);
        }
    }

    @FXML
    private void onMouseReleased(MouseEvent event) {
        if (isDrawing()) {
            handleDrawReleased();
        }
        if (isSelecting()) {
            handleSelectReleased(event);
        }
    }

    @FXML
    private void onConfirm() {
        currentShape.setMark(tfMark.getText());
    }

    //注意不要用Typed，这样getCode可能不对
    @FXML
    private void onEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onConfirm();
        }
    }

    @FXML
    private void initBoard() {
        trailSaver.getChildren().clear();
        fileSaver.getChildren().clear();
        shapeSaver.getChildren().clear();
        renderFileImage();
        DrawUtil.clearCanvas(canvas);
        currentTrail = null;
        startPoint = null;
        trails = new ArrayList<>();
        currentShape = null;
        shapes = new HashSet<>();
        tfMark.setText("");
        setActualEditable(false);
        btDraw.setSelected(true);
        currentDirName = null;
    }

    @FXML
    private void saveFile() {
        String newDirName;
        if (currentDirName == null) {
            newDirName = DefaultContext.PIC + System.currentTimeMillis();
        } else {
            newDirName = currentDirName;
        }
        if (IOUtil.saveFile(mainCanvasSaver.snapshot(null, null), trails, shapes, newDirName)) {
            currentDirName = newDirName;
            renderFileImage();
        }
    }

    @FXML
    private void onClearAllFiles() {
        IOUtil.deleteDir(DefaultContext.LOCATION_PICTURE);
        IOUtil.deleteDir(DefaultContext.DATA_PATH);
        renderFileImage();
        initBoard();
    }

    private void renderShapes() {
        shapeSaver.getChildren().clear();
        for (Shape shape : shapes) {
            Canvas tempCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
            for (Trail trail : shape.getTrails()) {
                DrawUtil.drawOneTrail(tempCanvas.getGraphicsContext2D(), trail);
            }
            Image image = tempCanvas.snapshot(null, null);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(shapeSaver.getWidth() * DefaultContext.SCALE_RATE);
            imageView.getStyleClass().add(DefaultContext.VIEW_BOX);
            imageView.setOnMouseClicked(e -> {
                setAllTrailsNormal();
                DrawUtil.clearCanvas(canvas);
                for (Trail trail : trails) {
                    if (shape.getTrails().contains(trail)) {
                        trail.changeColor();
                        setActualEditable(true);
                    }
                }
                tfMark.setText(shape.getMark());
            });
            shapeSaver.getChildren().add(imageView);
        }
    }

    private void handleDrawPressed(MouseEvent event) {
        context.beginPath();
        currentTrail = new Trail();
        double x = event.getX();
        double y = event.getY();
        currentTrail.add(new Point(x, y));
        context.moveTo(x, y);
    }

    private void handleDrawDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        currentTrail.add(new Point(x, y));
        context.lineTo(x, y);
        context.stroke();
    }

    private void handleDrawReleased() {
        Canvas tempCanvas = new Canvas(trailSaver.getWidth(), trailSaver.getHeight());
        trailSaver.getChildren().add(tempCanvas);

        AnchorPane.setTopAnchor(tempCanvas, 0.0);
        AnchorPane.setBottomAnchor(tempCanvas, 0.0);
        AnchorPane.setLeftAnchor(tempCanvas, 0.0);
        AnchorPane.setRightAnchor(tempCanvas, 0.0);

        DrawUtil.drawOneTrail(tempCanvas.getGraphicsContext2D(), currentTrail);
        currentTrail.setCanvas(tempCanvas);
        trails.add(currentTrail);
        DrawUtil.clearCanvas(canvas);
    }

    private void handleSelectPressed(MouseEvent event) {
        DrawUtil.clearCanvas(canvas);
        startPoint = new Point(event.getX(), event.getY());
    }

    private void handleSelectDragged(MouseEvent event) {
        DrawUtil.clearCanvas(canvas);
        double x = Math.min(startPoint.getX(), event.getX());
        double y = Math.min(startPoint.getY(), event.getY());
        double width = Math.abs(event.getX() - startPoint.getX());
        double height = Math.abs(event.getY() - startPoint.getY());
        context.strokeRect(x, y, width, height);
    }

    private void handleSelectReleased(MouseEvent event) {
        double x = Math.min(startPoint.getX(), event.getX());
        double y = Math.min(startPoint.getY(), event.getY());
        double width = Math.abs(event.getX() - startPoint.getX());
        double height = Math.abs(event.getY() - startPoint.getY());
        currentShape = new Shape();
        for (Trail trail : trails) {
            if (trail.isInRect(x, y, width, height)) {
                currentShape.add(trail);
                trail.changeColor();
            } else {
                trail.initColor();
            }
        }
        if (currentShape.getNumOfTrail() > 0) {
            boolean existed = false;
            for (Shape shape : shapes) {
                if (shape.equals(currentShape)) {
                    currentShape = shape;
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                currentShape.setMark(Recognizer.recognize(currentShape));
                shapes.add(currentShape);
                for (Trail trail : currentShape.getTrails()) {
                    trail.setSelectCounter(trail.getSelectCounter() + 1);
                }
                renderShapes();
            }
            openEdit();
            tfMark.setText(currentShape.getMark());
        }
    }

    private void openEdit() {
        setActualEditable(true);
    }

    private void closeEdit() {
        setActualEditable(false);
    }

    private void setActualEditable(boolean val) {
        tfMark.setDisable(!val);
        tfMark.setEditable(val);
        btConfirm.setDisable(!val);
    }

    private void setAllTrailsNormal() {
        for (Trail trail : trails) {
            trail.initColor();
        }
    }
}
