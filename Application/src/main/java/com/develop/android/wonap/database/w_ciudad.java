package com.develop.android.wonap.database;

/**
 * Created by Goloso on 6/7/2017.
 */

public class w_ciudad {

        private String id;

        public String getId() { return this.id; }

        public void setId(String id) { this.id = id; }

        private String pais;

        public String getPais() { return this.pais; }

        public void setPais(String pais) { this.pais = pais; }

        private String nombre;

        public String getNombre() { return this.nombre; }

        public void setNombre(String nombre) { this.nombre = nombre; }

        private String latitud;

        public String getLatitud() { return this.latitud; }

        public void setLatitud(String latitud) { this.latitud = latitud; }

        private String longitud;

        public String getLongitud() { return this.longitud; }

        public void setLongitud(String longitud) { this.longitud = longitud; }

    public w_ciudad(String id,
                                 String pais,String nombre, String latitud, String longitud) {
        this.id = id;
        this.pais = pais;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

}
