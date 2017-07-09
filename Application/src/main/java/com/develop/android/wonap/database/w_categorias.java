package com.develop.android.wonap.database;

/**
 * Created by Goloso on 6/7/2017.
 */

public class w_categorias {

        private String id;

        public String getId() { return this.id; }

        public void setId(String id) { this.id = id; }

        private String codigo;

        public String getCodigo() { return this.codigo; }

        public void setCodigo(String codigo) { this.codigo = codigo; }

        private String nombre;

        public String getNombre() { return this.nombre; }

        public void setNombre(String nombre) { this.nombre = nombre; }

        private String estado;

        public String getEstado() { return this.estado; }

        public void setEstado(String estado) { this.estado = estado; }

    public w_categorias() {}

    public w_categorias(String id,
                        String codigo, String nombre, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
    }

}
