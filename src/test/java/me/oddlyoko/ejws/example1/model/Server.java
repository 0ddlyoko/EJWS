package me.oddlyoko.ejws.example1.model;

import me.oddlyoko.ejws.model.Fields;
import me.oddlyoko.ejws.model.Models;

@Models.Model(primary = { "id" })
public class Server {
    @Fields.Field
    private long id;
    @Fields.Field
    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
