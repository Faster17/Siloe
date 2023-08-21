package com.example.demo.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int indice;
    private String estado = "Disponible";

    @ManyToOne
    @JoinColumn(name = "tipo_lote_id")
    private TipoLote tipoLote;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public TipoLote getTipoLote() {
        return tipoLote;
    }

    public void setTipoLote(TipoLote tipoLote) {
        this.tipoLote = tipoLote;
    }

    @Override
    public String toString() {
        return "Lote [id=" + id + ", indice=" + indice + ", estado=" + estado + ", tipoLote=" + tipoLote + "]";
    }

    
}
