package com.cadprev.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "processamentoDair")
public class ProcessamentoDairEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String cidade;
    private String uf;
    private Date dataProcessamento;

    public ProcessamentoDairEntity() {
    }

    public ProcessamentoDairEntity(String cidade, String uf) {
        this.cidade = cidade;
        this.uf = uf;
        this.dataProcessamento = new Date();
    }

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

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }
}
