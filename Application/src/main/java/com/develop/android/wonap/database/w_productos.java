package com.develop.android.wonap.database;

/**
 * Created by Goloso on 12/8/2017.
 */

public class w_productos
{
    private String id;

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    private String nombre;

    public String getNombre() { return this.nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    private String descripcion;

    public String getDescripcion() { return this.descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    private String imagen_producto;

    public String getImagenProducto() { return this.imagen_producto; }

    public void setImagenProducto(String imagen_producto) { this.imagen_producto = imagen_producto; }

    private String unidad;

    public String getUnidad() { return this.unidad; }

    public void setUnidad(String unidad) { this.unidad = unidad; }

    private String precio;

    public String getPrecio() { return this.precio; }

    public void setPrecio(String precio) { this.precio = precio; }

    private String marca;

    public String getMarca() { return this.marca; }

    public void setMarca(String marca) { this.marca = marca; }

    private String talla;

    public String getTalla() { return this.talla; }

    public void setTalla(String talla) { this.talla = talla; }

    private String dimension;

    public String getDimension() { return this.dimension; }

    public void setDimension(String dimension) { this.dimension = dimension; }

    public w_productos(String Id, String nombre, String descripcion,
                        String imagen_producto, String unidad, String precio, String marca, String talla, String dimension) {

        this.id = Id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen_producto = imagen_producto;
        this.unidad = unidad;
        this.precio = precio;
        this.marca = marca;
        this.talla = talla;
        this.dimension = dimension;
    }


}

