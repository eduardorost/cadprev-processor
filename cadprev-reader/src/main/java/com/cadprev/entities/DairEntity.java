package com.cadprev.entities;

import javax.persistence.*;

@Entity(name = "dair")
public class DairEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String cidade;
    private String uf;
    @Column(columnDefinition="TEXT")
    private String infosEnte;
    @Column(columnDefinition="TEXT")
    private String infosRepresentanteEnte;
    @Column(columnDefinition="TEXT")
    private String infosUG;
    @Column(columnDefinition="TEXT")
    private String infosRUG;
    private String totalRecursosRPPS;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "forma_gestao_assessoramento_id")
    private FormaGestaoAssessoramentoEntity formaGestaoAssessoramentoEntity;


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

    public FormaGestaoAssessoramentoEntity getFormaGestaoAssessoramentoEntity() {
        return formaGestaoAssessoramentoEntity;
    }

    public void setFormaGestaoAssessoramentoEntity(FormaGestaoAssessoramentoEntity formaGestaoAssessoramentoEntity) {
        this.formaGestaoAssessoramentoEntity = formaGestaoAssessoramentoEntity;
    }
}
