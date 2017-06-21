package window.controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import model.Map;
import window.ViewContext;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Souverain73 on 21.06.2017.
 */
public class SavePdfDialogController {
    private Map map;
    private File file;

    @FXML
    private Pane rootPane;

    @FXML
    private Label valueLabel;

    @FXML
    private Slider valueSlider;

    private int scaleValue=100;

    @FXML
    public void initialize(){
        valueLabel.setText(formatValue((int)valueSlider.getValue()));
        valueSlider.valueProperty().addListener((observable, oldValue, newValue) ->{
                valueLabel.setText(formatValue(newValue.intValue()));
                scaleValue = newValue.intValue();
        });
    }

    public void setInitialParams(Map map, File file){
        this.map = map;
        this.file = file;
    }

    @FXML
    private void okAction() throws Exception{
        Document doc = new Document();
        doc.setPageSize(new Rectangle((float)map.getWidth(), (float)map.getHeight()));
        doc.setMargins(0,0,0,0);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        ViewContext vc = new ViewContext();
        vc.setObjectsScale(((double)scaleValue)/100);
        map.SerializeToPDF(doc, writer, vc);
        doc.close();
        writer.close();
        close();
    }

    @FXML
    private void cancelAction(){
        close();
    }

    private void close(){
        rootPane.getScene().getWindow().hide();
    }

    private String formatValue(int value){
        return String.format("%d%%", value);
    }
}
