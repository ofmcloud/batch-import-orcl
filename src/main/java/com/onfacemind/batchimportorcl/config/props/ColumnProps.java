package com.onfacemind.batchimportorcl.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "population.column")
public class ColumnProps {

    private String table;

    private String face;

    private String name;

    private String idcard;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    @Override
    public String toString() {
        return "ColumnProps{" +
                "table='" + table + '\'' +
                ", face='" + face + '\'' +
                ", name='" + name + '\'' +
                ", idcard='" + idcard + '\'' +
                '}';
    }
}
