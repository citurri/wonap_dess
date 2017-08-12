package com.develop.android.wonap.database;

/**
 * Created by Goloso on 20/7/2017.
 */

public class w_contactos {
    private String direccion;

    public String getDireccion() { return this.direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    private String nombre;

    public String getNombre() { return this.nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    private String email;

    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    private String telefono_fijo;

    public String getTelefonoFijo() { return this.telefono_fijo; }

    public void setTelefonoFijo(String telefono_fijo) { this.telefono_fijo = telefono_fijo; }

    private String telefono_movil;

    public String getTelefonoMovil() { return this.telefono_movil; }

    public void setTelefonoMovil(String telefono_movil) { this.telefono_movil = telefono_movil; }

    public w_contactos(String direccion,
                    String nombre, String email, String telefono_fijo, String telefono_movil) {

        this.direccion = direccion;
        this.nombre = nombre;
        this.email = email;
        this.telefono_fijo = telefono_fijo;
        this.telefono_movil = telefono_movil;

    }

}
