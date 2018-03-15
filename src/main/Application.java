package main;

import static main.SortUtil.Type.ALPHABETICAL;
import static main.SortUtil.Type.AMOUNT;
import static main.SortUtil.Type.CHRONOLOGICAL;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import effects.Firework;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.SortUtil.Type;

public class Application extends javafx.application.Application {

	public static void main(String[] args) {
		launch(args);
	}

	// Constants
	private static final String	DISPLAY_TITLE		= "FBE Fundraiser";
	private static final String	CONTROL_TITLE		= "Controls";
	private static final String	HEADER_FORMAT		= "${0} of ${1}";
	private static final String	FONT				= "Arial Monospace";
	private static final String	APPLE_LOGO_ALPHA	= "/resources/FBE_AppleIconALPHA_TRIMMED.png";
	private static final String	APPLE_ICON_ALPHA	= "/resources/FBE_AppleIconCOLOR.png";

	private static final Insets INSETS = new Insets(25, 25, 25, 25);

	private final Map<String, Set<Donation>>	donations	= new HashMap<String, Set<Donation>>();
	private Double								target		= 25000d;

	// Class-level field because we want to be able to access it from anywhere.
	private Stage	controlStage;
	private Stage	displayStage;
	private Pane	fireworkPane;

	private Text					header;
	private Rectangle				progressBarAmount;
	private ObservableList<String>	donationsList;

	private Timeline	celebration;
	private boolean		lumpDonations	= false;
	private static Type	sortType		= CHRONOLOGICAL;

	boolean on = true;

	@Override
	public void start(Stage displayStage) {
		this.displayStage = displayStage;
		this.controlStage = new Stage();

		reload();
	}

	public void reload() {
		reloadProperties();

		setupDisplayStage();
		setupControlStage();
		loadDonations();
		updateDisplay();
	}

