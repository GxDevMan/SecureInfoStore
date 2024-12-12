package com.secinfostore.cellfactories;
import com.secinfostore.util.ImageConversion;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ImageLogoCellFactory<S> implements Callback<TableColumn<S, byte[]>, TableCell<S, byte[]>> {

    @Override
    public TableCell<S, byte[]> call(TableColumn<S, byte[]> param) {
        return new TableCell<S, byte[]>() {
            private ImageView imageLogo;

            @Override
            protected void updateItem(byte[] item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || (item == null)) {
                    setGraphic(null);
                } else {
                    Image image = ImageConversion.byteArraytoImage(item);
                    imageLogo = new ImageView();
                    imageLogo.setImage(image);
                    HBox buttonsBox = new HBox(imageLogo);
                    buttonsBox.setSpacing(10);
                    setGraphic(buttonsBox);
                }
            }
        };
    }

}
