package com.develop.android.wonap.database;

import java.io.Serializable;

/**
 * Created by Goloso on 22/6/2017.
 */

public class OfertaModel implements Serializable
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

    private String imagen_oferta;

    public String getImagenOferta() { return this.imagen_oferta; }

    public void setImagenOferta(String imagen_oferta) { this.imagen_oferta = imagen_oferta; }

    private boolean es_cupon;

    public boolean getEsCupon() { return this.es_cupon; }

    public void setEsCupon(boolean es_cupon) { this.es_cupon = es_cupon; }

    private String fecha_inicio;

    public String getFechaInicio() { return this.fecha_inicio; }

    public void setFechaInicio(String fecha_inicio) { this.fecha_inicio = fecha_inicio; }

    private String fecha_fin;

    public String getFechaFin() { return this.fecha_fin; }

    public void setFechaFin(String fecha_fin) { this.fecha_fin = fecha_fin; }

    private String denominacion;

    public String getDenominacion() { return this.denominacion; }

    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }

    private String pos_latitud;

    public String getPosLatitud() { return this.pos_latitud; }

    public void setPosLatitud(String pos_latitud) { this.pos_latitud = pos_latitud; }

    private String pos_longitud;

    public String getPosLongitud() { return this.pos_longitud; }

    public void setPosLongitud(String pos_longitud) { this.pos_longitud = pos_longitud; }

    private String pos_map_address;

    public String getPosMapAddress() { return this.pos_map_address; }

    public void setPosMapAddress(String pos_map_address) { this.pos_map_address = pos_map_address; }

    private String pos_map_city;

    public String getPosMapCity() { return this.pos_map_city; }

    public void setPosMapCity(String pos_map_city) { this.pos_map_city = pos_map_city; }

    private String pos_map_country;

    public String getPosMapCountry() { return this.pos_map_country; }

    public void setPosMapCountry(String pos_map_country) { this.pos_map_country = pos_map_country; }

    private String distancia_user;

    public String getDistanciaUser() { return this.distancia_user; }

    public void setDistanciaUser(String distancia_user) { this.distancia_user = distancia_user; }

    public OfertaModel() {}

    public OfertaModel(String id, String id_empresa, String nombre_empresa, String titulo,
                      String descripcion, String imagen_oferta,boolean es_cupon,String fecha_inicio, String fecha_fin,
                      String denominacion, String pos_latitud,String pos_longitud,String pos_map_address, String pos_map_city,
                      String pos_map_country, String distancia_user) {

        this.id = id;
        this.id_empresa = id_empresa;
        this.nombre_empresa = nombre_empresa;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen_oferta = imagen_oferta;
        this.es_cupon = es_cupon;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.denominacion = denominacion;
        this.pos_latitud = pos_latitud;
        this.pos_longitud = pos_longitud;
        this.pos_map_address = pos_map_address;
        this.pos_map_city = pos_map_city;
        this.pos_map_country = pos_map_country;
        this.distancia_user = distancia_user;
    }

}
