package com.cadprev.entities;

import javax.persistence.*;

@Entity(name = "erro")
public class ErroEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String arquivo;
    @Column(columnDefinition="TEXT")
    private String erro;
    @Column(columnDefinition="TEXT")
    private String stacktrace;

    public ErroEntity(String arquivo, String erro, String stacktrace) {
        this.arquivo = arquivo;
        this.erro = erro;
        this.stacktrace = stacktrace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
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
