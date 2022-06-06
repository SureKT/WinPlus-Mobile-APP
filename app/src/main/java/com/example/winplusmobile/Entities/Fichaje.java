package com.example.winplusmobile.Entities;

import java.time.LocalDateTime;

public class Fichaje {

    private String personal;
    private LocalDateTime hora;
    private byte terminal;
    private byte funcion;
    private String tarjeta;
    private short causa;
    private byte tipo;
    private String centro;
    private short estado;
    private int refDisparo;
    private double longitud;
    private double latitud;
    private byte exportWaf;
    private short timeZoneOffset;
    private String timeZone;

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public void setHora(LocalDateTime hora) {
        this.hora = hora;
    }

    public byte getTerminal() {
        return terminal;
    }

    public void setTerminal(byte terminal) {
        this.terminal = terminal;
    }

    public byte getFuncion() {
        return funcion;
    }

    public void setFuncion(byte funcion) {
        this.funcion = funcion;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public short getCausa() {
        return causa;
    }

    public void setCausa(short causa) {
        this.causa = causa;
    }

    public byte getTipo() {
        return tipo;
    }

    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public int getRefDisparo() {
        return refDisparo;
    }

    public void setRefDisparo(int refDisparo) {
        this.refDisparo = refDisparo;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public byte getExportWaf() {
        return exportWaf;
    }

    public void setExportWaf(byte exportWaf) {
        this.exportWaf = exportWaf;
    }

    public short getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(short timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
