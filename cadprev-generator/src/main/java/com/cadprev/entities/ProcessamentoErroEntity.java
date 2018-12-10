package com.cadprev.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "processamento_erro")
public class ProcessamentoErroEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String uf;
    private String cidade;
    @Column(columnDefinition="TEXT")
    private String erro;
    @Column(columnDefinition="TEXT")
    private String stacktrace;

    public ProcessamentoErroEntity() {
    }

    public ProcessamentoErroEntity(String uf, String cidade, String erro, String stacktrace) {
        this.uf = uf;
        this.cidade = cidade;
        this.erro = erro;
        this.stacktrace = stacktrace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }
}
