/**
 * This is a first part of project. user is allowed to drag and drop element from left pane to right pane
 * user can also delete element by clicking button 3 times. User can save commands from their program in a file.
 *
 * @author Bikash Mainali
 */
package augusta;

import augusta.properties.Access;
import augusta.properties.Direction;
import augusta.tree.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * This is a gui application program. This create gui component and save data to file in aug format.
 */
public class AugustaGui extends Application implements Serializable {
    private static BorderPane borderPane = new BorderPane();
    private static FlowPane rect;
    private static VBox commandList;
    private static TextChooser buttonSample;
    private static Point2D dragAnchor;
    private static double initX;
    private static double initY;
    private double mousePositionX, mousePositionY;
    private static String[] words = {"FORWARD()", "TURN( RIGHT )", "IF_CRUMB()", "ELSE()", "IF_ACCESS( OPEN AHEAD )", "WHILE_ ACCESS( OPEN AHEAD )",
            "REPEAT( 2 )", "EAT_CRUMB()", "DROP()", "NOP()", "HALT()", "BEGIN()", "END()"};
    private static String[] colorList = {"#F64747", "#AEA8D3", "#81CFE0", "#19B5FE", "#87D37C", "#36D7B7", "#F7CA18", "#F5D76E", "#F9BF3B", "#6C7A89", "#DADFE1"};
    private static ArrayList<String> comlist;

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    public void start(Stage primaryStage) throws Exception {
        //hold all scene
        commandList = new VBox(4);
        commandList.setPadding(new Insets(10, 10, 10, 10));
        List<TextChooser> list = new ArrayList<>();
        rect = new FlowPane();
        rect.setStyle("-fx-background-color: #b7e1e2; -fx-border-width: 1; -fx-border-color: black;");
        //add action to buttons.
        rectangleButtonAction();
        // create button and if the button have different option when right clicked. make that button special
        for (String word : words) {
            TextChooser btn = new TextChooser(word);
            if (word.startsWith("WHILE")) {
                String[] whileAccessOption = {"WHILE_ACCESS( BLOCKED,AHEAD )", "WHILE_ACCESS( OPEN,AHEAD )", "WHILE_ACCESS( BLOCKED,RIGHT )", "WHILE_ACCESS( OPEN,RIGHT )", "WHILE_ACCESS( BLOCKED,BEHIND )", "WHILE_ACCESS( OPEN,BEHIND )", "WHILE_ACCESS( BLOCKED,LEFT )", "WHILE_ACCESS( OPEN,LEFT )"};
                btn = new TextChooser(whileAccessOption);
            } else if (word.startsWith("TURN")) {
                String[] turnOption = {"TURN( AHEAD )", "TURN( RIGHT )", "TURN( BEHIND )", "TURN( LEFT )"};
                btn = new TextChooser(turnOption);
            } else if (word.startsWith("REPEAT")) {
                String[] repeatOption = {"REPEAT( 2 )", "REPEAT( 3 )", "REPEAT( 4 )", "REPEAT( 5 )", "REPEAT( 6 )", "REPEAT( 7 )", "REPEAT( 8 )", "REPEAT( 9 )"};
                btn = new TextChooser(repeatOption);
            } else if (word.startsWith("IF_ACCESS")) {
                String[] ifAccessOption = {"IF_ACCESS( BLOCKED,AHEAD )", "IF_ACCESS( OPEN,AHEAD )", "IF_ACCESS( BLOCKED,RIGHT )", "IF_ACCESS( OPEN,RIGHT )", "IF_ACCESS( BLOCKED,BEHIND )", "IF_ACCESS( OPEN,BEHIND )", "IF_ACCESS( BLOCKED,LEFT )", "IF_ACCESS( OPEN,LEFT )"};
                btn = new TextChooser(ifAccessOption);
            }
            btn.setMaxWidth(Double.MAX_VALUE);
            //set random color and border of btn.
            btn.setStyle("-fx-background-color: " + colorList[new Random().nextInt((colorList.length))] + "; " + "-fx-border-color: black;");
            btn.setMinHeight(29);
            btn.setMinWidth(223);
            btn.setAlignment(Pos.CENTER);
            buttonSample = btn;
            mainMenuDragDetection(btn);
            list.add(btn);
            buttonSample = btn;
        }
        commandList.getChildren().addAll(list);
        borderPane.setLeft(commandList);
        borderPane.setCenter(rect);
        commandList.setStyle("-fx-background-color: #EEE8AC;");
        Button save = saveButton();
        save.setAlignment(Pos.CENTER);
        borderPane.setBottom(save);
        Scene scene = new Scene(borderPane);
        // set title as Calculator
        primaryStage.setTitle("Project2");
//        //set width and height
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);
        // make non resizable
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        //show the scene
        primaryStage.show();
    }

    /**
     * This method create save button and action required when that button is pressed
     *
     * @return return Button with save label.
     */
    private Button saveButton() {
        // create new stage
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Save to File");

        Button save = new Button("SAVE");
        // when save button is pressed
        save.setOnAction((ActionEvent event) -> {
            // crate file chooser
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Augusta files (*.aug)", "*.aug");
            fileChooser.getExtensionFilters().add(extFilter);
            // show save dialog
            File file = fileChooser.showSaveDialog(primaryStage);
            //if file exist than
            if (file != null) {
                //get full or absolute path of the file
                String fileName = file.getAbsolutePath();
                //save data to fileName
                saveFile(fileName);
            }
        });
        // set button style.
        save.setStyle("-fx-background-color: " + colorList[new Random().nextInt((colorList.length))] + "; -fx-border-color: black;");
        save.setMinWidth(302);

        return save;
    }

    /**
     * this is a helper method to save file to the system.
     *
     * @param outName file of the name to write file to
     */
    private void saveFile(String outName) {

        BufferedWriter bw = null;
        try {//create file
            File file = new File(outName);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // buffer writer object created
            LinkedList<ProgNode> str = parseFile();
            if (str != null) {
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file.getAbsoluteFile()));
                //write each and every node ProgNode to file
                for (ProgNode p : str) {
                    outputStream.writeObject(p);
                }
                // close output
                outputStream.close();
            }
        } catch (IOException io) {
            showAlert(io.getMessage(), io.getMessage(), io.getMessage());
        } catch (Exception e) {
            showAlert(e.getMessage(), e.getMessage(), e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException io) {
                    showAlert(io.getMessage(), io.getMessage(), io.getMessage());
                }
            }
        }
    }

    /**
     * private variable which let drag detection feature to passed TextChooser parameter.
     * This function add different action to make and detetected in for that object.
     *
     * @param dragIcon
     */
    private void mainMenuDragDetection(TextChooser dragIcon) {
        // method that get call when mouse id moved withing rect that is right pane
        rect.setOnMouseMoved((MouseEvent me) -> {
            mousePositionX = me.getX();
            mousePositionY = me.getY();

        });
        // action when that object is drag over
        rect.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            db.setDragViewOffsetX(0);
            db.setDragViewOffsetY(0);
            event.consume();
        });

        // action to add when button on left is pressed
        dragIcon.setOnMousePressed((MouseEvent me) -> {
            // When mouse is pressed, store initial position
            initX = dragIcon.getTranslateX(); // location of rectangle (UL corner)
            initY = dragIcon.getTranslateY(); // location of rectangle (UL corner)
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY() // loc of mouse
            );
        });

        //action when button is dragged
        dragIcon.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = dragIcon.startDragAndDrop(TransferMode.ANY);
            //image is shouw when button is dragged
            db.setDragView(new Image("btn.png"));
                /* Put a string on a dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(dragIcon.getText());
            db.setContent(content);
            event.consume();
        });
    }

    /**
     * show alert text to user when file cannot be parsed completely or file cannot be store in specified location.
     *
     * @param title      title of alert box
     * @param headerText header of alert box
     * @param message    message of the alert box.
     */
    private static void showAlert(String title, String headerText, String message) {
        // alert object of type error
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * add function to drop the button on the pane. and other function to the make is moved.
     */
    private void rectangleButtonAction() {
        // when a dragged object is dropped on the right pane
        rect.setOnDragDropped((DragEvent event) -> {

            Dragboard db = event.getDragboard();

            boolean success = false;
            if (db.hasString()) {
                //create button to add to the pane
                Button btn = new Button(db.getString());
                btn.setPrefWidth(buttonSample.getWidth());
                btn.setPrefHeight(buttonSample.getHeight());
                btn.setStyle("-fx-background-color: " + colorList[new Random().nextInt((colorList.length))] + "; " + "-fx-border-color: black;");
                rect.getChildren().add(btn);

                if ((mousePositionX >= 0) &&
                        (mousePositionX <= 898 - btn.getWidth()) && (mousePositionY >= 0) &&
                        (mousePositionY <= 648 - 2 * btn.getHeight() - 1)) {
                    btn.relocate(mousePositionX, mousePositionY);
                }
                //add function to drag when button on right pane is moved.
                btn.setOnMouseDragged((MouseEvent me) -> {
                    double dragX = me.getSceneX() - dragAnchor.getX(); // delta of mouse
                    double dragY = me.getSceneY() - dragAnchor.getY(); // delta of mouse
                    // Calculate new position of the circle.
                    double newXPosition = initX + dragX; // delta of rectangle
                    newXPosition -= newXPosition % 20; // coarse movement "snap2grid"
                    double newYPosition = initY + dragY; // delta of rectangle
                    newYPosition -= newYPosition % 1; // coarse movement "snap2grid"
                    // If new position does not exceed borders of the outer recta
                    btn.setTranslateX(newXPosition);
                    btn.setTranslateY(newYPosition);
                });

                // add function what to do when moused is pressed
                btn.setOnMousePressed((MouseEvent me) -> {
                    //System.out.println("inside Mouse Pressed");
                    // When mouse is pressed, store initial position
                    initX = btn.getTranslateX(); // location of rectangle (UL corner)
                    initY = btn.getTranslateY(); // location of rectangle (UL corner)
                    dragAnchor = new Point2D(
                            me.getSceneX(), me.getSceneY() // loc of mouse
                    );
                });
                // Add action when button is dragged
                btn.setOnDragDetected((MouseEvent e) -> {
                    initX = btn.getTranslateX(); // location of rectangle (UL corner)
                    initY = btn.getTranslateY(); // location of rectangle (UL corner)
                    dragAnchor = new Point2D(
                            e.getSceneX(), e.getSceneY() // loc of mouse
                    );

                });
                // when button is 3 times to delete the node.
                btn.setOnMouseClicked((MouseEvent levent) -> {
                    if (levent.getButton().equals(MouseButton.PRIMARY)) {
                        if (levent.getClickCount() == 3) {
                            rect.getChildren().remove(btn);
                        }
                    }

                });
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * call gui application start method.
     * @param args armument passed by the user
     */
    public static void main(String[] args) {
        // call main by passing argument.
        launch(args);

    }

    /**
     * get all data from right pane of gui and try to parse it.
     *
     * @return return linked List
     */
    private static LinkedList<ProgNode> parseFile() {
        // get all children of the rect ( right pane)
        javafx.collections.ObservableList<Node> arr = rect.getChildren();

        LinkedList<Node> list = new LinkedList<>();
        for (Node node : arr) {
            list.add(node);
        }

        //sort the list by NodeComparator
        list.sort(new NodeComparator());


        String str = "";
        // add all string by "\n"
        for (Node btn : list) {
            Button b = (Button) btn;
            str += b.getText() + "\n";
        }
        // split that string by "\n"
        String[] a = str.split("\n");

        comlist = new ArrayList<>();
        Collections.addAll(comlist, a);

        // parse that string from GUI
        LinkedList<ProgNode> parse = parseHelper("");
//        System.out.println(parse);
        //return that linked list t right in the file
        return parse;
    }

    /**
     * this is a parse helper recursive function
     *
     * @param prevcommand string to notify if the we are looking for if block or while block, repeat
     * @return return linked List containing all node.
     */
    private static LinkedList<ProgNode> parseHelper(String prevcommand) {

        LinkedList<ProgNode> returnList = new LinkedList<>();
        // Arraylist containing string of command is not empty
        while (!comlist.isEmpty()) {
            //remove the first one from the list
            String command = comlist.remove(0);
            // if command is not empty
            if (!command.equals("")) {
                // get first 4 string char
                String cmd = command.substring(0, 4);

                switch (cmd) {
                    case "FORW":
                        // if forward store in the list.
                        returnList.add(new Forward());
                        break;
                    case "IF_A":
                        // split string in comma to get different parameter
                        String[] commabreak = command.split(",");
                        // if parameter means look for "else"
                        LinkedList<ProgNode> ifblock = parseHelper("IF");
                        // else parameter means look for "end"
                        LinkedList<ProgNode> elseblock = parseHelper("WH");
                        //if ifblock is empty
                        if (ifblock == null) {
                            ifblock = new LinkedList<>();
                        }
                        //if else block is empty
                        if (elseblock == null) {
                            elseblock = new LinkedList<>();
                        }
                        returnList.add(new IfBlocks(Access.valueOf(commabreak[0].substring(11)), Direction.valueOf(commabreak[1].substring(0, commabreak[1].length() - 2)), ifblock, elseblock));
                        break;
                    case "TURN":
                        //add to list
                        returnList.add(new Turn(Direction.valueOf(command.substring(6, command.length() - 2))));
                        break;
                    case "IF_C":
                        //recurse by passing if
                        LinkedList<ProgNode> ifblockC = parseHelper("IF");
                        //recurse by passing while
                        LinkedList<ProgNode> elseblockC = parseHelper("WH");
                        if (ifblockC == null) {
                            ifblockC = new LinkedList<>();
                        }
                        if (elseblockC == null) {
                            elseblockC = new LinkedList<>();
                        }
                        returnList.add(new IfCrumb(ifblockC, elseblockC));
                        break;
                    case "WHIL":
                        // split string in comma to get different parameter
                        String[] cobreak = command.split(",");
                        //recurse by passing while
                        LinkedList<ProgNode> whileBlock = parseHelper("WH");
                        //add to the list
                        returnList.add(new While(Access.valueOf(cobreak[0].substring(14)), Direction.valueOf(cobreak[1].substring(0, cobreak[1].length() - 2)), whileBlock));
                        break;
                    case "REPE":
                        // look for repeat block
                        LinkedList<ProgNode> repeatblock = parseHelper("WH");
                        //add to the list.
                        returnList.add(new Repeat(Integer.parseInt(command.substring(8, command.length() - 2)), repeatblock));
                        break;
                    case "EAT_":
                        //add to the list.
                        returnList.add(new Eat());
                        break;
                    case "ELSE":
                        //if the parameter was if it is okay otherwise show error
                        if (prevcommand.equals("IF")) {
                            return returnList;
                        } else {
                            showAlert("ERROR", "ERROR", "CANNOT PARSE COMPLETELY");
                            comlist.removeAll(comlist);
                        }
                    case "DROP":
                        returnList.add(new Drop());
                        break;
                    case "NOP(":
                        returnList.add(new DoNothing());
                        break;
                    case "HALT":
                        returnList.add(new Halt());
                        break;
                    case "END(":
                        if (prevcommand.equals("WH")) {
                            return returnList;
                        } else {
                            showAlert("ERROR", "ERROR", "CANNOT PARSE COMPLETELY");
                            comlist.removeAll(comlist);
                        }
                        break;
                }
            }
        }

        if (prevcommand.equals("")) {
            return returnList;
        } else {
            return null;
        }
    }

    /**
     * compare two node based on their position on the right pane.
     */
    private static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            Button b1 = (Button) o1;
            Button b2 = (Button) o2;
            Double s1 = b1.getBoundsInParent().getMinY();
            Double s2 = b2.getBoundsInParent().getMinY();
            return s1.compareTo(s2);
        }
    }

    /**
     * object to store things on the left pane. add functionality like dropdown on left pane element
     */
    public class TextChooser extends StackPane {

        private Label lbl = new Label();
        private ComboBox<String> dropDownOption = new ComboBox<>();

        /**
         * constructor that initialize and function to it.
         *
         * @param options list of string for dropdown and label
         */
        public TextChooser(String... options) {
            // bind lbl property to the dropdownOption.
            lbl.textProperty().bind(
                    dropDownOption.getSelectionModel().selectedItemProperty()
            );
            lbl.visibleProperty().bind(
                    dropDownOption.visibleProperty().not()
            );
            lbl.setPadding(new Insets(5, 0, 5, 9));
            dropDownOption.getItems().setAll(options);
            dropDownOption.getSelectionModel().select(0);
            dropDownOption.setVisible(false);
            // when the button is right clicked. show dropdown option.
            lbl.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    dropDownOption.setVisible(true);
                    dropDownOption.setStyle("-fx-background-color: " + colorList[new Random().nextInt((colorList.length))] + "; ");
                    dropDownOption.showingProperty().addListener(observable -> {
                        if (!dropDownOption.isShowing()) {
                            dropDownOption.setVisible(false);
                        }
                    });
                }
            });

            //if selection is don make dropDownOption to false.
            dropDownOption.setOnMouseExited(event -> {
                if (!dropDownOption.isShowing()) {
                    dropDownOption.setVisible(false);
                }
            });
            getChildren().setAll(lbl, dropDownOption);
        }

        /**
         * return the name of the lbl
         *
         * @return string representation or lbl of the TextChooser.
         */
        public String getText() {
            return lbl.getText();
        }
    }
}
