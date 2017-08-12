package com.develop.android.wonap.database;

public class w_sucursales {
    private String Id;

    public String getId() { return this.Id; }

    public void setId(String Id) { this.Id = Id; }

    private String denominacion;

    public String getDenominacion() { return this.denominacion; }

    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }

    private String ciudad;

    public String getCiudad() { return this.ciudad; }

    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    private String direccion;

    public String getDireccion() { return this.direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    private String imagen_sucursal;

    public String getImagenSucursal() { return this.imagen_sucursal; }

    public void setImagenSucursal(String imagen_sucursal) { this.imagen_sucursal = imagen_sucursal; }

    private String telefono_fijo;

    public String getTelefonoFijo() { return this.telefono_fijo; }

    public void setTelefonoFijo(String telefono_fijo) { this.telefono_fijo = telefono_fijo; }

    private String celular1;

    public String getCelular1() { return this.celular1; }

    public void setCelular1(String celular1) { this.celular1 = celular1; }

    private String celular2;

    public String getCelular2() { return this.celular2; }

    public void setCelular2(String celular2) { this.celular2 = celular2; }

    private String email;

    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    private String pos_latitud;

    public String getPos_latitud() { return this.pos_latitud; }

    public void setPos_latitud(String pos_latitud) { this.pos_latitud = pos_latitud; }

    private String pos_longitud;

    public String getPos_longitud() { return this.pos_longitud; }

    public void setPos_longitud(String pos_longitud) { this.pos_longitud = pos_longitud; }

    public w_sucursales(String Id, String denominacion,
                        String ciudad, String direccion, String imagen_sucursal, String telefono_fijo, String celular1, String celular2,
                        String email, String pos_latitud, String pos_longitud) {

        this.Id = Id;
        this.denominacion = denominacion;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.imagen_sucursal = imagen_sucursal;
        this.telefono_fijo = telefono_fijo;
        this.celular1 = celular1;
        this.celular2 = celular2;
        this.email = email;
        this.pos_latitud = pos_latitud;
        this.pos_longitud = pos_longitud;

    }

}
