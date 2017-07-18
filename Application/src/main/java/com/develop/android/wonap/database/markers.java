package com.develop.android.wonap.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class markers extends ArrayList<Parcelable> implements Parcelable {


    public String empresa;

    protected markers(Parcel in) {
        empresa = in.readString();
        direccion = in.readString();
        latitud = in.readString();
        longitud = in.readString();
    }

    public static final Creator<markers> CREATOR = new Creator<markers>() {
        @Override
        public markers createFromParcel(Parcel in) {
            return new markers(in);
        }

        @Override
        public markers[] newArray(int size) {
            return new markers[size];
        }
    };

    public String getEmpresa() { return this.empresa; }

    public void setEmpresa(String empresa) { this.empresa = empresa; }

    private String direccion;

    public String getDireccion() { return this.direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    private String latitud;

    public String getLatitud() { return this.latitud; }

    public void setLatitud(String latitud) { this.latitud = latitud; }

    private String longitud;

    public String getLongitud() { return this.longitud; }

    public void setLongitud(String longitud) { this.longitud = longitud; }



    public markers(String empresa,String direccion, String latitud, String longitud) {
        this.empresa = empresa;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(empresa);
        parcel.writeString(direccion);
        parcel.writeString(latitud);
        parcel.writeString(longitud);
    }
}
