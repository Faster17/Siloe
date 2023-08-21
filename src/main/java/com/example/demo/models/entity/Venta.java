package com.example.demo.models.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    private int cuotasPagadas;
    private int cuotasTotales;
    private LocalDate fecha;
    private double costo;
    private String contrato;
    
    
    
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getCuotasPagadas() {
        return cuotasPagadas;
    }


    public void setCuotasPagadas(int cuotasPagadas) {
        this.cuotasPagadas = cuotasPagadas;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public Lote getLote() {
        return lote;
    }

    
    public void setLote(Lote lote) {
        this.lote = lote;
    }


    public int getCuotasTotales() {
        return cuotasTotales;
    }


    public void setCuotasTotales(int cuotasTotales) {
        this.cuotasTotales = cuotasTotales;
    }


    public LocalDate getFecha() {
        return fecha;
    }


    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    public double getCosto() {
        return costo;
    }


    public void setCosto(double costo) {
        this.costo = costo;
    }


    public String getContrato() {
        return contrato;
    }


    public void setContrato(String contrato) {
        this.contrato = contrato;
    }


}
