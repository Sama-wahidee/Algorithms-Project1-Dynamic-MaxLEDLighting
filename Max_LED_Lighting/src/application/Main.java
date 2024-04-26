package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Main extends Application {
	LED[] LedsOrder;
	PowerSource[] sourcesOrder;
	int[][] sol;
	String[][] signs;
	private static final byte on = 1;
	private static final String DIAG = "↖";
	private static final String UP = "↑";
	private static final String LEFT = "←";
	private static final double ELEMENT_SIZE = 20;
	Stage DPtable = new Stage();
	String[][] marged;
	StringBuilder result = new StringBuilder();
	int[] LEDS;
	Stage primaryStage = new Stage();
	int count = 0;

	public void start(Stage primaryStage) throws FileNotFoundException {
		start();

	}

	public void start() throws FileNotFoundException {
		BorderPane root = new BorderPane();
		VBox bVB = new VBox();
		Font font = new Font(20);
		Button fileBT = new Button("Upload from file");
		fileBT.setFont(font);
		fileBT.setStyle(
				"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
		fileBT.setOnAction(e -> {
			boolean flag = false;
			while (flag == false) {
				if (openFileChooser() == true) {
					flag = true;
					dialog(AlertType.INFORMATION, "Data uploaded successfully");
				}
			}
			findLCSWithDirection(LedsOrder);
			margeTabels();
			primaryStage.close();
			secoundStage();
		});
		Button manuallyBT = new Button("Manually");
		manuallyBT.setFont(font);
		manuallyBT.setStyle(
				"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
		//action to handil if the user choose to enter the order for the leds by his hand 
		manuallyBT.setOnAction(e -> {
			Stage s = new Stage();
			primaryStage.close();
			VBox VB = new VBox();
			VB.setSpacing(8);
			VB.setStyle("-fx-background-color: #000000;");
			VB.setAlignment(Pos.CENTER);
			Label ledsNumL = new Label("Please enter the number of leds..");
			ledsNumL.setFont(font);
			ledsNumL.setStyle("-fx-text-fill: #FF6700");
			Label ledsOrderL = new Label("Please enter the leds order..");
			ledsOrderL.setFont(font);
			ledsOrderL.setStyle("-fx-text-fill: #FF6700;");
			TextField ledsNumTF = createPositiveInteger();
			ledsNumTF.setStyle("-fx-text-fill: #ffffff;-fx-control-inner-background: #000000;");
			TextField ledsOrderTF = createPositiveIntegerWithSpaceField();
			ledsOrderTF.setStyle("-fx-text-fill: #ffffff;-fx-control-inner-background: #000000;");
			Button uploadBT = new Button("Upload");
			uploadBT.setFont(font);
			uploadBT.setStyle(
					"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
			//upload the data from the text feild and cheek if its vaild to build h db table and find the maximum number of lighted leds
			uploadBT.setOnAction(m -> {
				s.close();
				int ledsNum = Integer.parseInt(ledsNumTF.getText());
				sourcesOrder = new PowerSource[ledsNum];
				for (int i = 1; i <= ledsNum; i++) {
					PowerSource ps = new PowerSource(i);
					sourcesOrder[i - 1] = ps;
				}
				String[] temp = ledsOrderTF.getText().split("\\s+");
				LedsOrder = new LED[temp.length];
				// Convert each substring to an integer and store in the array
				for (int i = 0; i < temp.length; i++) {
					LED L = new LED(Integer.parseInt(temp[i]));
					LedsOrder[i] = L;
				}
				if (isValid(LedsOrder) != true) {
					ledsOrderTF.setText("");
				} else {
					findLCSWithDirection(LedsOrder);
					margeTabels();
					primaryStage.close();
					secoundStage();
				}
			});
			Button startOver = new Button("Start over");
			startOver.setFont(new Font(20));
			startOver.setStyle(
					"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
			startOver.setOnAction(d -> {
				result.setLength(0);
				s.close();
				primaryStage.show();
			});
			VB.getChildren().addAll(ledsNumL, ledsNumTF, ledsOrderL, ledsOrderTF, uploadBT, startOver);
			Scene scene = new Scene(VB, 400, 300);
			s.setScene(scene);
			s.setTitle("Max LED Lighting");
			s.show();
		});
		//action to handil if the user choose to enter a random order for the leds 
		Button randomlyBT = new Button("Randomly");
		randomlyBT.setFont(font);
		randomlyBT.setStyle(
				"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
		randomlyBT.setOnAction(e -> {
			Stage s = new Stage();
			primaryStage.close();
			VBox VB = new VBox();
			VB.setSpacing(10);
			VB.setStyle("-fx-background-color: #000000;");
			VB.setAlignment(Pos.CENTER);
			Label ledsNumL = new Label("Please enter the number of leds..");
			ledsNumL.setFont(font);
			ledsNumL.setStyle("-fx-text-fill: #FF6700");
			TextField ledsNumTF = createPositiveInteger();
			ledsNumTF.setStyle("-fx-text-fill: #ffffff;-fx-control-inner-background: #000000;");
			Button randomBT = new Button("Upload");
			randomBT.setFont(font);
			randomBT.setStyle(
					"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
			randomBT.setOnAction(m -> {
				s.close();
				int ledsNum = Integer.parseInt(ledsNumTF.getText());
				LedsOrder = new LED[ledsNum];
				sourcesOrder = new PowerSource[ledsNum];

				// Initialize LED array and sourcesOrder
				for (int i = 0; i < ledsNum; i++) {
					LED L = new LED(i + 1);
					LedsOrder[i] = L;
					PowerSource ps = new PowerSource(i + 1);
					sourcesOrder[i] = ps;
				}

				// Shuffle the array using Fisher-Yates algorithm
				Random rand = new Random();
				for (int i = ledsNum - 1; i > 0; i--) {
					int j = rand.nextInt(i + 1);
					// Swap array[i] and array[j]
					LED temp = LedsOrder[i];
					LedsOrder[i] = LedsOrder[j];
					LedsOrder[j] = temp;
				}

				for (int i = 1; i <= ledsNum; i++) {
					PowerSource ps = new PowerSource(i);
					sourcesOrder[i - 1] = ps;
				}
				findLCSWithDirection(LedsOrder);
				margeTabels();
				primaryStage.close();
				secoundStage();
			});

			Scene scene = new Scene(VB, 400, 200);
			Button startOver = new Button("Start over");
			startOver.setFont(new Font(20));
			startOver.setStyle(
					"-fx-background-color:#0000 ; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill: #FF6700");
			startOver.setOnAction(d -> {
				result.setLength(0);
				s.close();
				primaryStage.show();
			});
			VB.getChildren().addAll(ledsNumL, ledsNumTF, randomBT, startOver);

			s.setScene(scene);
			s.setTitle("Max LED Lighting");
			s.show();

		});
		Label quastionL = new Label("Where is your data ?");
		quastionL.setFont(font);
		quastionL.setStyle("-fx-text-fill: #FF6700");
		Label or1L = new Label("Or");
		or1L.setFont(font);
		or1L.setStyle("-fx-text-fill: #FF6700");
		Label or2L = new Label("Or");
		or2L.setStyle("-fx-text-fill: #FF6700");
		or2L.setFont(font);
		Label space = new Label("\n\n\n\n\n");
		bVB.getChildren().addAll(quastionL, fileBT, or1L, manuallyBT, or2L, randomlyBT, space);
		bVB.setAlignment(Pos.CENTER);
		bVB.setSpacing(8);
		root.setBottom(bVB);
		Scene scene = new Scene(root, 394, 700);
		backGround(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Max LED Lighting");
		primaryStage.show();
	}
//*****************************************************seacoundstage**********************************************
	public void secoundStage() {
		Stage tabsS = new Stage();
		TabPane tabsP = new TabPane();
		Tab DPt = new Tab("DP table");
		DPt.setStyle("-fx-background-color: #000000;");
		BorderPane gb = new BorderPane();
		gb.setStyle("-fx-background-color: #000000;");
		TextArea resultTA = new TextArea(result.toString());
		resultTA.setEditable(false);
		resultTA.setMaxSize(1500, 100);
		resultTA.setFont(new Font(20));
		resultTA.setWrapText(true);
		resultTA.setStyle(
				"-fx-text-fill: #FF6700;-fx-alignment: CENTER;-fx-control-inner-background: #000000;-fx-border-color: #000000;");
		DPt.setContent(gb);
		ObservableList<String[]> data = FXCollections.observableArrayList();
		data.addAll(Arrays.asList(marged));
		data.remove(0);// remove titles from data
		TableView<String[]> table = new TableView<>();
		table.setStyle("-fx-table-cell-border-color: #FFFFFF;fx-table-border-color: #000000;-fx-border-color:FF6700;");
		String css2 = getClass().getResource("CSS2").toExternalForm();
		resultTA.getStylesheets().add(css2);
		String css = getClass().getResource("CSS").toExternalForm();
		table.getStylesheets().add(css);
		for (int i = 0; i < marged[0].length; i++) {
			TableColumn tc = new TableColumn(marged[0][i]);
			tc.setEditable(false);
			tc.setSortable(false);
			tc.setStyle(
					"-fx-background-color: #000000;-fx-alignment: CENTER;-fx-font-size: 20px;-fx-font-weight: bold;");
			final int colNo = i;
			tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
				public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
					return new SimpleStringProperty((p.getValue()[colNo]));
				}
			});
			tc.setPrefWidth(90);
			table.getColumns().add(tc);
			table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		}
		table.setItems(data);
		tabsS.setFullScreen(true);
		Scene tabsSC = new Scene(tabsP, 1920, 1080);
		table.setMaxSize(tabsSC.getWidth(), tabsSC.getHeight());
		Button startOver = new Button("Start over");
		startOver.setFont(new Font(20));
		startOver.setStyle(
				"-fx-background-color: #000000; -fx-border-color: #FF6700; -fx-border-width: 2px;-fx-text-fill:#FF6700");
		startOver.setOnAction(e -> {
			result.setLength(0);
			tabsS.close();
			primaryStage.show();
		});
		resultTA.selectionProperty();
		gb.setAlignment(resultTA, Pos.TOP_CENTER);
		gb.setTop(resultTA);
		gb.setAlignment(table, Pos.TOP_CENTER);
		gb.setCenter(table);
		gb.setAlignment(startOver, Pos.TOP_CENTER);
		gb.setBottom(startOver);
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Tab ImageT = new Tab("Graph");
		ImageT.setStyle("-fx-background-color: #000000;");
		ScrollPane sp = new ScrollPane();
		VBox vb = new VBox();
		Pane imageP = new Pane();
		ImageT.setOnSelectionChanged(e -> {
			tabsSC.heightProperty().subtract(1200);
		});
		// Create Power Sources
		List<Label> powerSourcesL = createLable(sourcesOrder.length, 950, 65, sourcesOrder); // Adjusted parameters
		imageP.getChildren().addAll(powerSourcesL);

		// Create LEDs under each power source
		List<Label> ledsL = createLable(LedsOrder.length, 350, 65, LedsOrder); // Adjusted parameters
		imageP.getChildren().addAll(ledsL);

		List<ImageView> powerSourcesIV = createImage(ledsL, sourcesOrder.length, 1010, 20, sourcesOrder); // Adjusted
																											// parameters
		imageP.getChildren().addAll(powerSourcesIV);

		// Create LEDs under each power source
		List<ImageView> ledsIV = createImage(ledsL, LedsOrder.length, 250, 20, LedsOrder); // Adjusted parameters
		imageP.getChildren().addAll(ledsIV);

		// Connect LEDs to Power Sources with Wires
		connectElements(imageP, ledsL, powerSourcesL);

		imageP.setStyle("-fx-background-color: #000000;");

		// Set the preferred size of the root Pane to be larger than the viewport
		imageP.setPrefSize(2000, ledsL.size() * 100); // You can adjust the height (1080) as needed the secound one
		// Wrap the root Pane inside a StackPane
		vb.getChildren().add(imageP);
		vb.setStyle("-fx-background-color: #000000;");
		vb.setAlignment(Pos.CENTER);
		BorderPane BPane = new BorderPane(vb);
		BPane.setStyle("-fx-background-color: #000000;");
		BPane.setCenter(imageP);
		// Set the content of the ScrollPane to be the StackPane
		sp.setContent(BPane);
		sp.setFitToWidth(true);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Disable horizontal scrollbar

		// Set the content of the Tab to be the ScrollPane
		ImageT.setContent(sp);

		// Add the tabs to the TabPane, etc.
		tabsP.getTabs().addAll(DPt, ImageT);
		tabsS.setFullScreen(true);
		tabsS.setScene(tabsSC);
		tabsS.setTitle("Max LED Lighting");
		tabsS.show();

	}
//******************************************create the list of labels************************************
	private <T> List<Label> createLable(int count, double startX, double y, T data) {
		List<Label> elements = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Label l;
			if (data.equals(sourcesOrder)) {
				l = new Label(i + 1 + "");
			} else {
				l = new Label(LedsOrder[i].getNum() + "");
			}
			l.setFont(new Font(20));
			l.setStyle("-fx-background-color: #FF6700;-fx-text-fill:#FFFFFF");
			l.setMinWidth(45);
			l.setAlignment(Pos.CENTER);
			l.setMaxHeight(20);
			l.relocate(startX, y + i * 100);
			elements.add(l);
		}
		return elements;
	}
	//******************************************create the list of images************************************

	private <T> List<ImageView> createImage(List<Label> elements1, int count, double startX, double y, T data) {
		List<ImageView> elements = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			ImageView imageView;
			if (data.equals(sourcesOrder)) {
				imageView = new ImageView(new Image(getClass().getResourceAsStream("/resources/PS.png")));
				imageView.setFitWidth(120);
				imageView.setFitHeight(120);
				// Configure other ImageView properties if needed
			} else {

				if (isNumberInArray(LEDS, Integer.parseInt(elements1.get(i).getText()))) {
					imageView = new ImageView(new Image(getClass().getResourceAsStream("/resources/LON.jpg")));

				} else {
					imageView = new ImageView(new Image(getClass().getResourceAsStream("/resources/lOF.png")));
				}
				imageView.setFitWidth(80);
				imageView.setFitHeight(80);
			}

			// Set common properties

			imageView.relocate(startX, y + i * 100);

			elements.add(imageView);
		}
		return elements;
	}
	//******************************************connect the list of labels************************************

	private void connectElements(Pane root, List<Label> elements1, List<Label> elements2) {
		for (int i = 0; i < LEDS.length; i++) {
			Label LED = findLabelByText(elements1, LEDS[i] + "");
			Label PS = findLabelByText(elements2, LEDS[i] + "");
			connectElementsWithWire(root, PS, LED);
		}

	}
//*******************************************************to find the lable index by the text in it*******************
	private Label findLabelByText(List<Label> labelList, String searchText) {
		for (Label label : labelList) {
			if (label.getText().equals(searchText)) {
				return label;
			}
		}
		return null; // Return null if not found
	}
//*************************************drow a line*************************************************
	private void connectElementsWithWire(Pane root, Label element1, Label element2) {
		Line wire = new Line(element1.getLayoutX() + ELEMENT_SIZE / 2, element1.getLayoutY() + ELEMENT_SIZE / 2,
				element2.getLayoutX() + ELEMENT_SIZE / 2, element2.getLayoutY() + ELEMENT_SIZE / 2);
		wire.setStyle(" -fx-stroke:#FF6700;");
		root.getChildren().add(wire);
	}
//*************************check if this led lighted or not*************************************
	public static boolean isNumberInArray(int[] array, int target) {
		for (int number : array) {
			if (number == target) {
				return true;
			}
		}
		return false;
	}

	public void backGround(Pane p) {
		try {
			BackgroundImage bGI = new BackgroundImage(new Image(getClass().getResourceAsStream("/resources/led.jpg")),
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					BackgroundSize.DEFAULT);
			Background bGround = new Background(bGI);
			p.setBackground(bGround);
		} catch (Exception e) {
			dialog(AlertType.ERROR, "Sorry, there was an error while uploading the background");
		}

	}

	public void dialog(AlertType t, String s) {
		Alert alert = new Alert(t);
		alert.setTitle("Dialog");
		alert.setHeaderText("");
		alert.setContentText(s);
		alert.showAndWait();
	}

	private TextField createPositiveIntegerWithSpaceField() {
		TextField textField = new TextField();
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("[\\d\\s]*")) {
				// Allow only positive integers and spaces
				textField.setText(oldValue);
				textField.setStyle("-fx-text-fill: #ffffff;-fx-control-inner-background: #000000;");
			}
		});

		return textField;
	}

	private TextField createPositiveInteger() {
		TextField textField = new TextField();
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				// Allow only positive integers and spaces
				textField.setText(oldValue);
				textField.setStyle("-fx-text-fill: #ffffff;-fx-control-inner-background: #000000;");
			}
		});

		return textField;
	}
