package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
	static final int choiceAmount = 4;
	File quesAndAnswers = new File("allStuff.txt");
	public static Scanner in;
	public static ArrayList<String> questions = new ArrayList<>();
	public static ArrayList<String[]> options = new ArrayList<>();
	public static ArrayList<String> answers = new ArrayList<>();
	static Scene scene;

	public static StackPane sp = new StackPane();
	static int correctCount = 0;
	static int currentQuestionIndex = 0;
	static Label correctCountLabel = new Label();
	static boolean answered = false;
	static boolean gottenWrong = false;
	static boolean ffLineUsed = false;
	static boolean ddLineUsed = false;

	@Override
	public void start(Stage primaryStage) throws Exception {
		in = new Scanner(quesAndAnswers); // scan the file with this
		scene = new Scene(sp, 800, 800);
		scene.getStylesheets().add("/style.css");
		primaryStage.setScene(scene);
		primaryStage.setTitle("Test");
		primaryStage.show();
		ArrayList<String> temp = inputLines();
		distributeLines(temp);
		StackPane initial = new StackPane();
		sp.getChildren().add(initial);
		Button startGame = new Button("Goodluck!");
		initial.getChildren().add(startGame);
		correctCountLabel.setStyle("-fx-background-color: mistyrose; "
				+ "-fx-font-size: 15; -fx-text-fill: black; -fx-font-weight: bolder;");
		startGame.setOnAction(e->{
			quizTime();
			sp.getChildren().remove(initial); 
		});
		
		// styling the start game button
		startGame.setStyle("-fx-border-width: 4; -fx-border-color: darkorchid; "
				+ "-fx-pref-width: 500; -fx-pref-height: 200; -fx-background-color: mediumpurple;"
				+ "-fx-font-size: 20; -fx-text-fill: black; -fx-font-weight: bolder;");
	}	
	
	ArrayList<String> inputLines() {
		ArrayList<String> input = new ArrayList<>();
		while(in.hasNext()) {
			input.add(in.nextLine()); // this should be taking everything it sees until a \n appears
		}
		return input;
	}
	void distributeLines(ArrayList<String> temp) {
		/*we could use a for loop maybe the text file ->
		 * line by line into a string array
		 * its length is gonna be the indicator to terminate*/
		int optCount = 0;
		String str = "";
		String[] opt = new String[choiceAmount];
		for(int i = 0; i < temp.size(); i++) {
			str = temp.get(i);
// debug:	System.out.println(str);
			if(str.trim().isEmpty() || str.length() < 2) {
				continue;
			}
			if(str.charAt(1) == '.') { // it means it is a question
				questions.add(str);
			}
			else { // it is an option
				opt[optCount++] = str;
				if (optCount == choiceAmount) {
					options.add(opt);
					optCount = 0;
					opt = new String[choiceAmount];
					// now get done with the answer of this question as there are 4 options already
					answers.add(temp.get(++i));
				}
			}
		}
//		if(options.size() == questions.size() && options.size() == answers.size())
//			System.out.println("It was successful, congrats");
		
	}
	/*this method is supposed to handle the bringing new questions to the screen task*/
	void quizTime() {
		Rectangle gui = new Rectangle();
		gui.setFill(Color.DARKSEAGREEN);
		gui.setOpacity(0.5);
		gui.setWidth(750);
		gui.setHeight(750);
		gui.setStyle("-fx-stroke: darkslategrey; -fx-stroke-width: 5;");
		sp.getChildren().clear();
		
		// button for next question
		VBox userControl = new VBox();
		userControl.setSpacing(10);
		userControl.setTranslateY(300);
		Button nextQ = new Button("Next Question");
		nextQ.setStyle("-fx-background-color: orange; -fx-font-size: 20;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		nextQ.setPrefHeight(40); nextQ.setPrefWidth(200);
		nextQ.setOnAction(e ->{
			currentQuestionIndex = currentQuestionIndex < questions.size() - 1?
					currentQuestionIndex + 1 : 1000;
			if (currentQuestionIndex == 1000) {
				sp.getChildren().clear();
//				System.out.println("You have completed the test");
				Label score = new Label("Your score is:\n" + correctCount + "/" + questions.size());
				score.setStyle("-fx-text-fill: purple; -fx-font-size: 40; -fx-font-weight: bolder;");
				if(correctCount >= 5)
					sp.setId("successBg");
				else
					sp.setId("failureBg");
				sp.getChildren().add(score);
			}
			gottenWrong = false;
			answered = false;
			if (currentQuestionIndex < 1000)
				quizTime();
		});
		userControl.getChildren().add(nextQ);
		sp.getChildren().add(userControl);
		sp.getChildren().add(gui);
		Button choices[] = manageButtons(gui);
		
		// button for 50-50 life-line
		Button ffLine = new Button("50/50 \u2702");
		userControl.getChildren().add(ffLine);
		ffLine.setStyle("-fx-background-color: darkturquoise; -fx-font-size: 20;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		if(ffLineUsed) ffLine.setStyle("-fx-background-color: grey; -fx-font-size: 20;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		ffLine.setPrefHeight(40); ffLine.setPrefWidth(200);
		ffLine.setOnAction(e->{
			fiftyFifty(choices);
		});
		
		// button for Double Dip life-line
		Button ddLine = new Button("Double dip (çift cevap)");
		userControl.getChildren().add(ddLine);
		ddLine.setStyle("-fx-background-color: darkturquoise; -fx-font-size: 12;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		if(ddLineUsed) ddLine.setStyle("-fx-background-color: grey; -fx-font-size: 12;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		ddLine.setPrefHeight(40); ddLine.setPrefWidth(200);
		ddLine.setOnAction(e->{
			doubleDip(choices);
		});
		
		// toggle group for background mode
		RadioButton darkMode = new RadioButton("Save those eyes of yours?(dark mode)");
		RadioButton lightMode = new RadioButton("Nope, light-mode it is!");
		ToggleGroup group = new ToggleGroup();
		darkMode.setToggleGroup(group);
		darkMode.setStyle("-fx-background-color: lightblue; -fx-font-size: 12;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		lightMode.setToggleGroup(group);
		lightMode.setStyle("-fx-background-color: lightblue; -fx-font-size: 12;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;");
		darkMode.setOnAction(e->{
			scene.getRoot().setStyle("-fx-background-color: #333333;");
		});
		lightMode.setOnAction(e->{
			scene.getRoot().setStyle("-fx-background-color: #F0F0F0;");
		});
		userControl.getChildren().addAll(darkMode, lightMode, correctCountLabel);
		
		// specified region for the question
		Label question = new Label(currentQuestionIndex + 1 + questions.get(currentQuestionIndex).trim());
		question.setMaxWidth(600);
		question.setMinHeight(400);
		question.setWrapText(true);
		question.setStyle("-fx-font-size: 20; -fx-text-fill: black; -fx-font-weight: bolder;");
		question.setTranslateY(-gui.getLayoutBounds().getHeight()/2 + 140);
		sp.getChildren().add(question);
		choices[0].setOnAction(e-> {
//			System.out.println("button1");
			if(choices[0].getText().equals(answers.get(currentQuestionIndex))) {
				rightAnswer(choices[0]);
			}
			else
				wrongAnswer(choices[0]);
		});
		choices[1].setOnAction(e-> {
//			System.out.println("button2");
			if(choices[1].getText().equals(answers.get(currentQuestionIndex))) {
				rightAnswer(choices[1]);
			}
			else
				wrongAnswer(choices[1]);
		});
		choices[2].setOnAction(e-> {
//			System.out.println("button3");
			if(choices[2].getText().equals(answers.get(currentQuestionIndex))) {
				rightAnswer(choices[2]);
			}
			else
				wrongAnswer(choices[2]);
		});
		choices[3].setOnAction(e-> {
//			System.out.println("button4");
			if(choices[3].getText().equals(answers.get(currentQuestionIndex))) {
				rightAnswer(choices[3]);
			}
			else
				wrongAnswer(choices[3]);
		});
	}
	
	Button[] manageButtons(Rectangle base){
		FlowPane fp = new FlowPane(125, 50);
		fp.setPadding(new Insets(15, 15, 15, 15));
		fp.setMaxWidth(800);
		Button option1 = new Button(options.get(currentQuestionIndex)[0]);
		Button option2 = new Button(options.get(currentQuestionIndex)[1]);
		Button option3 = new Button(options.get(currentQuestionIndex)[2]);
		Button option4 = new Button(options.get(currentQuestionIndex)[3]);
		
		Button buttons[] = {option1, option2, option3, option4};
		for(Button b: buttons) {
			b.setPrefWidth(300); b.setPrefHeight(150);
			b.setStyle("-fx-background-color: #91abd9; -fx-font-size: 15;"
					+ " -fx-text-fill: black; -fx-font-weight: bolder;"
					+ " -fx-border-width: 2; -fx-border-color: darkslateblue;");
			b.setWrapText(true);
		}

		fp.getChildren().addAll(option1, option2, option3, option4);
		fp.setTranslateY(base.getTranslateY() + base.getHeight()/2);
		fp.translateXProperty().bind(base.translateXProperty().add(20));
		sp.getChildren().add(fp);
		
		return buttons;
	}
	
	void rightAnswer(Button rightChoice) {
		if(rightChoice.getStyle().contains("-fx-background-color: #288f66;") || gottenWrong) {
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nNo cheating!");
		}
		else {
			correctCount++;
			answered = true;
			correctCountLabel.setText("Correct answers: " + correctCount + "/" + questions.size());
		}
		rightChoice.setStyle("-fx-background-color: #288f66; -fx-font-size: 15;"
				+ "	-fx-text-fill: black; -fx-font-weight: bolder;"
				+ " -fx-border-width: 2; -fx-border-color: darkgreen;");
// debug:		System.out.println("RIGHT");
	}
	
	void wrongAnswer(Button wrongChoice) {
		wrongChoice.setStyle("-fx-background-color: #b32d62; -fx-font-size: 15;"
				+ " -fx-text-fill: black; -fx-font-weight: bolder;"
				+ " -fx-border-width: 2; -fx-border-color: darkred;");
// debug:		System.out.println("WRONG");
		gottenWrong = true;
		answered = true;
	}
	
	void doubleDip(Button[] choices) {
		if(ddLineUsed) {
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nYou already used Double-Dip!");
			return;
		}
		if(answered && gottenWrong && noGreen(choices)) {
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nDouble Dip active!");
			ddLineUsed = true;
			answered = false; // just for once we will pretend that didn't happen
			gottenWrong = false; // just for once we will pretend that didn't happen
		}
		else if (answered){
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nYou are done with this question though?");
		}
		else {
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nMake a guess first, then you can use it.");
		}
	}

	boolean noGreen(Button[] choices) {
		for(Button b : choices) {
			if(b.getStyle().contains("-fx-background-color: #288f66;")) {
// debug:				System.out.println("THERE IS GREEN THERE IS GREEN");
				return false; // there is green
			}
		}
		return true; // there is indeed no green
	}
	
	boolean noRed(Button[] choices) {
		for(Button b : choices) {
			if(b.getStyle().contains("-fx-background-color: #b32d62;")) {
// debug:				System.out.println("THERE IS RED THERE IS RED");
				return false; // there is at least one red
			}
		}
		return true; // there is indeed no red
	}

	void fiftyFifty(Button[] choices){
		if(ffLineUsed) {
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nYou already used 50-50 and you know it!");
			return;
		}
		else if(answered) {
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nYou already answered this question... I'll let it pass.");
			return;
		}
		else if(!noRed(choices)) { // if no red is false then there is red
			correctCountLabel.setText("Correct answers: " + correctCount + 
					"/" + questions.size() + "\nThat's cheating! 50-50 should be used before DD");
			return;
		}
		// if it is a valid usage, to prevent it from being used repeatedly
		ffLineUsed = true;
		correctCountLabel.setText("Correct answers: " + correctCount + 
				"/" + questions.size() + "\nFifty-fifty activated!");
		int wrongCount = 0; // when == 2 leave the method
		
		// FIXME this can be much more optimized tbh (shuffle then for-each)
		// since the user will ever use it once, not the highest priority.
		Button temp[] = {choices[3], choices[0], choices[1], choices[2]};
		for(Button b : temp) {
			if(!b.getText().equals(answers.get(currentQuestionIndex))) {
				wrongCount++;
				b.setStyle("-fx-background-color: #b32d62; -fx-font-size: 15;"
						+ " -fx-text-fill: black; -fx-font-weight: bolder;"
						+ " -fx-border-width: 2; -fx-border-color: darkred;");
				System.out.println("choice eliminated from view");
				if(wrongCount == 2) break;
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
