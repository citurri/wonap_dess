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

    private String cupones_habilitados;

    public String getCupones_habilitados() { return this.cupones_habilitados; }

    public void setCupones_habilitados(String cupones_habilitados) { this.cupones_habilitados = cupones_habilitados; }

    private String cupones_redimidos;

    public String getCupones_redimidos() { return this.cupones_redimidos; }

    public void setCupones_redimidos(String cupones_redimidos) { this.cupones_redimidos = cupones_redimidos; }

    private boolean cupon_permitido;

    public boolean getCupon_permitido() { return this.cupon_permitido; }

    public void setCupon_permitido(boolean cupon_permitido) { this.cupon_permitido = cupon_permitido; }

    private String dias_restantes;

    public String getDiasRestantes() { return this.dias_restantes; }

    public void setDiasRestantes(String dias_restantes) { this.dias_restantes = dias_restantes; }

    private boolean es_favorito;

    public boolean getEsFavorito() { return this.es_favorito; }

    public void setEsFavorito(boolean es_favorito) { this.es_favorito = es_favorito; }

    private String secundarias_oferta;

    public String getSecundariasOferta() { return this.secundarias_oferta; }

    public void setSecundariasOferta(String secundarias_oferta) { this.secundarias_oferta = secundarias_oferta; }

    public OfertaModel() {}

    public OfertaModel(String id, String id_empresa, String nombre_empresa, String titulo,
                      String descripcion, String imagen_oferta,boolean es_cupon,String fecha_inicio, String fecha_fin,
                      String denominacion, String pos_latitud,String pos_longitud,String pos_map_address, String pos_map_city,
                      String pos_map_country, String distancia_user, String cupones_habilitados,String cupones_redimidos,boolean cupon_permitido,
                       String dias_restantes, boolean es_favorito, String secundarias_oferta) {

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
        this.cupones_habilitados = cupones_habilitados;
        this.cupones_redimidos = cupones_redimidos;
        this.cupon_permitido = cupon_permitido;
        this.dias_restantes = dias_restantes;
        this.es_favorito = es_favorito;
        this.secundarias_oferta = secundarias_oferta;
    }

}
