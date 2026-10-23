import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

/**
 * Animated login page built with JavaFX.
 *
 * Animations included:
 *  - Floating, pulsing background circles
 *  - Card slide-up + fade-in on launch, with staggered child fade-ins
 *  - Button hover scale + press feedback
 *  - Input focus glow
 *  - "Shake" animation on failed login
 *  - Success: card shrinks/fades out and a welcome message fades in
 *
 * Demo credentials: admin / password123
 */
public class AnimatedLoginApp extends Application {

    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "password123";

    private static final double WIDTH = 900;
    private static final double HEIGHT = 600;

    private static final String FIELD_STYLE =
            "-fx-background-color: #f4f5fb; -fx-background-radius: 10;"
          + "-fx-border-color: transparent; -fx-border-radius: 10; -fx-border-width: 2;"
          + "-fx-padding: 12 14 12 14; -fx-font-size: 14px;";

    private static final String FIELD_FOCUSED_STYLE =
            "-fx-background-color: white; -fx-background-radius: 10;"
          + "-fx-border-color: #667eea; -fx-border-radius: 10; -fx-border-width: 2;"
          + "-fx-padding: 12 14 12 14; -fx-font-size: 14px;";

    private StackPane root;
    private VBox card;
    private Label messageLabel;
    private Button loginButton;
    private TextField usernameField;
    private PasswordField passwordField;

    @Override
    public void start(Stage stage) {
        root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        Pane background = buildAnimatedBackground();
        card = buildLoginCard();
        root.getChildren().addAll(background, card);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Animated Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        playEntranceAnimation();
    }

    // ------------------------------------------------------------------
    // UI construction
    // ------------------------------------------------------------------

    private Pane buildAnimatedBackground() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        Random rnd = new Random();

        for (int i = 0; i < 14; i++) {
            double radius = 20 + rnd.nextDouble() * 60;
            Circle c = new Circle(radius, Color.WHITE);
            c.setOpacity(0.05 + rnd.nextDouble() * 0.12);
            c.setCenterX(rnd.nextDouble() * WIDTH);
            c.setCenterY(rnd.nextDouble() * HEIGHT);
            pane.getChildren().add(c);

            // Drift around
            TranslateTransition drift = new TranslateTransition(
                    Duration.seconds(6 + rnd.nextDouble() * 6), c);
            drift.setByX(-60 + rnd.nextDouble() * 120);
            drift.setByY(-60 + rnd.nextDouble() * 120);
            drift.setAutoReverse(true);
            drift.setCycleCount(Animation.INDEFINITE);
            drift.setInterpolator(Interpolator.EASE_BOTH);

            // Gentle pulse
            ScaleTransition pulse = new ScaleTransition(
                    Duration.seconds(3 + rnd.nextDouble() * 3), c);
            pulse.setToX(1.25);
            pulse.setToY(1.25);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);

            drift.play();
            pulse.play();
        }
        return pane;
    }

    private VBox buildLoginCard() {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(36));
        box.setMaxSize(360, Region_USE_PREF());
        box.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 20;");
        box.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.35)));

        Label title = new Label("Welcome Back");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#2d2d50"));

        Label subtitle = new Label("Sign in to continue");
        subtitle.setTextFill(Color.GRAY);

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleField(usernameField);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        loginButton = new Button("LOGIN");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        loginButton.setTextFill(Color.WHITE);
        loginButton.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);"
                + "-fx-background-radius: 10; -fx-padding: 12 0 12 0; -fx-cursor: hand;");
        addButtonAnimations(loginButton);
        loginButton.setOnAction(e -> attemptLogin());
        passwordField.setOnAction(e -> attemptLogin());

        messageLabel = new Label(" ");
        messageLabel.setTextFill(Color.web("#e74c3c"));

        Label hint = new Label("Hint: admin / password123");
        hint.setTextFill(Color.LIGHTGRAY);
        hint.setFont(Font.font(11));

        box.getChildren().addAll(title, subtitle, usernameField, passwordField,
                loginButton, messageLabel, hint);
        return box;
    }

    // tiny helper so the card height is driven by its content
    private static double Region_USE_PREF() {
        return javafx.scene.layout.Region.USE_PREF_SIZE;
    }

    private void styleField(TextField field) {
        field.setStyle(FIELD_STYLE);
        field.setMaxWidth(Double.MAX_VALUE);

        field.focusedProperty().addListener((obs, was, isNow) -> {
            field.setStyle(isNow ? FIELD_FOCUSED_STYLE : FIELD_STYLE);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), field);
            st.setToX(isNow ? 1.03 : 1.0);
            st.setToY(isNow ? 1.03 : 1.0);
            st.play();
        });
    }

    private void addButtonAnimations(Button button) {
        button.setOnMouseEntered(e -> scaleTo(button, 1.05));
        button.setOnMouseExited(e -> scaleTo(button, 1.0));
        button.setOnMousePressed(e -> scaleTo(button, 0.96));
        button.setOnMouseReleased(e -> scaleTo(button, 1.05));
    }

    private void scaleTo(Node node, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), node);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    // ------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------

    private void playEntranceAnimation() {
        // Card slides up and fades in
        card.setOpacity(0);
        card.setTranslateY(70);

        FadeTransition fade = new FadeTransition(Duration.millis(700), card);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(700), card);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide).play();

        // Children fade in one after another
        int index = 0;
        for (Node child : card.getChildren()) {
            child.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(450), child);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(300 + index * 90L));
            ft.play();
            index++;
        }
    }

    private void playShakeAnimation() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), card);
        shake.setFromX(-12);
        shake.setToX(12);
        shake.setCycleCount(8);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> card.setTranslateX(0));
        shake.play();
    }

    private void playSuccessAnimation(String username) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setToValue(0);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(500), card);
        shrink.setToX(0.8);
        shrink.setToY(0.8);

        ParallelTransition out = new ParallelTransition(fade, shrink);
        out.setOnFinished(e -> {
            root.getChildren().remove(card);

            Label welcome = new Label("Welcome, " + username + "!");
            welcome.setFont(Font.font("System", FontWeight.BOLD, 40));
            welcome.setTextFill(Color.WHITE);
            welcome.setOpacity(0);
            welcome.setScaleX(0.6);
            welcome.setScaleY(0.6);
            root.getChildren().add(welcome);

            FadeTransition in = new FadeTransition(Duration.millis(700), welcome);
            in.setToValue(1);
            ScaleTransition grow = new ScaleTransition(Duration.millis(700), welcome);
            grow.setToX(1);
            grow.setToY(1);
            grow.setInterpolator(Interpolator.EASE_OUT);
            new ParallelTransition(in, grow).play();
        });
        out.play();
    }

    // ------------------------------------------------------------------
    // Login logic
    // ------------------------------------------------------------------

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();

        loginButton.setDisable(true);
        loginButton.setText("SIGNING IN...");
        messageLabel.setText(" ");

        // Simulate a short network delay
        PauseTransition delay = new PauseTransition(Duration.millis(900));
        delay.setOnFinished(e -> {
            if (VALID_USER.equals(user) && VALID_PASS.equals(pass)) {
                playSuccessAnimation(user);
            } else {
                messageLabel.setText("Invalid username or password");
                playShakeAnimation();
                loginButton.setDisable(false);
                loginButton.setText("LOGIN");
            }
        });
        delay.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
