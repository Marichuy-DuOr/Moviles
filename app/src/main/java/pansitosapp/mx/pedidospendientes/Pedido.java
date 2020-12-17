package pansitosapp.mx.pedidospendientes;

import java.io.Serializable;

public class Pedido implements Serializable { //Objeto pan
    Integer id;
    Integer id_usuario;
    String nombre;
    String fecha;
    Float total;
    Double lat;
    Double lon;
    Integer estado;

    public Pedido(Integer id, Integer id_usuario, String nombre, String fecha, Float total, Double lat, Double lon, Integer estado) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.fecha = fecha;
        this.total = total;
        this.lat = lat;
        this.lon = lon;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getEstado() { return estado; }

    public void setEstado(Integer estado) { this.estado = estado; }
}
