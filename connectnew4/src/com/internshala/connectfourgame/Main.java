package com.internshala.connectfourgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createmenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

	    Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
	    menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }
    private MenuBar createmenu(){
	    Menu fileMenu = new Menu("file");
	    MenuItem newMenuItem = new MenuItem("New Game");
	    newMenuItem.setOnAction(event -> controller.resetGame());

	    MenuItem resetMenuItem = new MenuItem("Reset Game");

	    resetMenuItem.setOnAction(event -> controller.resetGame());
	    SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
	    MenuItem exitMenuItem = new MenuItem("Exit Game");
	    exitMenuItem.setOnAction(event -> exitGame());
	    fileMenu.getItems().setAll(newMenuItem, resetMenuItem, separatorMenuItem, exitMenuItem);

	    Menu helpMenu = new Menu("help");
	    MenuItem aboutGame = new MenuItem("About Connect4");
	    aboutGame.setOnAction(event -> aboutConnect4());

	    SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
	    MenuItem aboutMe = new MenuItem("About Me");
	    aboutMe.setOnAction(event -> aboutme());

	    helpMenu.getItems().addAll(aboutGame, aboutMe);

	    MenuBar menuBar = new MenuBar();
	    menuBar.getMenus().addAll(fileMenu, helpMenu);
	    return menuBar;


    }

	private void aboutme() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Devloper");
		alert.setHeaderText("Nishant kumar");
		alert.setContentText("work Hard and don't waste time");
		alert.show();


	}

	private void aboutConnect4() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("how to Play?");
		alert.setContentText("Connect Four is a two-player connection game" +
				" in which the players first choose a color and " +
				"then take turns dropping colored discs from the top into a seven-column," +
				" six-row vertically suspended grid. The pieces fall straight down," +
				" occupying the next available space within the column." +
				" The objective of the game is to be the first to form a horizontal, vertical, or diagonal line" +
				" of four of one's own discs. Connect Four is a solved game. " +
				"The first player can always win by playing the right moves.");
     alert.show();

	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);

	}

	private void resetGame() {



	}


	public static void main(String[] args) {
        launch(args);
    }
}
