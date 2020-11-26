package com.internshala.connectfourgame;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int columns = 7;
	private static final int rows = 6;
	private static final int circle_Diameter = 80;
	private static  final String discColor1 = "#24303E";
	private static final String discCoior2 = "#4CAA88";

	private static  String isPlayerOne = "Player One";
	private static  String isPlayerTwo = "Player two";

	private Disc[][] insertedDiscArray = new Disc[rows][columns];

	private  boolean isPlayerOneTurn = true;

	public void createPlayground(){
		Shape rectangleWithHoles = createGameStructureGrid();
		rootGridPane.add(rectangleWithHoles,0,1);
		List<Rectangle> rectangleList = createClickablecolumns();
		for(Rectangle rectangle: rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
	}
	private Shape createGameStructureGrid(){
		Shape rectangleWithHoles = new Rectangle((columns+1) * circle_Diameter,(rows+1) * circle_Diameter);
		for(int row = 0 ;row < rows ; row++){
			for(int col = 0; col< columns ; col++){

				Circle circle = new Circle();
				circle.setRadius(circle_Diameter/2);
				circle.setCenterX(circle_Diameter/2);
				circle.setCenterY(circle_Diameter/2);
				circle.setSmooth(true);

				circle.setTranslateX(col * ( circle_Diameter + 5) + circle_Diameter/4);
				circle.setTranslateY(row * (circle_Diameter + 5) +circle_Diameter/4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}
	private List<Rectangle> createClickablecolumns(){
		List<Rectangle> rectangleList = new ArrayList<>();

		for(int col= 0;col<columns;col++) {
			Rectangle rectangle = new Rectangle(circle_Diameter, (rows + 1) * circle_Diameter);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * ( circle_Diameter + 5) + circle_Diameter/4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;

	}
	private void insertDisc(Disc disc, int column){

		int row = rows -1 ;
		while(row >= 0){
			if(getDiscIfPresent(row,column) == null)
				break;
			row--;
		}if(row<0)
			return;
		insertedDiscArray[row][column] = disc;
		insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(column * ( circle_Diameter + 5) + circle_Diameter/4);
         int currentrow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * ( circle_Diameter + 5) + circle_Diameter/4);
		translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true;
			if(gameEnded(currentrow,column)){

              gameOver();
              return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? isPlayerOne : isPlayerTwo);
		});
		translateTransition.play();

	}

	private void gameOver() {
		String winner = isPlayerOneTurn? isPlayerOne : isPlayerTwo;
		System.out.println("the winner is: " + winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("connect four");
		alert.setContentText("The winner is " + winner);
		alert.setContentText("Want to play again");
		ButtonType yesBtn = new ButtonType("yes");
		ButtonType noBtn = new ButtonType("no, exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(() -> {
			Optional<ButtonType> clickedBtn = alert.showAndWait();
			if(clickedBtn.isPresent() && clickedBtn.get() == yesBtn){
				resetGame();
			}else{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
       insertedDiscPane.getChildren().clear();

       for(int row = 0; row < insertedDiscArray.length ;row ++){
       	for(int col= 0; col < insertedDiscArray[row].length;col++){
       		insertedDiscArray[row][col] = null;
        }
       }
       isPlayerOneTurn = true;
       playerNameLabel.setText(isPlayerOne);
       createPlayground();

	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3).
				mapToObj(r -> new Point2D(r,column))
				.collect(Collectors.toList());
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column +3)
		        .mapToObj(col -> new Point2D(row,col))
		        .collect(Collectors.toList());
        Point2D startPoint1 = new Point2D(row-3,column +3);
        List<Point2D> diagnol1Points = IntStream.rangeClosed(0,6).
		        mapToObj(i -> startPoint1.add(i,-i))
		        .collect(Collectors.toList());
		Point2D startPoint2 = new Point2D(row-3,column -3);
		List<Point2D> diagnol2Points = IntStream.rangeClosed(0,6).
				mapToObj(i -> startPoint2.add(i,-i))
				.collect(Collectors.toList());





         boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
		         ||checkCombinations(diagnol1Points) || checkCombinations(diagnol2Points);
       return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point: points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
			if (disc != null && disc.isPlayerOneTurn == isPlayerOneTurn) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else{
				chain = 0;
			}
		}
       return false;

	}
	private Disc getDiscIfPresent(int row,int column){

		if ( row >= rows || row< 0|| column >= columns || column < 0)
			return null;

			return insertedDiscArray[row][column];


	}

	private static class Disc extends Circle{
		private final boolean isPlayerOneTurn;

		public Disc(boolean isPlayerOneTurn){
			this.isPlayerOneTurn = isPlayerOneTurn;
			setRadius(circle_Diameter/2);
			setFill(isPlayerOneTurn? Color.valueOf(discColor1) : Color.valueOf(discCoior2));
			setCenterX(circle_Diameter/2);
			setCenterY(circle_Diameter/2);
		}


    }



	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label playerNameLabel;


	private boolean isAllowedToInsert = true;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
