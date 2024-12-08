package com.secinfostore.secureinfostore.cellfactories;

import com.secinfostore.secureinfostore.util.TimeFormatter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.sql.Timestamp;

public class TimeStampChangeLogCellFactory<T> implements Callback<TableColumn<T, Timestamp>, TableCell<T, Timestamp>> {
    @Override
    public TableCell<T, Timestamp> call(TableColumn<T, Timestamp> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String timeDate = String.format("%s %s",
                            TimeFormatter.getFormattedDate(item),
                            TimeFormatter.formatTime(item));
                    setText(timeDate);
                }
            }
        };
    }
}
