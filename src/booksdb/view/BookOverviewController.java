package booksdb.view;

import booksdb.model.aBook;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import booksdb.MainApp;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

public class BookOverviewController {
    @FXML
    private TableView<aBook> personTable;

    @FXML
    private TableColumn<aBook, Integer> idColumn;

    @FXML
    private TableColumn<aBook, String> TittleColumn;

    @FXML
    private TableColumn<aBook, String> AuthorColumn;

    @FXML
    private Label idLabel;

    @FXML
    private Label TittleLabel;

    @FXML
    private Label AuthorLabel;

    @FXML
    private Label YearLabel;


    private MainApp mainApp;
    boolean contains = false;
    int selectedid = 0;
    public int getselId() {
        return selectedid;
    }
    private boolean okClicked = false;


    public BookOverviewController() {
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TittleColumn.setCellValueFactory(new PropertyValueFactory<>("tittle"));
        AuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        showBookDetails(null);

        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookDetails(newValue));
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        personTable.setItems(mainApp.getBookData());
    }


    private void showBookDetails(aBook book) {
        if (book != null) {
            idLabel.setText(Integer.toString(book.getId()));
            TittleLabel.setText(book.getTittle());
            AuthorLabel.setText(book.getAuthor());
            YearLabel.setText(Integer.toString(book.getYear()));
        } else {
            idLabel.setText("");
            TittleLabel.setText("");
            AuthorLabel.setText("");
            YearLabel.setText("");
        }
    }

    private void refreshXML(File file, Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    Document FiletoDoc(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmldoc = builder.parse(file);
        return xmldoc;
    }
    @FXML
    private void handleDeleteBook() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException, TransformerException {
        String idFromTable  = idLabel.getText();
        int t = personTable.getSelectionModel().getSelectedIndex();
        if(t >= 0){
        personTable.getItems().remove(t);
        File bookFile = mainApp.getPersonFilePath();
        Document xmldoc = FiletoDoc(bookFile);
        XPathFactory builderx = XPathFactory.newInstance();
        XPath xpath = builderx.newXPath();
        String expression = "BookCatalogue/Book[Id =" + idFromTable + "]";
        Node node = (Node) xpath.compile(expression).evaluate(xmldoc, XPathConstants.NODE);
        node.getParentNode().removeChild(node);
        refreshXML(bookFile, xmldoc);
        File file = mainApp.getPersonFilePath();
        mainApp.loabBookDataFromFile(file);}
        else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");
            alert.showAndWait();
            }
        }

    @FXML
    private void handleNewBook() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        aBook book = new aBook();
        boolean okClicked = mainApp.showNewBookEditDialog(book);
        if (okClicked) {
            mainApp.getBookData().add(book);
            ObservableList<aBook> last = mainApp.getBookData();
            int newid ;
            String newtitle;
            String newauthor;
            int newyear ;
                aBook abookk = last.get(last.size() - 1);
                newid = abookk.getId();
                newtitle = abookk.getTittle();
                newauthor = abookk.getAuthor();
                newyear = abookk.getYear();
            File bookFile1 = mainApp.getPersonFilePath();
            Document xmldoc1 = FiletoDoc(bookFile1);
             Node books1 = xmldoc1.getDocumentElement().getElementsByTagName("Book").item(0);
                Element newbook = xmldoc1.createElement("Book");
                Element newiid = xmldoc1.createElement("Id");
                newiid.setTextContent(Integer.toString(newid));
                Element newtit = xmldoc1.createElement("Title");
                newtit.setTextContent(newtitle);
                Element newauth = xmldoc1.createElement("Author");
                newauth.setTextContent(newauthor);
                Element newyeaar = xmldoc1.createElement("Date");
                newyeaar.setTextContent(Integer.toString(newyear));
                newbook.appendChild(newiid);
                newbook.appendChild(newtit);
                newbook.appendChild(newauth);
                newbook.appendChild(newyeaar);
                books1.getParentNode().appendChild(newbook);

            refreshXML(bookFile1, xmldoc1);
        }
    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void searching() throws IOException, SAXException, ParserConfigurationException {
        mainApp.showresults();
     }

   @FXML
    private void handleEditBook() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {
        aBook selectedBook = personTable.getSelectionModel().getSelectedItem();
        selectedid = idColumn.getCellData(personTable.getSelectionModel().getSelectedIndex());
        int idFromTable = selectedid;
        int u = 0;
        ObservableList<aBook> bookss = mainApp.getBookData();
       for(u = 0 ; u < bookss.size(); u ++)
       {
           aBook abookk = bookss.get(u);
           int iid = abookk.getId();
           if(iid == idFromTable){ break;}
           System.out.println("iid = " + iid);
           System.out.println("искомое = " + idFromTable);
       }
       System.out.println("просто значение" + idFromTable);
        if (selectedBook != null) {
            boolean okClicked = mainApp.showBookEditDialog(selectedBook);
            selectedid = idColumn.getCellData(personTable.getSelectionModel().getSelectedIndex());
            System.out.println("значение u " + u);
            if (okClicked) {
                showBookDetails(selectedBook);
//сначала найти в xml выбранный элемент, потом отредактировать его
                aBook newbook = bookss.get(u);
                int bookid = newbook.getId();
                String booktit = newbook.getTittle();
                String bookauth = newbook.getAuthor();
                int bookdat = newbook.getYear();
                File bookFile = mainApp.getPersonFilePath();
                Document xmldoc = FiletoDoc(bookFile);
                XPathFactory builderx = XPathFactory.newInstance();
                XPath xpath = builderx.newXPath();
                String expression1 = "BookCatalogue/Book[Id =" + idFromTable + "]";
                System.out.println("тута");
                System.out.println("значение искомого" + idFromTable);
                Node node = (Node) xpath.compile(expression1).evaluate(xmldoc, XPathConstants.NODE);
                while(node.hasChildNodes()){node.removeChild(node.getFirstChild());}//тут мы удвляем ноды
                Element newiid5 = xmldoc.createElement("Id");
                newiid5.setTextContent(Integer.toString(bookid));
                node.appendChild(newiid5);
                Element newtit5 = xmldoc.createElement("Title");
                newtit5.setTextContent(booktit);
                node.appendChild(newtit5);
                Element newauth5 = xmldoc.createElement("Author");
                newauth5.setTextContent(bookauth);
                node.appendChild(newauth5);
                Element newyeaar5 = xmldoc.createElement("Date");
                newyeaar5.setTextContent(Integer.toString(bookdat));
                node.appendChild(newyeaar5);
                refreshXML(bookFile, xmldoc);

            }
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }
}