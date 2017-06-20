package helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Souverain73 on 20.06.2017.
 */
public class JFX {
    public static Stage createDialog(String fxml, Modality modality) throws Exception{
        Stage dialog = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(loader.getClass().getResource("/fxml/MainWindow.fxml"));
        dialog.setScene(new Scene(root));
        dialog.initModality(modality);
        return dialog;
    }
}
