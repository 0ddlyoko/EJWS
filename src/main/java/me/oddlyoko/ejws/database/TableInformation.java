package me.oddlyoko.ejws.database;

import java.sql.Types;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TableInformation {
    private String name;
    private List<ColumnInformation> columns;

    public TableInformation(String name, List<ColumnInformation> columns) {
        this.name = name;
        this.columns = columns;
    }

    @Getter
    @AllArgsConstructor
    public static class ColumnInformation {
        private String name;
        private int type;
        private boolean nullable;
        private String defaultValue;
        private boolean autoIncrement;
    }
}
