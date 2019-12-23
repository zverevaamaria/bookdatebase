package booksdb.view;

import booksdb.MainApp;
import booksdb.model.aBook;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class BookEditDialogController {
    @FXML
    private TextField tittleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField idField;



    private Stage dialogStage;
    private aBook abook;
    private boolean okClicked = false;

    int newid;
    String newtitle;
    String newauthor;
    int newyear ;



    public File localGetFile() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }
    public boolean uniqueIdforEdit(int newid) throws IOException, SAXException, ParserConfigurationException {
        boolean unique = true;
        BookOverviewController poc = new BookOverviewController();
        int idm = poc.getselId();//сработало!
        File checkingFile = localGetFile();
        ArrayList<Integer> listOfIds = new ArrayList<Integer>();
        //
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = null;
        try {
            document = builder.parse(checkingFile);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + checkingFile);

            alert.showAndWait();
        }
        listOfIds.clear();
        NodeList books = document.getDocumentElement().getElementsByTagName("Id");
        for (int i = 0; i < books.getLength(); i++) {
            Node id = books.item(i);
            if (id.getNodeType() != Node.TEXT_NODE) {
                NodeList bookProps = id.getChildNodes();
                int idx = Integer.parseInt(id.getTextContent());
                listOfIds.add(idx);
            }
        }
        //удалить сам выбраный элемент
        for(int i = 0; i < listOfIds.size(); i++)
        {
            if(listOfIds.get(i) == idm)
            {
                listOfIds.remove(i);
            }

        }
        for(int i = 0; i < listOfIds.size(); i++)
        {
            int id = listOfIds.get(i);
            if(id == newid){ unique =  false;}
        }
        return unique;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPerson(aBook abook) {
        this.abook = abook;

        idField.setText(Integer.toString(abook.getId()));
        tittleField.setText(abook.getTittle());
        authorField.setText(abook.getAuthor());
        dateField.setText(Integer.toString(abook.getYear()));
    }

    public boolean isOkClickedforedit() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() throws ParserConfigurationException, SAXException, IOException {
        boolean uniqueid = uniqueIdforEdit(Integer.parseInt(idField.getText()));
        if (isInputValid() && uniqueid == true) {
            abook.setId(Integer.parseInt((idField.getText())));
            abook.setTittle(tittleField.getText());
            abook.setAuthor(authorField.getText());
            abook.setYear(Integer.parseInt(dateField.getText()));
            int newidp = Integer.parseInt(idField.getText());
            setId(newidp);
            String newtitlep = tittleField.getText();
            setTit(newtitlep);
            String newauthorp =  authorField.getText();
            seAuth(newauthorp);
            int newyearp = Integer.parseInt(dateField.getText());
            setDate(newyearp);
            okClicked = true;
            dialogStage.close();
        }
        else{Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ID must be unique!");
            alert.setHeaderText("Correct the ID");
            alert.setContentText("ID must be unique! Pleause, correct ID!" );
            alert.showAndWait();}

    }

    public int  getnewid(){return newid;}
    public String getnewtitle(){return newtitle;}
    public String getNewauthor(){return newauthor;}
    public int  getnewyaer(){return newyear;}

    public void setId(int newid) { this.newid = newid; }
    public void setTit(String newtitle) {
        this.newtitle = newtitle;
    }
    public void seAuth(String newauthor) {
        this.newauthor = newauthor;
    }
    public void setDate(int newyear) {
        this.newyear = newyear;
    }
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


    private boolean isInputValid() throws NumberFormatException  {
        String errorMessage = "";

        if (idField.getText() == null || idField.getText().length() == 0) {
            errorMessage += "Null ID!\n";
        } else {
            // try to parse id into an int.
            try {
                int id = Integer.parseInt(idField.getText());
                if (id < 0) {
                    errorMessage += "ID cant be < 0\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "No valid id (must be an integer)!\n";
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(dialogStage);
                alert.setTitle("Invalid Fields");
                alert.setHeaderText("Please correct invalid fields");
                alert.setContentText(errorMessage);

                alert.showAndWait();

                return false;
            }
        }

        if (tittleField.getText() == null || tittleField.getText().length() == 0) {
            errorMessage += "No valid tittle!\n";
        }
        if (authorField.getText() == null || authorField.getText().length() == 0) {
            errorMessage += "No author name!\n";
        }
        if (dateField.getText() == null || dateField.getText().length() == 0) {
            errorMessage += "No year!\n";
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault(), java.util.Locale.getDefault());
        calendar.setTime(new java.util.Date());
        int currentYear = calendar.get(java.util.Calendar.YEAR);
        if ( Integer.parseInt(dateField.getText()) > currentYear ) {
            errorMessage += "No valid year!\n";
        }
        if(Integer.parseInt(idField.getText()) == 0){}
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}