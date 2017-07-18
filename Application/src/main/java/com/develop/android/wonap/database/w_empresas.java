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


        private String historia;

        public String getHistoria() { return this.historia; }

        public void setHistoria(String historia) { this.historia = historia; }


        private String valores;

        public String getValores() { return this.valores; }

        public void setValores(String valores) { this.valores = valores; }


        private boolean not_noticias;

        public boolean getNotNoticias() { return this.not_noticias; }

        public void setNotNoticias(boolean not_noticias) { this.not_noticias = not_noticias; }


        private boolean not_ofertas;

        public boolean getNotOfertas() { return this.not_ofertas; }

        public void setNotOfertas(boolean not_ofertas) { this.not_ofertas = not_ofertas; }


        private String seguidores;

        public String getSeguidores() { return this.seguidores; }

        public void setSeguidores(String seguidores) { this.seguidores = seguidores; }


        private String direccion;

        public String getDireccion() { return this.direccion; }

        public void setDireccion(String direccion) { this.direccion = direccion; }


        private String latitud;

        public String getLatitud() { return this.latitud; }

        public void setLatitud(String latitud) { this.latitud = latitud; }


        private String longitud;

        public String getLongitud() { return this.longitud; }

        public void setLongitud(String longitud) { this.longitud = longitud; }



    public w_empresas() {}

    public w_empresas(String id,
                      String id_categoria, String nombre_categoria, String nombre, String descripcion, String logo, Boolean favorito, String historia,
                      String valores, Boolean not_noticias, Boolean not_ofertas, String seguidores) {
        this.id = id;
        this.id_categoria = id_categoria;
        this.nombre_categoria = nombre_categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.logo = logo;
        this.favorito = favorito;

        this.historia = historia;
        this.valores = valores;
        this.not_noticias = not_noticias;
        this.not_ofertas = not_ofertas;
        this.seguidores = seguidores;
    }

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

    public w_empresas(String id,
                      String id_categoria, String nombre_categoria, String nombre, String descripcion, String logo, Boolean favorito , String direccion,
                      String latitud, String longitud) {
        this.id = id;
        this.id_categoria = id_categoria;
        this.nombre_categoria = nombre_categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.logo = logo;
        this.favorito = favorito;

        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
