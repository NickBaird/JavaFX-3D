package main;

import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Light;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainLauncher extends Application {
	
	public static Stage primaryStage;
	
	public boolean dragging = false;
	public double oldX, oldY, deltaX, deltaY;
	
	public static final int X_ROWS = 80, Y_ROWS = 80, Z_ROWS = 80;
	public static final int RADIUS = 50, GAP = 20;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			
			Camera camera = new PerspectiveCamera(true);
			//camera.setNearClip();
			camera.setFarClip(1000000);
			camera.translateZProperty().set(-16000);

			Group boxes = new Group();
			Nodes nodes = new Nodes(X_ROWS, Y_ROWS, Z_ROWS, GAP, RADIUS);
			boxes.getChildren().add(nodes.toGroup());
			boxes.getChildren().add(nodes.border());
			AmbientLight light = new AmbientLight();
			//PointLight light = new PointLight();
			light.setColor(Color.WHITE);
			light.getTransforms().add(new Translate(0, -50, -200));
					
			Group group = new Group();
			group.getChildren().addAll(camera, boxes, light);
	
			Translate pivot = new Translate();
	        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
	        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);
	        
	        boxes.getTransforms().addAll (
	                pivot,
	                yRotate,
	                zRotate,
	                new Rotate(-10, Rotate.X_AXIS),
	                new Translate(0, 0, -10)
	        );

	        // animate the camera position.
	        Timeline timeline = new Timeline(
	                new KeyFrame(
	                        Duration.seconds(0), 
	                        new KeyValue(yRotate.angleProperty(), 0),
	                        new KeyValue(zRotate.angleProperty(), 0)
	                ),
	                new KeyFrame(
	                        Duration.seconds(15), 
	                        new KeyValue(yRotate.angleProperty(), 360),
	                        new KeyValue(zRotate.angleProperty(), 360)
	                )
	        );
	        timeline.setCycleCount(Timeline.INDEFINITE);
	        timeline.play();
	        
	        group.getChildren().stream()
            .filter(node -> !(node instanceof Camera))
            .forEach(node ->
                    node.setOnMouseClicked(event -> {
                        pivot.setX(node.getTranslateX());
                        pivot.setY(node.getTranslateY());
                        pivot.setZ(node.getTranslateZ());
                    })
            );
	        
	        SubScene subScene = new SubScene(group, 1280, 720, true, SceneAntialiasing.BALANCED); 
	        Group root = new Group(subScene);
	        subScene.setCamera(camera);
	        Scene scene = new Scene(root);
	        scene.setOnKeyPressed(e -> {
				switch(e.getCode()) {
					case W:
						group.translateZProperty().set(group.getTranslateZ() - Z_ROWS); break;
					case A:
						group.translateXProperty().set(group.getTranslateX() + X_ROWS); break;
					case S:
						group.translateZProperty().set(group.getTranslateZ() + Z_ROWS); break;
					case D:
						group.translateXProperty().set(group.getTranslateX() - X_ROWS); break;
					case Q:
						nodes.hide(); break;
					case E:
						nodes.show(); break;
					case R:
						nodes.pickTwoRandom(); break;
					default: break;
				}
			});
			
			
		    primaryStage.setScene(scene);
		    primaryStage.setWidth(1280);
		    primaryStage.setHeight(720);
		    
		    primaryStage.setResizable(true);
		    primaryStage.setTitle("3D Test");
		    primaryStage.show();
		    
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public static void onLaunch(String[] args) {
		MainLauncher.launch(args);
	}
}
