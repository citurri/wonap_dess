package com.develop.android.wonap.database;

public class w_moferta_comentarios {

    public String id;
    public String id_oferta;
    public String id_user;
    public String mensaje;
    public String fecha;
    public String id_empresa;
    public String imagen_user;
    public String imagen_empresa;
    public String tipo_remitente;
    public String nombre_user;
    public String nombre_empresa;


    public w_moferta_comentarios() {}

    public w_moferta_comentarios(String id,
                           String id_oferta,String id_user, String mensaje, String fecha , String id_empresa, String imagen_user,
                                 String imagen_empresa, String tipo_remitente, String nombre_user, String nombre_empresa) {

        this.id = id;
        this.id_oferta = id_oferta;
        this.id_user = id_user;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.id_empresa = id_empresa;
        this.imagen_user = imagen_user;
        this.imagen_empresa = imagen_empresa;
        this.tipo_remitente = tipo_remitente;
        this.nombre_user = nombre_user;
        this.nombre_empresa = nombre_empresa;
    }


    public String getMensaje() {
        return mensaje;
    }

}
