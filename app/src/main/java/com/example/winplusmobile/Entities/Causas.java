package com.example.winplusmobile.Entities;

public class Causas {

    public short codigo;
    public String descripcion;
    public byte aprobar;
    public byte solicitud;
    public String referencia;
    public byte tweb;
    public byte calculodiasnaturales;
    public String pldocs;

    public short getCodigo() {
        return codigo;
    }

    public void setCodigo(short codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getAprobar() {
        return aprobar;
    }

    public void setAprobar(byte aprobar) {
        this.aprobar = aprobar;
    }

    public byte getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(byte solicitud) {
        this.solicitud = solicitud;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public byte getTweb() {
        return tweb;
    }

    public void setTweb(byte tweb) {
        this.tweb = tweb;
    }

    public byte getCalculodiasnaturales() {
        return calculodiasnaturales;
    }

    public void setCalculodiasnaturales(byte calculodiasnaturales) {
        this.calculodiasnaturales = calculodiasnaturales;
    }

    public String getPldocs() {
        return pldocs;
    }

    public void setPldocs(String pldocs) {
        this.pldocs = pldocs;
    }
}
