package booksdb.view;

import booksdb.MainApp;
import booksdb.model.aBook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class ShowResultController {
    @FXML
    private TableView<aBook> personTable1;
    @FXML
    private TableColumn<aBook, Integer> IDcolumn;

    @FXML
    private TableColumn<aBook, String> TitleColumn;

    @FXML
    private TableColumn<aBook, String> AuthorColumn;

    @FXML
    private TableColumn<aBook, Integer> DateColumn;
    @FXML
    private TextField searchfield;

    @FXML
    private ChoiceBox<String> choice;


    private Stage dialogStage2;
    boolean okClicked = false;
    private ObservableList<aBook> allbooks = FXCollections.observableArrayList();
    private ObservableList<aBook> toshow = FXCollections.observableArrayList();

    public void setDialogStage(Stage dialogStage2) {
        this.dialogStage2 = dialogStage2;
    }


    @FXML
    private void initialize() {
        IDcolumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TitleColumn.setCellValueFactory(new PropertyValueFactory<>("tittle"));
        AuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        choice.getItems().add("ID");
        choice.getItems().add("Title");
        choice.getItems().add("Author");
        choice.getItems().add("Year");


    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public File localGetFile() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

   @FXML
    private void go() throws XPathExpressionException, ParserConfigurationException {
       File checkingFile = localGetFile();
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       factory.setNamespaceAware(true);
       DocumentBuilder builder = factory.newDocumentBuilder();
       Document document = null;
       try {
           document = builder.parse(checkingFile);
       } catch (Exception e) { // catches ANY exception
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Error");
           alert.setHeaderText("Could not load data");
           alert.setContentText("Could not load data from file:\n" + checkingFile.getPath());

           alert.showAndWait();
       }
       allbooks.clear();
       // Получаем корневой элемент
       NodeList ids = document.getDocumentElement().getElementsByTagName("Id");
       NodeList titles = document.getDocumentElement().getElementsByTagName("Title");
       NodeList authors = document.getDocumentElement().getElementsByTagName("Author");
       NodeList dates = document.getDocumentElement().getElementsByTagName("Date");
       ArrayList<Integer> k = new ArrayList<>();
       for (int i = 0; i < ids.getLength(); i++) {
           Node id1 = ids.item(i);
           int idx = Integer.parseInt(id1.getTextContent());
           if (k.size() > 0) {
               for (int r = 0; r < k.size(); r++) {
                   if (k.get(r) == idx) {
                       Alert alert = new Alert(Alert.AlertType.WARNING);
                       alert.initOwner(dialogStage2);
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
           Node dat1 = dates.item(i);
           int datx = Integer.parseInt(dat1.getTextContent());
           aBook abook = new aBook(idx, titlex, authx, datx);
           allbooks.add(abook);
       }
       toshow.clear();
       //вот тут надо провести проверку уже
       String choose = choice.getValue();
       ArrayList<Integer> indexes = new ArrayList<>();
         if (choose == "ID") {
             indexes.clear();
             toshow.clear();
             int id = Integer.parseInt(searchfield.getText());
             for(int i = 0 ; i < allbooks.size(); i ++)
             {
                 aBook book = allbooks.get(i);
                 int curid = book.getId();
                 if(curid == id)
                 {
                     indexes.add(i);
                 }
             }
             if(indexes.size()>0)
             {
                 for(int t = 0 ; t < indexes.size(); t ++)
                 {
                     int index = indexes.get(t);
                     aBook boik = allbooks.get(index);
                     toshow.add(boik);

                 }
             }
             else
             {
                 Alert alert = new Alert(Alert.AlertType.WARNING);
                 alert.initOwner(dialogStage2);
                 alert.setTitle("There are no books with such parameters");
                 alert.setHeaderText("Nothing to show");
                 alert.setContentText("There are no books with such parameters");

                 alert.showAndWait();
             }
             personTable1.setItems(toshow);

         }
         if (choose == "Title") {
             indexes.clear();
             toshow.clear();
             char[] auth = searchfield.getText().toCharArray();
             char[] authfa;
             boolean contains = false;
             for(int i = 0 ; i < allbooks.size(); i ++)
             {
                 aBook book = allbooks.get(i);
                 authfa = book.getTittle().toCharArray(); //сравнить
                 if(auth.length == authfa.length)
                 {
                     for(int f = 0; f < auth.length; f++)
                     {
                         if(auth[f] == authfa[f])
                         {
                             contains = true;
                         }

                     }
                     if(contains == true)
                     {
                         indexes.add(i);
                     }
                     contains = false;
                 }


             }
             if(indexes.size()>0)
             {
                 for(int t = 0 ; t < indexes.size(); t ++)
                 {
                     int index = indexes.get(t);
                     aBook boik = allbooks.get(index);
                     toshow.add(boik);
                 }
             }
             else
             {
                 Alert alert = new Alert(Alert.AlertType.WARNING);
                 alert.initOwner(dialogStage2);
                 alert.setTitle("There are no books with such parameters");
                 alert.setHeaderText("Nothing to show");
                 alert.setContentText("There are no books with such parameters");

                 alert.showAndWait();
             }
             personTable1.setItems(toshow);



         }
         if (choose == "Author") {
             indexes.clear();
             toshow.clear();
             char[] auth = searchfield.getText().toCharArray();
             char[] authfa;
             boolean contains = false;
             for(int i = 0 ; i < allbooks.size(); i ++)
             {
                 aBook book = allbooks.get(i);
                 authfa = book.getAuthor().toCharArray(); //сравнить
                 if(auth.length == authfa.length)
                 {
                     for(int f = 0; f < auth.length; f++)
                     {
                         if(auth[f] == authfa[f])
                         {
                             contains = true;
                         }

                     }
                     if(contains == true)
                 {
                     indexes.add(i);
                 }
                     contains = false;
                 }


             }
             if(indexes.size()>0)
             {
                 for(int t = 0 ; t < indexes.size(); t ++)
                 {
                     int index = indexes.get(t);
                     aBook boik = allbooks.get(index);
                     toshow.add(boik);

                 }
             }
             else
             {
                 Alert alert = new Alert(Alert.AlertType.WARNING);
                 alert.initOwner(dialogStage2);
                 alert.setTitle("There are no books with such parameters");
                 alert.setHeaderText("Nothing to show");
                 alert.setContentText("There are no books with such parameters");

                 alert.showAndWait();
             }
             personTable1.setItems(toshow);

         }
         if (choose == "Year") {
             indexes.clear();
             toshow.clear();
             int year = Integer.parseInt(searchfield.getText());
             for(int i = 0 ; i < allbooks.size(); i ++)
             {
                 aBook book = allbooks.get(i);
                 int curyear = book.getYear();
                 if(curyear == year)
                 {
                     indexes.add(i);
                 }
             }
             if(indexes.size()>0)
             {
                 for(int t = 0 ; t < indexes.size(); t ++)
                 {
                     int index = indexes.get(t);
                     aBook boik = allbooks.get(index);
                     toshow.add(boik);

                 }
             }
             else
             {
                 Alert alert = new Alert(Alert.AlertType.WARNING);
                 alert.initOwner(dialogStage2);
                 alert.setTitle("There are no books with such parameters");
                 alert.setHeaderText("Nothing to show");
                 alert.setContentText("There are no books with such parameters");

                 alert.showAndWait();
             }
             personTable1.setItems(toshow);


         }

         else {
         }
   }
}

