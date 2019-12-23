package booksdb.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import booksdb.model.aBook;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import booksdb.MainApp;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RootLayoutController {

    // Reference to the main application
    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // new ставляем
    @FXML
    private void handleNew() throws IOException, SAXException, ParserConfigurationException, TransformerException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file.getPath().endsWith(".xml")) {
            file = new File(file.getPath());

            DocumentBuilderFactory dbf;
            DocumentBuilder        db ;
            Document               doc;

            dbf = DocumentBuilderFactory.newInstance();
            db  = dbf.newDocumentBuilder();
            doc = db.newDocument();
        Element newbookcatalog = doc.createElement("BookCatalogue");
        Element newbook = doc.createElement("Book");
        newbookcatalog.appendChild(newbook);
        doc.appendChild(newbookcatalog);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
            if (file != null) {
                mainApp.loabBookDataFromFile(file);
            }
        }
        else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Not correct file");
                alert.setHeaderText("Not correct file");
                alert.setContentText("Not correct file");

                alert.showAndWait();
            }
        }



    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    @FXML
    private void handleOpen() throws ParserConfigurationException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            mainApp.loabBookDataFromFile(file);
        }
    }

    @FXML
    private void handleSavexls() throws FileNotFoundException, IOException {
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet("Book Details");
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Title");
        header.createCell(2).setCellValue("Author");
        header.createCell(3).setCellValue("Date");
        aBook book_for_output;
        int out_id;
        String out_title;
        String out_author;
        int our_date;
        int pers = 0;
        ObservableList<aBook> bookDatafronmain = mainApp.getBookData();
        while (pers < bookDatafronmain.size()) {
            XSSFRow brow = sheet.createRow(pers + 1);
            book_for_output = bookDatafronmain.get(pers);
            out_id = book_for_output.getId();
            out_title = book_for_output.getTittle();
            out_author = book_for_output.getAuthor();
            our_date = book_for_output.getYear();
            brow.createCell(0).setCellValue(out_id);
            brow.createCell(1).setCellValue(out_title);
            brow.createCell(2).setCellValue(out_author);
            brow.createCell(3).setCellValue(our_date);
            pers++;
        }

        // Set extension filter
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XLSX files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());


            FileOutputStream out = new FileOutputStream(file.getPath());
            book.write(out);
            out.close();

    }
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}