	private void setupDisplayStage() {
		BorderPane displayPane = new BorderPane();
		displayPane.setPadding(INSETS);

		// Setup the Top section
		displayPane.setTop(new HBox() {
			{
				setAlignment(Pos.CENTER);

				header = new Text();
				header.setTextAlignment(TextAlignment.CENTER);
				header.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 48));
				header.setFill(Color.WHITE);
				getChildren().add(header);

			}
		});

		// Setup the Center section
		displayPane.setCenter(new StackPane() {
			{
				Double width = PropertyReader.SIZE_WIDTH.getValue();
				Double height = PropertyReader.SIZE_HEIGHT.getValue();
				setBackground(
						new Background(new BackgroundFill(PropertyReader.BACKGROUND_COLOR.getValue(), null, null)));
				setMaxWidth(width);
				setMaxHeight(height);

				progressBarAmount = new Rectangle(width, 0, PropertyReader.FILL_COLOR.getValue());
				setAlignment(Pos.BOTTOM_CENTER);

				ImageView progressBarOutline = new ImageView(
						new Image(this.getClass().getResourceAsStream(APPLE_LOGO_ALPHA)));

				progressBarOutline.setFitWidth(width);
				progressBarOutline.setFitHeight(height);

				getChildren().add(progressBarAmount);
				getChildren().add(progressBarOutline);
			}
		});

		displayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0,62,113), null, null)));

		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(displayPane);

		fireworkPane = new Pane();
		celebration = new Timeline(new KeyFrame(Duration.millis(500), func -> {
			Integer amount = MathUtils.randInRange(3, 5);
			Firework.launch(fireworkPane, amount);
		}));
		celebration.setCycleCount(Animation.INDEFINITE);
		stackPane.getChildren().add(fireworkPane);

		// Put everything together and show it.
		Scene displayScene = new Scene(stackPane);
		displayStage.setTitle(DISPLAY_TITLE);
		displayStage.getIcons().add(new Image(this.getClass().getResourceAsStream(APPLE_ICON_ALPHA)));
		displayStage.setScene(displayScene);
		displayStage.show();

	}

	private void setupControlStage() {
		BorderPane controlPane = new BorderPane();
		controlPane.setPadding(INSETS);

		// Setup the Top section
		controlPane.setTop(new GridPane() {
			{
				setAlignment(Pos.CENTER);

				Label donationLabel = new Label("Donation: ");
				TextField amountInput = new TextField();

				Label donorLabel = new Label("Donor: ");
				TextField nameInput = new TextField();

				Button submitDonation = new Button("Enter");
				submitDonation.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent e) {
						try {
							// Get the inputs, donation and donor.
							// NOTE: If donation isn't valid, catch the exception and tell the user.
							Double amount = Double.parseDouble(amountInput.getText());
							String name = nameInput.getText();
							name = name.isEmpty() ? "Anonymous" : name;
							Donation donation = new Donation(name, amount);

							update(donation);
							amountInput.clear();
							nameInput.clear();

							Firework.launch(fireworkPane, (int) (donation.getAmount() / 10));

						} catch (NumberFormatException nfe) {
							// If we fail to get a valid Double input, then tell the user.
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("Warning Dialog");
							alert.setHeaderText(null);
							alert.setContentText("Donation must be a number");
							alert.showAndWait();
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
						lumpDonationsButton.setText(lumpDonations ? "Total Donations" : "Individual Donations");
						updateList();
					}
				});
				add(donationLabel, 0, 0);
				add(amountInput, 1, 0);

				add(donorLabel, 0, 1);
				add(nameInput, 1, 1);

				add(submitDonation, 0, 2);

				add(toggleSortTypeButton, 0, 3);
				add(lumpDonationsButton, 1, 3);

			}
		});

		// Setup the Center section
		controlPane.setCenter(new ListView<String>() {
			{
				donationsList = FXCollections.observableArrayList();
				setItems(donationsList);
			}
		});

		// Setup the Bottom section
		controlPane.setBottom(new GridPane() {
			{
				Button clearDonations = new Button("Clear All");
				clearDonations.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						donations.clear();
						updateDisplay();
						saveDonations();
					}
				});
				Button reloadButton = new Button("Reload");
				reloadButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						reload();
					}
				});
				add(clearDonations, 0, 0);
				add(reloadButton, 1, 0);

			}
		});

		Scene controlScene = new Scene(controlPane);
		controlStage.setScene(controlScene);
		controlStage.setTitle(CONTROL_TITLE);
		controlStage.getIcons().add(new Image(this.getClass().getResourceAsStream(APPLE_ICON_ALPHA)));
		controlStage.show();
	}

	private void update(Donation donation) {
		// Update the current running total, set the height of the progressBar, update
		// the display list,and display message.
		addDonation(donation);
		updateDisplay();
		saveDonations();
	}

	private void updateDisplay() {
		updateList();
		progressBarAmount.setHeight(getPercent() * PropertyReader.SIZE_HEIGHT.getValue());
		header.setText(MessageFormat.format(HEADER_FORMAT, getCurrentTotal(), target));
		boolean targetReached = getCurrentTotal() >= target;
		if (targetReached) {
			startCelebration();
		} else {
			stopCelebration();
		}
	}

	private void addDonation(Donation donation) {
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
		List<Donation> sortedList = SortUtil.getAsSortedList(donations, sortType, lumpDonations);
		List<String> toStringList = sortedList.stream().map(donation -> donation.toString())
				.collect(Collectors.toList());
		donationsList.addAll(toStringList);
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

	private void saveDonations() {
		SaveUtil.save(SortUtil.getAsSortedList(donations, Type.CHRONOLOGICAL, false));
	}

	private void loadDonations() {
		List<Donation> list = SaveUtil.load();
		for (Donation donation : list) {
			addDonation(donation);
		}
	}

	private void reloadProperties() {
		PropertyReader.reload();
	}

	public void startCelebration() {
		celebration.play();
	}

	public void stopCelebration() {
		celebration.stop();
	}

}