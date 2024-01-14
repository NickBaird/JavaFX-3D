package main;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

public class MainController {
	
	@FXML
	private AnchorPane background;
	
	@FXML
	private PerspectiveCamera camera;

	public void initialize() {
		
		Sphere sphere = new Sphere(50);
		sphere.setMaterial(new PhongMaterial());
		
		background.getChildren().add(sphere);
		
	}
	
}
