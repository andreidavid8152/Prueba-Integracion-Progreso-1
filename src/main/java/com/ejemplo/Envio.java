package com.ejemplo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Envio {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("cliente")
    private String cliente;
    
    @JsonProperty("direccion")
    private String direccion;
    
    @JsonProperty("estado")
    private String estado;

    // Constructores, Getters y Setters
    public Envio() {
        // Constructor vacío necesario para Jackson
    }

    public Envio(String id, String cliente, String direccion, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.direccion = direccion;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Envio{" +
                "id='" + id + '\'' +
                ", cliente='" + cliente + '\'' +
                '}';
    }
}