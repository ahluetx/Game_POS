/**
 * @author Alex Hutton
 * 
 * 
 */
package game.UI;


import java.net.URL;

import game.controllers.MainController;
import game.model.Store;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

	    Store gameStore = new Store();
	    gameStore.loadInventoryFromFile("game_inventory.txt");

	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
	    Parent root = loader.load();

	    MainController controller = loader.getController();
	    controller.setStore(gameStore);

	    Scene scene = new Scene(root);
	    
	    URL cssUrl = getClass().getResource("/styles.css");
	    if (cssUrl != null) {
	        scene.getStylesheets().add(cssUrl.toExternalForm());
	    } else {
	        System.out.println("Cannot find styles.css");
	    }

	    primaryStage.setTitle("Game Rental System");
	    primaryStage.setScene(scene);
	    
	    primaryStage.show();
	}

}


