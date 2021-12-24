package v3.form.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javafx.animatednodes.AnimatedButton;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	private static final int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 500;
	private static final File ACCOUNTS_FILE = new File("C:/Accounts/LoginFormV3/accounts.txt");
	// NAME|EMAIL|PASSWORD
	private static final String ACCOUNT_STORE_FORMAT = "%s|%s|%s";
	private static final Image PROFILE_IMAGE = new Image(Main.class.getResourceAsStream("images/profile.png")),
		MENU_BUTTON_IMAGE = new Image(Main.class.getResourceAsStream("images/menubutton.png"));
	private static final int FONT_SIZE = 20;
	private static final String EMAIL_REGEX = "^(.+)@(.+)$";

	private Scene scene;
	private StackPane introPane, loginPane, registerPane, loggedInPane, profilePane, menuPane;
	// Intro Pane
	private Label titleLabel;
	// Login Pane
	private AnimatedButton loginButton, registerButton;
	private TextField emailField;
	private PasswordField passwordField;
	private Label messageLabel;
	// Register Pane
	private AnimatedButton okButton, cancelButton;
	private TextField rNameField, rEmailField;
	private PasswordField rPasswordField, rConfirmPasswordField;
	private Label rMessageLabel;
	// Logged In Pane
	private Button menuButton;
	// Menu Pane
	private ImageView profileImage;
	private AnimatedButton profileButton, logoutButton;
	private Label nameLabel;
	// Profile Pane
	private ImageView profileImage0;
	private Label infoLabel;

	private StackPane currentPane;

	private ArrayList<Account> accounts;
	private DoubleProperty stageWidth, stageHeight;
	private boolean viewProfile, viewMenu;

	public Main() {
		introPane = new StackPane();
		loginPane = new StackPane();
		registerPane = new StackPane();
		loggedInPane = new StackPane();
		profilePane = new StackPane();
		menuPane = new StackPane();

		scene = new Scene(introPane, SCREEN_WIDTH, SCREEN_HEIGHT);

		loginButton = new AnimatedButton("LOGIN", FONT_SIZE);
		registerButton = new AnimatedButton("REGISTER", FONT_SIZE);
		emailField = new TextField();
		passwordField = new PasswordField();
		okButton = new AnimatedButton("OK", FONT_SIZE);
		cancelButton = new AnimatedButton("CANCEL", FONT_SIZE);
		rNameField = new TextField();
		rEmailField = new TextField();
		rPasswordField = new PasswordField();
		rConfirmPasswordField = new PasswordField();
		menuButton = new Button();
		profileButton = new AnimatedButton("PROFILE", FONT_SIZE);
		logoutButton = new AnimatedButton("LOGOUT", FONT_SIZE);
		messageLabel = new Label();
		rMessageLabel = new Label();

		profileImage = new ImageView(PROFILE_IMAGE);
		profileImage0 = new ImageView(PROFILE_IMAGE);

		stageHeight = new SimpleDoubleProperty();
		stageWidth = new SimpleDoubleProperty();

		titleLabel = new Label("LOGIN FORM V3");
		infoLabel = new Label();
		nameLabel = new Label();

		loadAccounts();
	}

	@Override
	public void start(Stage stage) {
		introPane.setId("INTRO");
		loginPane.setId("LOGIN");
		registerPane.setId("REGISTER");
		loggedInPane.setId("LOGGEDIN");
		profilePane.setId("PROFILE");
		titleLabel.setId("TITLE");
		menuPane.setId("MENU");
		menuButton.setId("MENUB");
		infoLabel.setId("INFO");

		introPane.getChildren().add(titleLabel);

		AnimatedButton[] buttons = { loginButton, registerButton, okButton, cancelButton, profileButton };
		for (AnimatedButton button : buttons) button.setShape(new Rectangle(button.getPrefWidth(), button.getPrefHeight()));

		TextField[] fields = { emailField, passwordField, rNameField, rEmailField, rPasswordField, rConfirmPasswordField };
		for (TextField field : fields) {
			field.setPrefWidth(200);
			field.setShape(new Rectangle(field.getPrefWidth(), 30));
		}

		// Login Pane

		HBox[] lHBoxes = new HBox[3];
		VBox lVBox = new VBox();

		for (int i = 0; i < lHBoxes.length; i++) {
			lHBoxes[i] = new HBox();
			lHBoxes[i].setSpacing(10);
			lHBoxes[i].setAlignment(Pos.CENTER);
		}

		lHBoxes[0].getChildren().addAll(new Label("EMAIL"), emailField);
		lHBoxes[1].getChildren().addAll(new Label("PASSWORD"), passwordField);
		lHBoxes[2].getChildren().addAll(loginButton, registerButton);

		lVBox.setSpacing(20);
		lVBox.setAlignment(Pos.CENTER);

		Label loginLabel = new Label("LOGIN");
		loginLabel.setStyle("-fx-font-size: 40.0px;");

		lVBox.getChildren().add(loginLabel);
		lVBox.getChildren().addAll(lHBoxes);
		lVBox.getChildren().add(messageLabel);

		loginPane.getChildren().add(lVBox);

		loginButton.setOnAction(e -> login(emailField.getText(), passwordField.getText()));
		registerButton.setOnAction(e -> fadeTransition(registerPane));

		// Register Pane

		HBox[] rHBoxes = new HBox[5];
		VBox rVBox = new VBox();

		for (int i = 0; i < rHBoxes.length; i++) {
			rHBoxes[i] = new HBox();
			rHBoxes[i].setSpacing(10);
			rHBoxes[i].setAlignment(Pos.CENTER);
		}

		rVBox.setSpacing(20);
		rVBox.setAlignment(Pos.CENTER);

		rHBoxes[0].getChildren().addAll(new Label("NAME"), rNameField);
		rHBoxes[1].getChildren().addAll(new Label("EMAIL"), rEmailField);
		rHBoxes[2].getChildren().addAll(new Label("PASSWORD"), rPasswordField);
		rHBoxes[3].getChildren().addAll(new Label("CONFIRM PASSWORD"), rConfirmPasswordField);
		rHBoxes[4].getChildren().addAll(okButton, cancelButton);

		Label registerLabel = new Label("REGISTER");
		registerLabel.setStyle("-fx-font-size: 40.0px;");

		rVBox.getChildren().add(registerLabel);
		rVBox.getChildren().addAll(rHBoxes);
		rVBox.getChildren().add(rMessageLabel);

		registerPane.getChildren().add(rVBox);

		okButton.setOnAction(e -> register(rNameField.getText(), rEmailField.getText(), rPasswordField.getText(), rConfirmPasswordField.getText()));
		cancelButton.setOnAction(e -> fadeTransition(loginPane));

		// Logged In Pane

		Label niLabel = new Label("NO OTHER FEATURES HAVE BEEN IMPLEMENTED.");

		loggedInPane.getChildren().addAll(niLabel, profilePane, menuPane, menuButton);

		loggedInPane.setAlignment(Pos.CENTER_LEFT);

		StackPane.setAlignment(menuButton, Pos.TOP_LEFT);
		StackPane.setAlignment(niLabel, Pos.CENTER);

		ImageView menuImage = new ImageView(MENU_BUTTON_IMAGE);
		menuImage.setFitWidth(20);
		menuImage.setFitHeight(20);
		menuButton.setGraphic(menuImage);

		menuButton.setShape(new Rectangle(20, 20));
		menuButton.setOnAction(e -> {
			viewMenu = !viewMenu;
			viewMenu(viewMenu);
		});

		// Menu Pane

		VBox mVBox = new VBox();
		mVBox.getChildren().addAll(profileImage, nameLabel, profileButton, logoutButton);

		nameLabel.setStyle("-fx-text-fill: black;");

		profileImage.setFitWidth(150);
		profileImage.setFitHeight(150);

		mVBox.setSpacing(20);
		mVBox.setAlignment(Pos.CENTER);

		menuPane.getChildren().add(mVBox);
		menuPane.setMaxWidth(200);

		profileButton.setOnAction(e -> {
			viewProfile = !viewProfile;
			viewProfile(viewProfile);
		});
		logoutButton.setOnAction(e -> {
			viewProfile(false);
			viewMenu(false);
			viewProfile = false;
			viewMenu = false;

			fadeTransition(loginPane);
		});

		// Profile Pane

		HBox pHBox = new HBox();
		pHBox.getChildren().addAll(profileImage0, infoLabel);

		pHBox.setSpacing(50);
		pHBox.setAlignment(Pos.CENTER);

		profilePane.getChildren().add(pHBox);

		scene.getStylesheets().add(Main.class.getResource("style.css").toString());

		stageHeight.bind(stage.heightProperty());
		stageWidth.bind(stage.widthProperty());

		stage.setScene(scene);
		stage.setTitle("JavaFX Login Form V3");
		stage.heightProperty().addListener(e -> {
			if (!viewProfile) profilePane.setTranslateY(-stageHeight.get());
		});
		stage.show();

		profilePane.setTranslateY(-stageHeight.get());
		menuPane.setTranslateX(stageWidth.subtract(stageWidth.add(menuPane.getMaxWidth())).get());

		intro();
	}

	private void intro() {
		FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), introPane), fadeOut = new FadeTransition(Duration.seconds(1.5), introPane);

		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.setCycleCount(1);

		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.setCycleCount(1);

		fadeIn.setOnFinished(e -> fadeOut.play());
		fadeOut.setOnFinished(e -> scene.setRoot(loginPane));

		fadeIn.play();

		currentPane = loginPane;
	}

	private void fadeTransition(StackPane nextPane) {
		setDisableAll(true);

		FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), nextPane), fadeOut = new FadeTransition(Duration.seconds(0.6), currentPane);

		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.setCycleCount(1);

		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.setCycleCount(1);

		fadeOut.setOnFinished(e -> {
			fadeIn.play();
			scene.setRoot(nextPane);
			currentPane = nextPane;
			messageLabel.setText("");
			rMessageLabel.setText("");
		});
		fadeIn.setOnFinished(e -> setDisableAll(false));

		fadeOut.play();
	}

	private void setDisableAll(boolean disable) {
		AnimatedButton[] buttons = { loginButton, registerButton, okButton, cancelButton, profileButton };

		for (AnimatedButton button : buttons) button.setDisable(disable);
	}

	private void viewProfile(boolean view) {
		Timeline timeline = new Timeline();
		KeyFrame frame = null;
		KeyValue value = null;

		if (view) value = new KeyValue(profilePane.translateYProperty(), 0, Interpolator.SPLINE(0, 0, 0, 1));
		else value = new KeyValue(profilePane.translateYProperty(), -stageHeight.get(), Interpolator.SPLINE(0, 0, 0, 1));

		frame = new KeyFrame(Duration.seconds(1), value);

		timeline.getKeyFrames().add(frame);
		timeline.play();
	}

	private void viewMenu(boolean view) {
		Timeline timeline = new Timeline();
		KeyFrame frame = null;
		KeyValue value = null;

		if (view) value = new KeyValue(menuPane.translateXProperty(), 0, Interpolator.SPLINE(0, 0, 0, 1));
		else value = new KeyValue(menuPane.translateXProperty(), stageWidth.subtract(stageWidth.add(menuPane.getMaxWidth())).get(),
			Interpolator.SPLINE(0, 0, 0, 1));

		frame = new KeyFrame(Duration.seconds(1), value);

		timeline.getKeyFrames().add(frame);
		timeline.play();
	}

	private void loadAccounts() {
		accounts = new ArrayList<>();

		try {
			FileReader reader = new FileReader(ACCOUNTS_FILE);
			BufferedReader br = new BufferedReader(reader);

			String line = "";

			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "|");
				Account account = new Account(st.nextToken(), st.nextToken(), st.nextToken());
				accounts.add(account);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void login(String email, String password) {
		if (email.length() == 0 && password.length() == 0) {
			loginMessage("PLEASE FILL IN ALL BLANKS", Color.RED);
			return;
		}

		boolean foundAccount = false;

		for (Account account : accounts) {
			if (account.getEmail().equals(email)) {
				foundAccount = true;

				if (account.getPassword().equals(password)) {
					loginMessage("LOGIN SUCCESS", Color.LIGHTGREEN);

					emailField.clear();
					passwordField.clear();

					fadeTransition(loggedInPane);

					nameLabel.setText(account.getName());

					infoLabel.setText("NAME: " + account.getName() + "\nEMAIL: " + account.getEmail() + "\nPASSWORD: ");
					for (int i = 0; i < account.getPassword().length(); i++) infoLabel.setText(infoLabel.getText() + "●");

					return;
				} else {
					loginMessage("PASSWORD IS INCORRECT", Color.RED);
					return;
				}
			}
		}

		if (!foundAccount) {
			loginMessage("THIS ACCOUNT DOES NOT EXIST", Color.RED);
			return;
		}
	}

	private void register(String name, String email, String password, String confirmPassword) {
		if (name.length() == 0 || email.length() == 0 && password.length() == 0 || confirmPassword.length() == 0) {
			loginMessage("PLEASE FILL IN ALL BLANKS", Color.RED);
			return;
		}

		// check entered information

		for (Account account : accounts) {
			if (account.getEmail().equals(email)) {
				registerMessage("EMAIL ALREADY EXISTS", Color.RED);
				return;
			}
		}

		if (!password.equals(confirmPassword)) {
			registerMessage("PASSWORDS DO NOT MATCH", Color.RED);
			return;
		}

		if (!Pattern.matches(EMAIL_REGEX, email)) {
			registerMessage("EMAIL FORMAT IS INCORRECT", Color.RED);
			return;
		}

		// make account and store to file

		Account newAccount = new Account(name, email, password);
		accounts.add(newAccount);
		writeAccountToFile(newAccount);
		registerMessage("REGISTRATION SUCCESS", Color.LIGHTGREEN);
		loadAccounts();

		rNameField.clear();
		rEmailField.clear();
		rPasswordField.clear();
		rConfirmPasswordField.clear();

		fadeTransition(loginPane);
	}

	private void registerMessage(String message, Color color) {
		rMessageLabel.setText(message);
		rMessageLabel.setTextFill(color);
	}

	private void loginMessage(String message, Color color) {
		messageLabel.setText(message);
		messageLabel.setTextFill(color);
	}

	private void writeAccountToFile(Account account) {
		String accountData = String.format(ACCOUNT_STORE_FORMAT, account.getName(), account.getEmail(), account.getPassword());

		try {
			FileWriter writer = new FileWriter(ACCOUNTS_FILE, true);
			writer.write(accountData + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
