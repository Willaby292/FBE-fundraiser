package main;

import static main.SortUtil.Type.ALPHABETICAL;
import static main.SortUtil.Type.AMOUNT;
import static main.SortUtil.Type.CHRONOLOGICAL;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
import main.SortUtil.Type;

public class Application extends javafx.application.Application {

	public static void main(String[] args) {
		launch(args);
	}

	// Constants
	private static final String	TITLE				= "FBE Fundraiser";
	private static final String	TITLE_FORMAT		= "Goal : ${0}";
	private static final String	MESSAGE_FORMAT		= "Thank you {0} for donating ${1}";
	private static final String	FONT				= "Arial Monospace";
	private static final String	APPLE_LOGO_ALPHA	= "/resources/FBE_AppleIconALPHA_TRIMMED.png";

	private static final Double	PROGRESSBAR_WIDTH	= 600d;
	private static final Double	PROGRESSBAR_HEIGHT	= 600d;

	private final Map<String, Set<Donation>>	donations	= new HashMap<String, Set<Donation>>();
	private Double								target		= 5000d;

	// Class-level field because we want to be able to access it from anywhere.
	private Text					display;
	private Rectangle				progressBarAmount;
	private ObservableList<String>	donationsList;

	private boolean		lumpDonations	= false;
	private static Type	sortType		= CHRONOLOGICAL;

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
				setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
				setMaxWidth(PROGRESSBAR_WIDTH);
				setMaxHeight(PROGRESSBAR_HEIGHT);

				progressBarAmount = new Rectangle(PROGRESSBAR_WIDTH, 0, Color.SILVER);
				setAlignment(Pos.BOTTOM_CENTER);

				ImageView progressBarOutline = new ImageView(
						new Image(this.getClass().getResourceAsStream(APPLE_LOGO_ALPHA)));

				progressBarOutline.setFitWidth(PROGRESSBAR_WIDTH);
				progressBarOutline.setFitHeight(PROGRESSBAR_HEIGHT);

				getChildren().add(progressBarAmount);
				getChildren().add(progressBarOutline);
			}
		});

		// Setup the Right section
		pane.setRight(new ListView<String>() {
			{
				donationsList = FXCollections.observableArrayList();
				setItems(donationsList);
			}
		});

		// Setup the Bottom section
		pane.setBottom(new GridPane() {
			{
				setAlignment(Pos.CENTER);

				Label donationLabel = new Label("Donation: ");
				TextField amountInput = new TextField();

				Label donorLabel = new Label("Donor: ");
				TextField nameInput = new TextField();

				display = new Text();
				display.setFill(Color.FIREBRICK);

				Button submitDonation = new Button("Enter");
				submitDonation.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent e) {
						try {
							// Get the inputs, donation and donor.
							// NOTE: If donation isn't valid, catch the exception and tell the user.
							Double amount = Double.parseDouble(amountInput.getText());
							String name = nameInput.getText();
							Donation donation = new Donation(name, amount);

							updateUI(donation);

						} catch (NumberFormatException nfe) {
							// If we fail to get a valid Double input, then tell the user.
							display.setText("Donation must be a number");
						}
					}
				});

				Button toggleSortTypeButton = new Button(sortType.displayName());
				toggleSortTypeButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						toggleSortType();
						updateList();
					}

					private void toggleSortType() {
						switch (sortType) {
						case ALPHABETICAL:
							sortType = CHRONOLOGICAL;
							break;
						case CHRONOLOGICAL:
							sortType = AMOUNT;
							break;
						case AMOUNT:
							sortType = ALPHABETICAL;
							break;
						}
						toggleSortTypeButton.setText(sortType.displayName());
					}
				});

				Button lumpDonationsButton = new Button(lumpDonations ? "Total Donations" : "Individual Donations");
				lumpDonationsButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						lumpDonations = !lumpDonations;
						updateList();
					}
				});

				// Add Donation label and input to first row, first/second column respectively
				add(donationLabel, 0, 0);
				add(amountInput, 1, 0);

				// Add Donation label and input to second row, first/second column respectively
				add(donorLabel, 0, 1);
				add(nameInput, 1, 1);

				// Add Donation label and input to third row, first/second column respectively
				add(submitDonation, 0, 2);
				add(display, 1, 2);

				add(toggleSortTypeButton, 0, 3);
				add(lumpDonationsButton, 1, 3);

			}
		});

		pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		// Put everything together and show it.
		Scene scene = new Scene(pane);
		stage.setTitle(TITLE);
		stage.setScene(scene);
		stage.show();
	}

	private void updateUI(Donation donation) {
		// Update the current running total, set the height of the progressBar, update
		// the display list,and display message.
		Double amount = donation.getAmount();
		String name = donation.getName();

		progressBarAmount.setHeight(getPercent() * PROGRESSBAR_HEIGHT);

		updateDonations(donation);
		updateList();
		display.setText(MessageFormat.format(MESSAGE_FORMAT, name, amount));
	}

	private void updateDonations(Donation donation) {
		// If that person hasn't made a donation, make a set for them.
		String name = donation.getName();
		if (!donations.containsKey(name)) {
			donations.put(name, new HashSet<Donation>());
		}
		// Add the donation to the person's set.
		donations.get(name).add(donation);
	}

	private void updateList() {
		// Refresh the front end display list.
		donationsList.clear();
		donationsList.addAll(SortUtil.getAsSortedList(donations, sortType, lumpDonations));
	}

	private Double getPercent() {
		return Math.min(getCurrentTotal() / target, 1);
	}

	private Double getCurrentTotal() {
		Double sum = 0d;
		for (String name : donations.keySet()) {
			Set<Donation> donationsSet = donations.get(name);
			for (Donation donation : donationsSet) {
				sum += donation.getAmount();
			}
		}
		return sum;
	}

}