package main;



import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
 
public class Thermometer extends Application {	
	
	private double donation;
	private double totalDonations;	
	
    public static void main(String[] args) {
        launch(args);
    }
    
    
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FBE Fundraiser");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Rectangle r = new Rectangle(200, 100, Color.RED);
        r.setWidth(20);
        r.setHeight(100);
        grid.add(r, 2, 1);
        // r.setAlignment(Pos.CENTER_RIGHT);
        
        Text sceneTitle = new Text ("Goal : $1000");
        sceneTitle.setFont(Font.font("Verdana", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);
        
        Label userInputLabel = new Label("Donation:");
        grid.add(userInputLabel, 2, 2);
        
        TextField donationTextField = new TextField();
        grid.add(donationTextField, 3, 2);
        
        Label donationName = new Label("Donator:");
        grid.add(donationName, 2, 3);
        
        TextField donatorTextField = new TextField();
        grid.add(donatorTextField, 3, 3);
        
//        Image appleLogo = new Image("file:FBE_AppleIconCOLOR.jpg");
//        ImageView iv = new ImageView(appleLogo);
//        iv.setFitHeight(100);
//        iv.setFitWidth(100);
//        grid.add(iv, 2, 2);
        
        Button btn = new Button("Enter");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 2, 4);
       
        final Text actiontarget = new Text();
        grid.add(actiontarget, 3, 4);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
       	 
            @Override
            public void handle(ActionEvent e) {
            	if(isDouble(donationTextField.getText())) {
            	donation = Double.parseDouble(donationTextField.getText());
            	totalDonations = totalDonations + donation;
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Thank you " + donatorTextField.getText() + " for donationg $" + donationTextField.getText());
            	}
            	else {
            		actiontarget.setFill(Color.FIREBRICK);
            		actiontarget.setText("Donation must be a number");
            	}
            }
        });
        
        grid.setGridLinesVisible(true);

        Scene scene = new Scene(grid, 500, 500, Color.WHITE);
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }
    
    boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
}