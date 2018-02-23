package main;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

	public static void main(String[] args) {
		launch(args);
	}

	private enum SortType {
		ALPHABETICAL,
		TOTAL_DONATIONS,
		CHRONOLOGICAL;
	}

	private static SortType sortType = SortType.CHRONOLOGICAL;

	// Constants
	private static final String	TITLE				= "FBE Fundraiser";
	private static final String	TITLE_FORMAT		= "Goal : ${0}";
	private static final String	MESSAGE_FORMAT		= "Thank you {0} for donating ${1}";
	private static final String	DONATION_FORMAT		= "{0} - ${1}";
	private static final String	FONT				= "Arial Monospace";
	private static final String	APPLE_LOGO_ALPHA	= "file:FBE_AppleIconALPHA.png";

	private static final double	PROGRESSBAR_WIDTH	= 800;
	private static final double	PROGRESSBAR_HEIGHT	= 800;

	private double	current	= 0;
	private double	target	= 5000;

	private Map<String, Double>		donorMap	= new HashMap<String, Double>();
	private ObservableList<String>	donorList;

	// Class-level field because we want to be able to access it from anywhere.
	private Text		display;
	private Rectangle	progressBarAmount;

	@Override
	public void start(Stage stage) {

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(25, 25, 25, 25));

		// Setup the Top section
		pane.setTop(new HBox() {
			{
				setAlignment(Pos.CENTER);

				Text title = new Text();
				title.setTextAlignment(TextAlignment.CENTER);
				title.setText(MessageFormat.format(TITLE_FORMAT, target));
				title.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 48));
				getChildren().add(title);
			}
		});

		// Setup the Center section
		pane.setCenter(new StackPane() {
			{
				setMaxWidth(PROGRESSBAR_WIDTH);
				setMaxHeight(PROGRESSBAR_HEIGHT);

				progressBarAmount = new Rectangle(PROGRESSBAR_WIDTH, 0, Color.RED);
				progressBarAmount.setTranslateY(0);

				ImageView progressBarOutline = new ImageView(new Image(APPLE_LOGO_ALPHA));

				progressBarOutline.setFitWidth(PROGRESSBAR_WIDTH);
				progressBarOutline.setFitHeight(PROGRESSBAR_HEIGHT);

				getChildren().add(progressBarAmount);
				getChildren().add(progressBarOutline);
			}
		});

		// Setup the Right section
		pane.setRight(new ListView<String>() {
			{
				donorList = FXCollections.observableArrayList();
				setItems(donorList);
			}
		});

		// Setup the Bottom section
		pane.setBottom(new GridPane() {
			{
				setAlignment(Pos.CENTER);

				Label donationLabel = new Label("Donation: ");
				TextField donationInput = new TextField();

				Label donorLabel = new Label("Donor: ");
				TextField donorInput = new TextField();

				display = new Text();
				display.setFill(Color.FIREBRICK);

				Button submitDonation = new Button("Enter");
				submitDonation.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent e) {
						try {
							// Get the inputs, donation and donor.
							// NOTE: If donation isn't valid, catch the exception and tell the user.
							Double donation = Double.parseDouble(donationInput.getText());
							String donor = donorInput.getText();

							updateProgressBar(donation, donor);
							updateDonorList(donation, donor);

						} catch (NumberFormatException nfe) {
							// If we fail to get a valid Double input, then tell the user.
							display.setText("Donation must be a number");
						}
					}
				});

				Button toggleSortType = new Button("Toggle Sort");
				toggleSortType.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent e) {
						toggleSortType();
					}

				});

				// Add Donation label and input to first row, first/second column respectively
				add(donationLabel, 0, 0);
				add(donationInput, 1, 0);

				// Add Donation label and input to second row, first/second column respectively
				add(donorLabel, 0, 1);
				add(donorInput, 1, 1);

				// Add Donation label and input to third row, first/second column respectively
				add(submitDonation, 0, 2);
				add(display, 1, 2);

				add(toggleSortType, 0, 3);

			}
		});

		// Put everything together and show it.
		Scene scene = new Scene(pane);
		stage.setTitle(TITLE);
		stage.setScene(scene);
		stage.show();
	}

	private void updateProgressBar(Double donation, String donor) {
		// If no donor was given, assign it to 'Anonymous'.
		if (donor.isEmpty()) {
			donor = "Anonymous";
		}

		// Update the current, set the height of the progressBar, and display message.
		current += donation;
		progressBarAmount.setHeight(getPercent() * PROGRESSBAR_HEIGHT);
		display.setText(MessageFormat.format(MESSAGE_FORMAT, donor, donation));
	}

	private void updateDonorList(Double donation, String donor) {
		// Add the donation to the map.
		if (!donorMap.containsKey(donor)) {
			// If it's a first time donor, add him to map with donation.
			donorMap.put(donor, donation);
		} else {
			// If it's a repeat donor, put his running total into the map.
			donorMap.put(donor, donorMap.get(donor) + donation);
		}

		switch (sortType) {
		case ALPHABETICAL:
			donorList.clear();
			for (String name : donorMap.keySet()) {
				Double total = donorMap.get(name);
				donorList.add(MessageFormat.format(DONATION_FORMAT, name, total));
			}
			Collections.sort(donorList);

			break;
		// FIXME: Chronological order works only if you start with it and never toggle
		// sortType.
		// It simply adds the next donor to the end of the list. If the list was
		// previously sorted, it wont resort
		// TODO: Save the timestamp of the donation and sort by that.
		case CHRONOLOGICAL:
			donorList.add(MessageFormat.format(DONATION_FORMAT, donor, donation));
			break;
		case TOTAL_DONATIONS:
			donorList.clear();
			for (String name : donorMap.keySet()) {
				Double total = donorMap.get(name);
				donorList.add(MessageFormat.format(DONATION_FORMAT, name, total));
			}
			donorList.sort(new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					Double d1 = Double.parseDouble(s1.substring(s1.indexOf("$") + 1));
					Double d2 = Double.parseDouble(s2.substring(s2.indexOf("$") + 1));

					return Double.compare(d2, d1);
				}
			});
		}
	}

	private void toggleSortType() {
		switch (sortType) {
		case ALPHABETICAL:
			sortType = SortType.CHRONOLOGICAL;
			return;
		case CHRONOLOGICAL:
			sortType = SortType.TOTAL_DONATIONS;
			return;
		case TOTAL_DONATIONS:
			sortType = SortType.ALPHABETICAL;
			return;
		}
	}

	private double getPercent() {
		return current / target;
	}

}