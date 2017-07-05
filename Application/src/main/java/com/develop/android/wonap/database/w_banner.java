package com.develop.android.wonap.database;

public class w_banner {

    public String id;
    public String imagen;


    public w_banner() {}

    public w_banner(String id,
                    String imagen) {

        this.id = id;
        this.imagen = imagen;

    }


    public String getId() {
        return id;
    }
    public String getImagen() {
        return imagen;
    }

}
