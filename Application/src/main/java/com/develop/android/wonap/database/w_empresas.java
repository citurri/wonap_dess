package com.develop.android.wonap.database;

/**
 * Created by Goloso on 6/7/2017.
 */

public class w_empresas {

        private String id;

        public String getId() { return this.id; }

        public void setId(String id) { this.id = id; }

        private String id_categoria;

        public String getIdCategoria() { return this.id_categoria; }

        public void setIdCategoria(String id_categoria) { this.id_categoria = id_categoria; }

        private String nombre_categoria;

        public String getNombre_categoria() { return this.nombre_categoria; }

        public void setNombre_categoria(String nombre_categoria) { this.nombre_categoria = nombre_categoria; }

        private String nombre;

        public String getNombre() { return this.nombre; }

        public void setNombre(String nombre) { this.nombre = nombre; }

        private String descripcion;

        public String getDescripcion() { return this.descripcion; }

        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }


        private String logo;

        public String getLogo() { return this.logo; }

        public void setLogo(String logo) { this.logo = logo; }

        private Boolean favorito;

        public Boolean getFavorito() { return this.favorito; }

        public void setFavorito(Boolean favorito) { this.favorito = favorito; }

    public w_empresas() {}

    public w_empresas(String id,
                      String id_categoria, String nombre_categoria, String nombre, String descripcion, String logo, Boolean favorito) {
        this.id = id;
        this.id_categoria = id_categoria;
        this.nombre_categoria = nombre_categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.logo = logo;
        this.favorito = favorito;
    }

}
