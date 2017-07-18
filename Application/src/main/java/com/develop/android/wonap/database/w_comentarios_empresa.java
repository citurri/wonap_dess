package com.develop.android.wonap.database;

/**
 * Created by Goloso on 15/7/2017.
 */

public class w_comentarios_empresa {
    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    private String empresa;

    public String getEmpresa() { return this.empresa; }

    public void setEmpresa(String empresa) { this.empresa = empresa; }

    private String usuario;

    public String getUsuario() { return this.usuario; }

    public void setUsuario(String usuario) { this.usuario = usuario; }

    private String mensaje;

    public String getMensaje() { return this.mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    private String estado;

    public String getEstado() { return this.estado; }

    public void setEstado(String estado) { this.estado = estado; }

    private String fecha_registro;

    public String getFechaRegistro() { return this.fecha_registro; }

    public void setFechaRegistro(String fecha_registro) { this.fecha_registro = fecha_registro; }

    public String tipo_remitente;

    public String getTipoRemitente() { return this.tipo_remitente; }

    public void setTipoRemitente(String tipo_remitente) { this.tipo_remitente = tipo_remitente; }

    public String imagen_user;

    public String getImagenUser() { return this.imagen_user; }

    public void setImagenUser(String imagen_user) { this.imagen_user = imagen_user; }

    public String imagen_empresa;

    public String getImagenEmpresa() { return this.imagen_empresa; }

    public void setImagenEmpresa(String imagen_empresa) { this.imagen_empresa = imagen_empresa; }

    public w_comentarios_empresa(String id,
                    String empresa,String usuario, String mensaje, String estado , String fecha_registro, String tipo_remitente,
                                 String imagen_user, String imagen_empresa) {
        this.id = id;
        this.empresa = empresa;
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.estado = estado;
        this.fecha_registro = fecha_registro;
        this.tipo_remitente = tipo_remitente;
        this.imagen_user = imagen_user;
        this.imagen_empresa = imagen_empresa;
    }
}