//**********************************read the first line that have the number of leads****************************
	private int readFirstLine(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			// Read the first line
			String line = br.readLine();
			if (isNumeric(line)) {
				try {
					return Integer.parseInt(line);

				} catch (NumberFormatException e) {
					dialog(AlertType.ERROR,
							"Sorry, there is an invalid data in the file(non integer number), Please try to upload another one");
					return 0;
				}
			} else {
				dialog(AlertType.ERROR,
						"Sorry, there is an invalid data in the file(non numeric character), Please try to upload another one");
				return 0;

			}
		}
	}
	//**********************************read the secound line that have the leads order****************************

	private LED[] readSecondLine(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			// Read the first line
			br.readLine();

			// Read the second line
			String secondLine = br.readLine();

			// Check if there is exactly one more line
			if (secondLine != null && br.readLine() == null) {
				String[] values = secondLine.split(" ");
				// Convert the string values to an array of ints
				LED[] array = new LED[values.length];
				for (int i = 0; i < values.length; i++) {
					if (isNumeric(values[i])) {
						try {
							LED L = new LED(Integer.parseInt(values[i]));
							array[i] = L;
						} catch (NumberFormatException e) {
							dialog(AlertType.ERROR,
									"Sorry, there is an invalid data in the file(non integer number), Please try to upload another one");
							return null;
						}
					} else {
						dialog(AlertType.ERROR,
								"Sorry, there is an invalid data in the file (non-numeric character). Please try to upload another one.");
						return null;
					}
				}

				if (isValid(array)) {
					return array;
				} else {
					return null;
				}
			} else {
				dialog(AlertType.ERROR, "Invalid file format. The file should have exactly two lines.");
				return null;
			}
		}
	}

	private boolean isValid(int num) {
		if (num <= 0) {
			dialog(AlertType.ERROR, "Sorry, The number of leds you are trying to enter is invalid, Please try again");
			return false;
		}
		return true;
	}

	private static boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isValid(LED[] array) {
		// same as sources number
		if (array.length < sourcesOrder.length || array.length > sourcesOrder.length) {
			dialog(AlertType.ERROR,
					"Sorry, There is a Lack/excess in the data you are trying to enter, Please try again");
			return false;
		}
		// repetitions cheek
		int length = array.length;
		for (int i = 0; i < length - 1; i++) {
			for (int j = i + 1; j < length; j++) {
				if (array[i].getNum() == array[j].getNum()) {
					dialog(AlertType.ERROR,
							"Sorry, There is duplication in the data you are trying to enter, Please try again");
					return false;
				}
			}
		}
		// positive cheek
		for (int i = 0; i < array.length; i++) {
			if (array[i].getNum() <= 0 || array[i].getNum() > sourcesOrder.length) {
				dialog(AlertType.ERROR,
						"Sorry, There is a nonpositive/out of the range  value in the data you are trying to enter, Please try again");
				return false;
			}
		}

		return true;
	}

	public Boolean openFileChooser() {
		try {
			// reading the data from the file
			FileChooser fileChooser = new FileChooser();
			Stage fileChooserStage = new Stage();
			File file = fileChooser.showOpenDialog(fileChooserStage);
			if (file != null) {
				// Read the first line into an int variable
				int ledsNum = readFirstLine(file);
				if (ledsNum != 0) {
					if (isValid(ledsNum) == true) {
						sourcesOrder = new PowerSource[ledsNum];
						for (int i = 1; i <= ledsNum; i++) {
							PowerSource ps = new PowerSource(i);
							sourcesOrder[i - 1] = ps;
						}
					} else {
						return false;
					}
					// Read the second line into an array
					if (readSecondLine(file) != null) {
						LedsOrder = readSecondLine(file);
					} else {
						return false;
					}

				} else {
					openFileChooser();
				}
				return true;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public void findLCSWithDirection(LED[] ledsOrder2) {
		int n = ledsOrder2.length;
		// Initialization
		sol = new int[n + 1][n + 1];
		signs = new String[n + 1][n + 1];
		for (int i = 0; i <= n; i++) {
			sol[0][i] = 0;
			sol[i][0] = 0;
		}

		// Dynamic Programming to calculate LCS length and Path
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= n; j++) {
				if (sourcesOrder[i-1].getNum() == ledsOrder2[j - 1].getNum()) {
					sol[i][j] = sol[i - 1][j - 1] + 1;
					signs[i][j] = DIAG;
				} else {
					if (sol[i][j - 1] > sol[i - 1][j]) {
						sol[i][j] = sol[i][j - 1];
						signs[i][j] = LEFT;
					} else {
						sol[i][j] = sol[i - 1][j];
						signs[i][j] = UP;
					}
				}
			}
		}

		// Print the LCS and its length
		int lcsLength = sol[n][n];
		LEDS = new int[lcsLength];
		result.append("Maximum number of LEDs are lighted: " + lcsLength + "\n Lighted LEDS: ");
		printLCSPath(LedsOrder, n, n);

	}

	private void printLCSPath(LED[] L, int i, int j) {
		if (i == 0 || j == 0) {
			return;
		}

		if (signs[i][j].equals(DIAG)) {
			printLCSPath(L, i - 1, j - 1);
			result.append(L[j - 1].getNum() + " ");
			L[j - 1].setSituation(on);
			sourcesOrder[L[j - 1].getNum() - 1].setSituation(on);
			LEDS[count] = L[j - 1].getNum();
			count++;
		} else if (signs[i][j].equals(UP)) {
			printLCSPath(L, i - 1, j);
		} else if (signs[i][j].equals(LEFT)) {
			printLCSPath(L, i, j - 1);
		}
	}

	private void margeTabels() {
		marged = new String[sourcesOrder.length + 2][sourcesOrder.length + 2];

		// Fill in the first row and first column with "0"
		for (int i = 0; i <= sourcesOrder.length; i++) {
			marged[0][i] = "0";
			marged[i][0] = "0";
		}
		marged[0][0] = "";
		marged[0][1] = "";
		marged[1][0] = "";
		// Fill in the LedsOrder array to the first column
		for (int i = 1; i <= sourcesOrder.length; i++) {
			marged[i + 1][0] = sourcesOrder[i - 1].getNum() + "";
		}

		// Fill in the sourcesOrder array to the first row
		for (int i = 1; i <= sourcesOrder.length; i++) {
			marged[0][i + 1] = LedsOrder[i - 1].getNum() + "";
		}

		// Fill in values from sol and signs
		for (int i = 1; i <= sourcesOrder.length + 1; i++) {
			for (int j = 1; j <= sourcesOrder.length + 1; j++) {
				if (signs[i - 1][j - 1] == null) {
					marged[i][j] = " " + sol[i - 1][j - 1];
				} else {
					marged[i][j] = signs[i - 1][j - 1] + "  " + sol[i - 1][j - 1];
				}
			}
		}

		// Print the marged array for debugging
		for (int i = 0; i < marged.length; i++) {
			for (int j = 0; j < marged[0].length; j++) {
				// llLedsOrder[i].getNum()
				System.out.print(marged[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
