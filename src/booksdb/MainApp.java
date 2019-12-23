package booksdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import booksdb.model.aBook;
import booksdb.view.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<aBook> books_list = FXCollections.observableArrayList();

    public MainApp() {
    }
    public ObservableList<aBook> getBookData() {
        return books_list;
    }

    @Override
    public void start(Stage primaryStage) throws ParserConfigurationException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Bookapp");
        books_list.clear();
        initRootLayout();
        showPersonOverview();
    }

    public void initRootLayout() throws ParserConfigurationException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showPersonOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/BookOverview.fxml"));
            AnchorPane bookOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(bookOverview);

            BookOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   public void showresults(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ShowResult.fxml"));
            AnchorPane page1 = new AnchorPane() ;
            page1 = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page1);
            dialogStage.setScene(scene);

            ShowResultController controller = loader.getController();
            BookOverviewController poc = new BookOverviewController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException n){
            n.printStackTrace();
        }

    }


    public boolean showNewBookEditDialog(aBook aBook) {
        try {
            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(MainApp.class.getResource("view/NewBookDialog.fxml"));
            AnchorPane page1 = (AnchorPane) loader1.load();

            Stage dialogStage1 = new Stage();
            dialogStage1.setTitle("Edit Person");
            dialogStage1.initModality(Modality.WINDOW_MODAL);
            dialogStage1.initOwner(primaryStage);
            Scene scene1 = new Scene(page1);
            dialogStage1.setScene(scene1);

            NewBookDialogController controller = loader1.getController();
            controller.setDialogStage(dialogStage1);
            controller.setBook(aBook);

            dialogStage1.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showBookEditDialog(aBook aBook) {
        try {
            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(MainApp.class.getResource("view/BookEditDialog.fxml"));
            AnchorPane page1 = (AnchorPane) loader1.load();

            Stage dialogStage1 = new Stage();
            dialogStage1.setTitle("Edit Book");
            dialogStage1.initModality(Modality.WINDOW_MODAL);
            dialogStage1.initOwner(primaryStage);
            Scene scene1 = new Scene(page1);
            dialogStage1.setScene(scene1);

            BookEditDialogController controller = loader1.getController();
            controller.setDialogStage(dialogStage1);
            controller.setPerson(aBook);

            dialogStage1.showAndWait();
            return controller.isOkClickedforedit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //getPersonFilePath  оставляем
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }
    //setPersonFilePath  оставляем
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("Booksapp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("Booksapp");
        }
    }

    public void loabBookDataFromFile(File file) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = null;
        try {
            document = builder.parse(file);
            if(file == null)
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Open file");
                alert.setContentText("Open file" );

                alert.showAndWait();
            }
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
        books_list.clear();
        // Получаем корневой элемент
        NodeList ids = document.getDocumentElement().getElementsByTagName("Id");
        NodeList titles = document.getDocumentElement().getElementsByTagName("Title");
        NodeList authors = document.getDocumentElement().getElementsByTagName("Author");
        NodeList dates = document.getDocumentElement().getElementsByTagName("Date");
         ArrayList<Integer> k = new ArrayList<>();
        for (int i = 0; i < ids.getLength(); i++) {
            Node id1 = ids.item(i);

                    int idx = Integer.parseInt(id1.getTextContent());
                    if(k.size()>0){
                        for(int r = 0; r < k.size();r++)
                        {
                            if(k.get(r) == idx)
                            {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.initOwner(getPrimaryStage());
                                alert.setTitle("There are(is) not unique ID(IDS)");
                                alert.setHeaderText("Not correct ID");
                                alert.setContentText("Correct the ID");

                                alert.showAndWait();
                            }
                        }
                    }
                    k.add(idx);
            Node tit1 = titles.item(i);
                    String titlex = tit1.getTextContent();
            Node auth1 = authors.item(i);
                    String authx = auth1.getTextContent();
            Node dat1 = ids.item(i);
                    int datx = Integer.parseInt(dat1.getTextContent());
                      aBook abook = new aBook(idx, titlex, authx, datx );
                      books_list.add(abook);
                    }
                }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}