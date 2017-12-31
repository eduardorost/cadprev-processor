package com.cadprev.entities;


import javax.persistence.*;

@Entity(name = "forma_gestao_assessoramento")
public class FormaGestaoAssessoramentoEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Integer prazoVigencia;
    private String dataAssinaturaContrato;
    private String dataFinalContrato;
    private String razaoSocial;
    private String valorContratualMensal;
    @Column(columnDefinition="TEXT")
    private String detalhes;

    @OneToOne(mappedBy = "formaGestaoAssessoramentoEntity")
    private DairEntity dairEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrazoVigencia(Integer prazoVigencia) {
        this.prazoVigencia = prazoVigencia;
    }

    public Integer getPrazoVigencia() {
        return prazoVigencia;
    }

    public void setDataAssinaturaContrato(String dataAssinaturaContrato) {
        this.dataAssinaturaContrato = dataAssinaturaContrato;
    }

    public String getDataAssinaturaContrato() {
        return dataAssinaturaContrato;
    }

    public void setDataFinalContrato(String dataFinalContrato) {
        this.dataFinalContrato = dataFinalContrato;
    }

    public String getDataFinalContrato() {
        return dataFinalContrato;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setValorContratualMensal(String valorContratualMensal) {
        this.valorContratualMensal = valorContratualMensal;
    }

    public String getValorContratualMensal() {
        return valorContratualMensal;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getDetalhes() {
        return detalhes;
    }
}
