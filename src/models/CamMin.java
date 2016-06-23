package models;

import java.io.Serializable;

public class CamMin implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private String name;

    private String adserver;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdserver() {
        return adserver;
    }

    public void setAdserver(String adserver) {
        this.adserver = adserver;
    }

}
