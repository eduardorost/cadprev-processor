package com.cadprev.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "dair")
public class DairEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String cidade;
    private String uf;
    private String infosEnte;
    private String infosRepresentanteEnte;
    private String infosUG;
    private String infosRUG;
    private String totalRecursosRPPS;

    //private FormaGestaoAssessoramentoEntity formaGestaoAssessoramentoEntity;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getInfosEnte() {
        return infosEnte;
    }

    public void setInfosEnte(String infosEnte) {
        this.infosEnte = infosEnte;
    }

    public String getInfosRepresentanteEnte() {
        return infosRepresentanteEnte;
    }

    public void setInfosRepresentanteEnte(String infosRepresentanteEnte) {
        this.infosRepresentanteEnte = infosRepresentanteEnte;
    }

    public String getInfosUG() {
        return infosUG;
    }

    public void setInfosUG(String infosUG) {
        this.infosUG = infosUG;
    }

    public String getInfosRUG() {
        return infosRUG;
    }

    public void setInfosRUG(String infosRUG) {
        this.infosRUG = infosRUG;
    }

    public String getTotalRecursosRPPS() {
        return totalRecursosRPPS;
    }

    public void setTotalRecursosRPPS(String totalRecursosRPPS) {
        this.totalRecursosRPPS = totalRecursosRPPS;
    }
}
