package com.example.malotiprime.controller;

import com.example.malotiprime.model.DemoCatalog;
import com.example.malotiprime.model.DemoCredentials;
import com.example.malotiprime.model.Video;
import com.example.malotiprime.service.YouTubeEmbedServer;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MainController {
    private static final Video FEATURED_VIDEO = DemoCatalog.videos().getFirst();
    private static final String HERO_IMAGE =
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=2200&q=90";

    @FXML
    private StackPane appRoot;

    private final List<Video> videos = new ArrayList<>(DemoCatalog.videos());
    private final Set<String> myListIds = new LinkedHashSet<>();
    private final Set<String> recordedViews = new LinkedHashSet<>();
    private final Map<String, Label> navigationItems = new LinkedHashMap<>();
    private YouTubeEmbedServer embedServer;
    private String signedInEmail;
    private String activeNavigation = "home";
    private BorderPane shell;
    private VBox catalogPage;
    private StackPane playerFrame;
    private StackPane fullscreenContainer;
    private Label statusLabel;
    private WebView currentPlayer;
    private Stage fullscreenStage;
    private RotateTransition loadingAnimation;
    private Label playerToggle;
    private Label playerToggleIcon;
    private boolean playerPlaying;
    private boolean showingMyList;

    @FXML
    private void initialize() {
        shell = new BorderPane();
        shell.getStyleClass().add("app-shell");
        appRoot.getChildren().setAll(shell);
        try {
            embedServer = new YouTubeEmbedServer();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not start the local YouTube player.", exception);
        }
        showCatalog();
    }

    public void shutdown() {
        stopPlayer();
        if (embedServer != null) {
            embedServer.close();
            embedServer = null;
        }
    }

    private void showCatalog() {
        showingMyList = false;
        stopPlayer();
        shell.setTop(buildHeader());
        catalogPage = new VBox(28);
        catalogPage.getStyleClass().add("home-page");
        catalogPage.getChildren().add(buildHero());
        catalogPage.getChildren().add(buildGenreSection());
        catalogPage.getChildren().add(buildVideoSection());
        ScrollPane scrollPane = new ScrollPane(catalogPage);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        shell.setCenter(scrollPane);
        shell.setBottom(statusLabel);
    }

    private HBox buildHeader() {
        HBox header = new HBox(9);
        header.getStyleClass().add("top-bar");
        navigationItems.clear();

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream(
                "/com/example/malotiprime/view/maloti-mark.png")));
        logo.setFitWidth(40);
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label brand = new Label("Maloti");
        brand.getStyleClass().add("brand");
        Label prime = new Label("Prime");
        prime.getStyleClass().addAll("brand", "brand-prime");
        HBox wordmark = new HBox(5, brand, prime);
        wordmark.getStyleClass().add("wordmark");
        HBox brandLockup = new HBox(9, logo, wordmark);
        brandLockup.getStyleClass().add("brand-lockup");
        brandLockup.setAlignment(Pos.CENTER_LEFT);

        Label home = headerNavItem("home", "Home", "/home.png", this::showCatalog);
        Label movies = headerNavItem(
                "movies", "Movies", "/video-camera.png",
                () -> showScrollable(buildVideoSection()));
        Label series = headerNavItem(
                "series", "Series", "/view-list.png",
                () -> showScrollable(buildVideoSection()));
        Label genres = headerNavItem(
                "genres", "Genres", "/category.png",
                () -> showScrollable(buildGenrePage()));
        Label myList = headerNavItem("my-list", "My List", "/plus.png", this::showMyList);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField search = new TextField();
        search.setPromptText("Search movies...");
        search.setAccessibleText("Search videos");
        search.getStyleClass().add("header-search");
        search.setOnAction(event -> showSearchResults(search.getText()));

        Label account = iconAction(
                signedInEmail == null ? "Sign In" : "Account",
                "/add-people-interface-symbol-of-black-person-close-up-with-plus-sign-in-small-circle.png",
                signedInEmail == null ? this::showLogin : this::showAccount);
        account.getStyleClass().add("sign-in-action");

        header.getChildren().addAll(brandLockup, home, movies, series, genres, myList,
                spacer, search, account);
        updateActiveNavigation();
        header.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            boolean compact = newWidth.doubleValue() < 1180;
            movies.setVisible(!compact);
            movies.setManaged(!compact);
            series.setVisible(!compact);
            series.setManaged(!compact);
            search.setPrefWidth(newWidth.doubleValue() < 980 ? 105 : 190);
        });
        return header;
    }

    private Label headerNavItem(String key, String text, String iconResource, Runnable action) {
        Label item = iconAction(text, iconResource, () -> {
            activeNavigation = key;
            updateActiveNavigation();
            action.run();
        });
        item.getStyleClass().add("nav-item");
        navigationItems.put(key, item);
        return item;
    }

    private void updateActiveNavigation() {
        navigationItems.forEach((key, item) -> {
            if (key.equals(activeNavigation)) {
                if (!item.getStyleClass().contains("nav-item-active")) {
                    item.getStyleClass().add("nav-item-active");
                }
            } else {
                item.getStyleClass().remove("nav-item-active");
            }
        });
    }

    private StackPane buildHero() {
        StackPane hero = new StackPane();
        hero.getStyleClass().add("hero");
        ImageView background = new ImageView(new Image(HERO_IMAGE, true));
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(hero.widthProperty());
        background.fitHeightProperty().bind(hero.heightProperty());
        hero.getChildren().add(background);

        Region shade = new Region();
        shade.getStyleClass().add("hero-shade");
        hero.getChildren().add(shade);
        VBox copy = new VBox(13);
        copy.getStyleClass().add("hero-copy-block");
        StackPane.setAlignment(copy, Pos.CENTER_LEFT);
        StackPane.setMargin(copy, new javafx.geometry.Insets(35, 40, 35, 58));
        Label eyebrow = new Label("STREAM MORE. FEEL MORE.");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label("Stream More.");
        title.getStyleClass().add("hero-title");
        Label accentTitle = new Label("Feel More.");
        accentTitle.getStyleClass().add("hero-title-accent");
        Label description = new Label("Adventure, animation, drama and more — all in one place.\n"
                + "Your next favorite story is just a click away.");
        description.getStyleClass().add("hero-copy");
        HBox actions = new HBox(14);
        actions.setAlignment(Pos.CENTER_LEFT);
        Label watch = iconAction("Watch Now", "/play-button.png",
                () -> playVideo(FEATURED_VIDEO));
        watch.getStyleClass().add("primary-action");
        Label addToList = iconAction("Add to My List", "/plus.png",
                () -> toggleMyList(FEATURED_VIDEO));
        addToList.getStyleClass().add("hero-secondary-action");
        actions.getChildren().addAll(watch, addToList);
        copy.getChildren().addAll(eyebrow, title, accentTitle, description, actions);
        hero.getChildren().add(copy);
        return hero;
    }

    private VBox buildGenreSection() {
        FlowPane grid = new FlowPane(12, 12);
        grid.getStyleClass().add("genre-grid");
        for (String genre : DemoCatalog.genres()) {
            Video example = videos.stream()
                    .filter(video -> video.genre().equals(genre))
                    .findFirst()
                    .orElseThrow();
            grid.getChildren().add(genreCard(genre, example.thumbnailUrl()));
        }
        VBox section = new VBox(13, sectionHeading("Browse by Genre", "Explore all " + videos.size() + " trailers"), grid);
        section.getStyleClass().add("home-section");
        return section;
    }

    private StackPane genreCard(String genre, String imageUrl) {
        ImageView image = new ImageView(new Image(imageUrl, 220, 150, true, true, true));
        image.setFitWidth(220);
        image.setFitHeight(150);
        image.setPreserveRatio(false);
        image.setMouseTransparent(true);
        Region shade = new Region();
        shade.getStyleClass().add("genre-shade");
        Label label = new Label(genre);
        label.getStyleClass().add("genre-label");
        StackPane card = new StackPane(image, shade, label);
        card.setPrefSize(220, 150);
        card.getStyleClass().add("genre-card");
        card.setAccessibleRole(AccessibleRole.BUTTON);
        card.setAccessibleText("Browse " + genre);
        card.setFocusTraversable(true);
        card.setOnMouseClicked(event -> {
            showGenre(genre);
            event.consume();
        });
        card.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                showGenre(genre);
                event.consume();
            }
        });
        return card;
    }

    private VBox buildVideoSection() {
        VBox page = new VBox(24);
        page.getStyleClass().addAll("page-content", "home-section");
        page.getChildren().add(sectionHeading("Featured on Maloti", "Official YouTube trailers"));
        for (String genre : DemoCatalog.genres()) {
            List<Video> categoryVideos = videos.stream()
                    .filter(video -> video.genre().equals(genre))
                    .toList();
            page.getChildren().add(buildGenreRow(genre, categoryVideos));
        }
        return page;
    }

    private VBox buildGenreRow(String genre, List<Video> categoryVideos) {
        VBox section = new VBox(12);
        section.getStyleClass().add("video-shelf");
        section.getChildren().add(sectionHeading(genre, categoryVideos.size() + " trailers"));
        HBox cards = new HBox(16);
        cards.getChildren().addAll(categoryVideos.stream().map(this::videoCard).toList());
        ScrollPane horizontal = new ScrollPane(cards);
        horizontal.setFitToHeight(true);
        horizontal.setPannable(true);
        horizontal.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        horizontal.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        horizontal.getStyleClass().add("horizontal-scroll");
        horizontal.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (Math.abs(event.getDeltaY()) > Math.abs(event.getDeltaX())) {
                horizontal.setHvalue(horizontal.getHvalue() - event.getDeltaY() / 1200.0);
                event.consume();
            }
        });
        section.getChildren().add(horizontal);
        return section;
    }

    private VBox videoCard(Video video) {
        VBox card = new VBox(8);
        card.getStyleClass().add("video-card");
        card.setPrefWidth(220);
        card.setMinWidth(220);
        card.setMaxWidth(220);

        Label posterFallback = new Label("▶");
        posterFallback.getStyleClass().add("poster-fallback");
        Image posterImage = new Image(video.thumbnailUrl(), 220, 124, true, true, true);
        ImageView poster = new ImageView(posterImage);
        poster.setFitWidth(220);
        poster.setFitHeight(124);
        poster.setPreserveRatio(false);
        poster.setMouseTransparent(true);
        posterImage.errorProperty().addListener((observable, oldValue, failed) ->
                posterFallback.setVisible(failed));
        StackPane frame = new StackPane(posterFallback, poster);
        frame.getStyleClass().add("poster-frame");
        Label play = actionLabel("▶", new Label(), () -> playVideo(video));
        play.setAccessibleText("Play " + video.title());
        play.getStyleClass().add("play-overlay");
        frame.getChildren().add(play);

        Label title = new Label(video.title());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);
        title.setAccessibleRole(AccessibleRole.BUTTON);
        title.setFocusTraversable(true);
        title.setOnMouseClicked(event -> {
            playVideo(video);
            event.consume();
        });
        title.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                playVideo(video);
                event.consume();
            }
        });
        Label details = new Label(video.genre() + "  ·  " + video.views() + " views");
        details.getStyleClass().add("muted-text");
        Label save = myListAction(video);
        save.getStyleClass().add("save-action");

        card.getChildren().addAll(frame, title, details, save);
        card.setOnMouseClicked(event -> playVideo(video));
        return card;
    }

    private VBox buildGenrePage() {
        VBox page = new VBox(20);
        page.getStyleClass().add("page-content");
        page.getChildren().add(sectionHeading("Genres", "Find something for every mood"));
        FlowPane tiles = new FlowPane(12, 12);
        for (String genre : DemoCatalog.genres()) {
            Label tile = navItem(genre, () -> showGenre(genre));
            tile.getStyleClass().add("genre-tile");
            tiles.getChildren().add(tile);
        }
        page.getChildren().add(tiles);
        return page;
    }

    private void showGenre(String genre) {
        List<Video> selected = videos.stream()
                .filter(video -> genre.equalsIgnoreCase(video.genre()))
                .toList();
        VBox page = new VBox(18);
        page.getStyleClass().add("page-content");
        page.getChildren().addAll(
                sectionHeading(genre, selected.size() + " trailers"),
                buildGenreRow(genre, selected));
        showScrollable(page);
    }

    private void playVideo(Video video) {
        showingMyList = false;
        stopPlayer();
        BorderPane playerPage = new BorderPane();
        playerPage.getStyleClass().add("player-page");
        VBox details = new VBox(12);
        details.getStyleClass().add("player-details");
        Label back = navItem("‹  Back to browse", this::showCatalog);
        Label title = new Label(video.title());
        title.getStyleClass().add("section-title");
        Label description = new Label(video.description());
        description.getStyleClass().add("muted-text");

        playerToggleIcon = new Label("▶");
        playerToggle = actionLabel("Play trailer", playerToggleIcon, this::togglePlayerPlayback);
        playerToggle.getStyleClass().add("primary-action");
        Label fullscreen = actionLabel("⛶", new Label(), this::togglePlayerFullscreen);
        fullscreen.setAccessibleText("Toggle full-screen playback");
        fullscreen.setTooltip(new Tooltip("Full screen"));
        fullscreen.getStyleClass().add("secondary-action");
        Label save = myListAction(video);
        save.getStyleClass().add("secondary-action");
        HBox controls = new HBox(12, playerToggle, fullscreen, save);
        controls.setAlignment(Pos.CENTER_LEFT);
        Label qualityHint = new Label(
                "Use the YouTube settings gear for video quality options when available.");
        qualityHint.getStyleClass().add("muted-text");
        details.getChildren().addAll(back, title, description, controls);
        details.getChildren().add(qualityHint);

        playerFrame = new StackPane();
        playerFrame.getStyleClass().add("player-frame");
        WebView player = new WebView();
        player.setContextMenuEnabled(false);
        player.setMinHeight(300);
        player.setPrefHeight(540);
        player.setMaxHeight(Double.MAX_VALUE);
        currentPlayer = player;
        Label spinner = new Label("◌");
        spinner.getStyleClass().add("loading-spinner");
        Label loadingText = new Label("Loading trailer...");
        loadingText.getStyleClass().add("loading-copy");
        VBox loading = new VBox(10, spinner, loadingText);
        loading.setAlignment(Pos.CENTER);
        loading.getStyleClass().add("player-loading");
        playerFrame.getChildren().addAll(player, loading);
        playerPage.setCenter(playerFrame);
        playerPage.setBottom(details);
        shell.setCenter(playerPage);
        shell.setBottom(statusLabel);

        loadingAnimation = new RotateTransition(Duration.seconds(1.2), spinner);
        loadingAnimation.setByAngle(360);
        loadingAnimation.setCycleCount(RotateTransition.INDEFINITE);
        loadingAnimation.play();
        player.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, state) -> {
            if (state == Worker.State.SUCCEEDED) {
                PauseTransition delay = new PauseTransition(Duration.seconds(1.1));
                delay.setOnFinished(event -> {
                    if (currentPlayer == player) {
                        loading.setVisible(false);
                        if (loadingAnimation != null) {
                            loadingAnimation.stop();
                        }
                    }
                });
                delay.play();
            } else if (state == Worker.State.FAILED) {
                loadingText.setText("Could not load trailer. Check your internet connection.");
                spinner.setVisible(false);
                if (loadingAnimation != null) {
                    loadingAnimation.stop();
                }
            }
        });
        player.getEngine().load(embedServer.pageUri(youtubeId(video.youtubeUrl()), false).toString());
        recordView(video);
    }

    private void togglePlayerPlayback() {
        if (currentPlayer == null || playerToggle == null) {
            return;
        }
        boolean startPlayback = !playerPlaying;
        String command = startPlayback ? "playVideo" : "pauseVideo";
        try {
            currentPlayer.getEngine().executeScript("sendPlayerCommand('" + command + "')");
            playerPlaying = startPlayback;
            playerToggle.setText(startPlayback ? "Pause trailer" : "Play trailer");
            playerToggleIcon.setText(startPlayback ? "Ⅱ" : "▶");
        } catch (RuntimeException exception) {
            showStatus("The YouTube player is not ready yet. Try again in a moment.", true);
        }
    }

    private void togglePlayerFullscreen() {
        if (fullscreenStage != null) {
            exitPlayerFullscreen();
            return;
        }
        if (currentPlayer == null || playerFrame == null) {
            return;
        }

        fullscreenContainer = new StackPane();
        fullscreenContainer.getStyleClass().add("fullscreen-player");
        playerFrame.getChildren().remove(currentPlayer);
        Label exit = actionLabel("×", new Label(), this::exitPlayerFullscreen);
        exit.setAccessibleText("Exit full-screen playback");
        exit.setTooltip(new Tooltip("Exit full screen"));
        exit.getStyleClass().add("fullscreen-exit-action");
        fullscreenContainer.getChildren().addAll(currentPlayer, exit);
        StackPane.setAlignment(exit, Pos.TOP_RIGHT);

        fullscreenStage = new Stage(StageStyle.UNDECORATED);
        fullscreenStage.initOwner((Stage) appRoot.getScene().getWindow());
        Scene scene = new Scene(fullscreenContainer);
        scene.getStylesheets().setAll(appRoot.getScene().getStylesheets());
        fullscreenStage.setScene(scene);
        fullscreenStage.setFullScreenExitHint("Press Esc to exit full screen");
        StackPane.setMargin(exit, new Insets(18));
        Stage activeStage = fullscreenStage;
        fullscreenStage.fullScreenProperty().addListener((observable, wasFullScreen, isFullScreen) -> {
            if (!isFullScreen && fullscreenStage == activeStage) {
                exitPlayerFullscreen();
            }
        });
        fullscreenStage.show();
        fullscreenStage.setFullScreen(true);
    }

    private void exitPlayerFullscreen() {
        Stage stage = fullscreenStage;
        fullscreenStage = null;
        if (fullscreenContainer != null) {
            fullscreenContainer.getChildren().remove(currentPlayer);
            fullscreenContainer = null;
        }
        if (currentPlayer != null && playerFrame != null) {
            playerFrame.getChildren().add(0, currentPlayer);
        }
        if (stage != null) {
            stage.close();
        }
    }

    private void recordView(Video video) {
        if (!recordedViews.add(video.id())) {
            return;
        }
        for (int index = 0; index < videos.size(); index++) {
            Video existing = videos.get(index);
            if (existing.id().equals(video.id())) {
                videos.set(index, new Video(existing.id(), existing.title(), existing.description(),
                        existing.genre(), existing.youtubeUrl(), existing.thumbnailUrl(), existing.views() + 1));
                return;
            }
        }
    }

    private void stopPlayer() {
        if (fullscreenStage != null) {
            exitPlayerFullscreen();
        }
        if (loadingAnimation != null) {
            loadingAnimation.stop();
            loadingAnimation = null;
        }
        if (currentPlayer != null) {
            currentPlayer.getEngine().load("about:blank");
            currentPlayer = null;
        }
        playerFrame = null;
        playerToggle = null;
        playerToggleIcon = null;
        playerPlaying = false;
    }

    private void showSearchResults(String queryText) {
        String query = queryText == null ? "" : queryText.trim().toLowerCase();
        List<Video> matches = videos.stream()
                .filter(video -> query.isBlank()
                        || video.title().toLowerCase().contains(query)
                        || video.genre().toLowerCase().contains(query)
                        || video.description().toLowerCase().contains(query))
                .toList();
        VBox results = new VBox(18);
        results.getStyleClass().add("page-content");
        results.getChildren().add(sectionHeading(
                "Search results", query.isBlank() ? "All videos" : "Matches for: " + query));
        if (matches.isEmpty()) {
            results.getChildren().add(new Label("No matching trailers found."));
        } else {
            FlowPane cards = new FlowPane(16, 18);
            cards.getChildren().addAll(matches.stream().map(this::videoCard).toList());
            results.getChildren().add(cards);
        }
        dismissOverlay();
        showScrollable(results);
    }

    private void showLogin() {
        VBox form = new VBox(13);
        form.getStyleClass().add("login-card");
        Label title = new Label("Welcome to Maloti Prime");
        title.getStyleClass().add("login-title");
        Label note = new Label("Use the demo account to sign in.");
        note.getStyleClass().add("login-subtitle");
        Label emailLabel = new Label("Email address");
        emailLabel.getStyleClass().add("field-label");
        TextField email = new TextField(DemoCredentials.EMAIL);
        email.setAccessibleText("Email address");
        emailLabel.setLabelFor(email);
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");
        PasswordField password = new PasswordField();
        password.setPromptText("Enter the demo password");
        password.setAccessibleText("Password");
        passwordLabel.setLabelFor(password);
        Label hint = new Label("Demo login: user1@gmail.com  /  123admin");
        hint.getStyleClass().add("muted-text");
        Label feedback = new Label();
        feedback.getStyleClass().add("login-feedback");
        Label signIn = iconAction("Sign in", "/lock.png",
                () -> authenticate(email.getText(), password.getText(), feedback));
        signIn.getStyleClass().add("primary-action");
        signIn.setMaxWidth(Double.MAX_VALUE);
        Label close = navItem("Continue as guest", this::dismissOverlay);
        close.getStyleClass().add("form-link");
        email.setOnAction(event -> password.requestFocus());
        password.setOnAction(event -> authenticate(email.getText(), password.getText(), feedback));
        form.getChildren().addAll(title, note, emailLabel, email, passwordLabel, password,
                hint, signIn, feedback, close);
        showOverlay(form, "");
        Platform.runLater(email::requestFocus);
    }

    private void authenticate(String email, String password, Label feedback) {
        if (!DemoCredentials.authenticate(email, password)) {
            feedback.setText("Email or password is incorrect. Use the demo credentials shown above.");
            feedback.getStyleClass().remove("login-feedback-ok");
            feedback.getStyleClass().add("login-feedback-error");
            return;
        }
        signedInEmail = DemoCredentials.EMAIL;
        dismissOverlay();
        shell.setTop(buildHeader());
        showStatus("Signed in as " + signedInEmail + ".", false);
    }

    private void showAccount() {
        VBox content = new VBox(14);
        content.getStyleClass().add("login-card");
        content.getChildren().addAll(
                new Label("Signed in as " + signedInEmail),
                navItem("Sign out", this::signOut),
                navItem("Close", this::dismissOverlay));
        showOverlay(content, "Account");
    }

    private void signOut() {
        signedInEmail = null;
        dismissOverlay();
        shell.setTop(buildHeader());
    }

    private void showMyList() {
        List<Video> saved = videos.stream()
                .filter(video -> myListIds.contains(video.id()))
                .toList();
        VBox page = new VBox(18);
        page.getStyleClass().add("page-content");
        page.getChildren().add(sectionHeading("My List", saved.size() + " saved trailers"));
        if (saved.isEmpty()) {
            page.getChildren().add(new Label("Your list is empty. Add trailers with “Add to My List”."));
        } else {
            FlowPane cards = new FlowPane(16, 18);
            cards.getChildren().addAll(saved.stream().map(this::videoCard).toList());
            page.getChildren().add(cards);
        }
        showScrollable(page);
        showingMyList = true;
    }

    private void toggleMyList(Video video) {
        boolean removed = !myListIds.add(video.id());
        if (removed) {
            myListIds.remove(video.id());
        }
        showStatus(removed
                ? "Removed “" + video.title() + "” from My List."
                : "Added “" + video.title() + "” to My List.", false);
        if (showingMyList) {
            showMyList();
        }
    }

    private Label myListAction(Video video) {
        boolean saved = myListIds.contains(video.id());
        Label icon = new Label(saved ? "✓" : "+");
        Label action = new Label(saved ? "Saved to My List" : "Add to My List");
        action.setGraphic(icon);
        action.setGraphicTextGap(8);
        action.getStyleClass().add("icon-action");
        setAccessibleAction(action, () -> {
            toggleMyList(video);
            boolean nowSaved = myListIds.contains(video.id());
            action.setText(nowSaved ? "Saved to My List" : "Add to My List");
            icon.setText(nowSaved ? "✓" : "+");
        });
        return action;
    }

    private void showOverlay(Node content, String title) {
        dismissOverlay();
        VBox card = new VBox(14);
        card.getStyleClass().add("dialog-card");
        if (!title.isBlank()) {
            Label heading = new Label(title);
            heading.getStyleClass().add("section-title");
            card.getChildren().add(heading);
        }
        card.getChildren().add(content);
        StackPane overlay = new StackPane(card);
        overlay.getStyleClass().add("overlay");
        overlay.setFocusTraversable(true);
        overlay.setOnMouseClicked(event -> {
            if (event.getTarget() == overlay) {
                dismissOverlay();
                event.consume();
            }
        });
        overlay.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                dismissOverlay();
                event.consume();
            }
        });
        appRoot.getChildren().add(overlay);
    }

    private void showScrollable(Node content) {
        showingMyList = false;
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        shell.setCenter(scrollPane);
    }

    private void dismissOverlay() {
        if (appRoot.getChildren().size() > 1) {
            appRoot.getChildren().remove(appRoot.getChildren().size() - 1);
        }
    }

    private void showStatus(String message, boolean error) {
        if (statusLabel == null) {
            statusLabel = new Label();
        }
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-error", "status-ok");
        statusLabel.getStyleClass().add(error ? "status-error" : "status-ok");
        shell.setBottom(statusLabel);
    }

    private VBox sectionHeading(String title, String subtitle) {
        VBox heading = new VBox(3);
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Region accent = new Region();
        accent.getStyleClass().add("section-accent");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("muted-text");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleRow.getChildren().addAll(accent, titleLabel, spacer, subtitleLabel);
        heading.getChildren().add(titleRow);
        return heading;
    }

    private Label navItem(String text, Runnable action) {
        Label item = new Label(text);
        item.getStyleClass().add("nav-item");
        setAccessibleAction(item, action);
        return item;
    }

    private Label iconAction(String text, String iconResource, Runnable action) {
        Image image;
        try (var stream = getClass().getResourceAsStream(iconResource)) {
            if (stream == null) {
                throw new IllegalStateException("Missing bundled icon: " + iconResource);
            }
            image = new Image(stream);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not load bundled icon: " + iconResource, exception);
        }
        ImageView icon = new ImageView(image);
        icon.setFitWidth(19);
        icon.setFitHeight(19);
        ColorAdjust contrast = new ColorAdjust();
        contrast.setSaturation(-1);
        contrast.setBrightness(0.75);
        icon.setEffect(contrast);
        icon.getStyleClass().add("action-icon");
        return actionLabel(text, icon, action);
    }

    private Label actionLabel(String text, Node graphic, Runnable action) {
        Label item = new Label(text);
        item.setGraphic(graphic);
        item.setGraphicTextGap(9);
        item.getStyleClass().add("icon-action");
        setAccessibleAction(item, action);
        return item;
    }

    private static void setAccessibleAction(Label item, Runnable action) {
        item.setAccessibleRole(AccessibleRole.BUTTON);
        item.setFocusTraversable(true);
        item.setOnMouseClicked(event -> {
            action.run();
            event.consume();
        });
        item.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                action.run();
                event.consume();
            }
        });
    }

    private static String youtubeId(String url) {
        URI uri = URI.create(url);
        String host = uri.getHost();
        if (host != null && host.endsWith("youtu.be")) {
            return uri.getPath().substring(1);
        }
        String query = uri.getRawQuery();
        if (query != null) {
            for (String part : query.split("&")) {
                if (part.startsWith("v=")) {
                    return part.substring(2);
                }
            }
        }
        throw new IllegalArgumentException("Invalid YouTube video URL: " + url);
    }

}
