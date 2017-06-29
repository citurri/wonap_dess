package com.develop.android.wonap.database;

import java.io.Serializable;

/**
 * Created by Goloso on 22/6/2017.
 */

public class NoticiasModel implements Serializable
{
    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    private String id_empresa;

    public String getIdEmpresa() { return this.id_empresa; }

    public void setIdEmpresa(String id_empresa) { this.id_empresa = id_empresa; }

    private String nombre_empresa;

    public String getNombreEmpresa() { return this.nombre_empresa; }

    public void setNombreEmpresa(String nombre_empresa) { this.nombre_empresa = nombre_empresa; }

    private String titulo;

    public String getTitulo() { return this.titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    private String descripcion;

    public String getDescripcion() { return this.descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    private String imagen_noticia;

    public String getImagenNoticia() { return this.imagen_noticia; }

    public void setImagenNoticia(String imagen_noticia) { this.imagen_noticia = imagen_noticia; }

    private String fecha_publicacion;

    public String getFechaPublicacion() { return this.fecha_publicacion; }

    public void setFechaPublicacion(String fecha_publicacion) { this.fecha_publicacion = fecha_publicacion; }

    private Boolean es_favorito;

    public Boolean getEsFavorito() { return this.es_favorito; }

    public void setEsFavorito(Boolean es_favorito) { this.es_favorito = es_favorito; }


    public NoticiasModel() {}

    public NoticiasModel(String id, String id_empresa, String nombre_empresa, String titulo,
                         String descripcion, String imagen_noticia, boolean es_favorito, String fecha_publicacion) {

        this.id = id;
        this.id_empresa = id_empresa;
        this.nombre_empresa = nombre_empresa;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen_noticia = imagen_noticia;
        this.es_favorito = es_favorito;
        this.fecha_publicacion = fecha_publicacion;

    }

}
