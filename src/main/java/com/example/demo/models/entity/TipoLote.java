package com.example.demo.models.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class TipoLote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tamano;
    private double precio;
    private int loteInicial;
    private int loteFinal;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    @OneToMany(mappedBy = "tipoLote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lote> lotes = new ArrayList<>();
    

    public Long getId() {
        return id;
    }



    public void setId(Long id) {
        this.id = id;
    }



    public String getTamano() {
        return tamano;
    }



    public void setTamano(String tamano) {
        this.tamano = tamano;
    }



    public double getPrecio() {
        return precio;
    }



    public void setPrecio(double precio) {
        this.precio = precio;
    }



    public int getLoteInicial() {
        return loteInicial;
    }



    public void setLoteInicial(int loteInicial) {
        this.loteInicial = loteInicial;
    }



    public int getLoteFinal() {
        return loteFinal;
    }



    public void setLoteFinal(int loteFinal) {
        this.loteFinal = loteFinal;
    }


    public Proyecto getProyecto() {
        return proyecto;
    }



    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }



    @Override
    public String toString() {
        return "TipoLote [id=" + id + ", tamano=" + tamano +
                ", precio=" + precio + ", loteInicial=" + loteInicial +
                ", loteFinal=" + loteFinal + "]";
    }



    public List<Lote> getLotes() {
        return lotes;
    }



    public void setLotes(List<Lote> lotes) {
        this.lotes = lotes;
    }
}
