package com.cadprev.reader.email;

public class Email {

    private String uf;
    private String cidade;
    private String emailRUGs;
    private String emailRepresentantesEntes;

    public Email(String uf, String cidade, String emailRUGs, String emailRepresentantesEntes) {
        this.uf = uf;
        this.cidade = cidade;
        this.emailRUGs = emailRUGs;
        this.emailRepresentantesEntes = emailRepresentantesEntes;
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

    public String getEmailRUGs() {
        return emailRUGs;
    }

    public void setEmailRUGs(String emailRUGs) {
        this.emailRUGs = emailRUGs;
    }

    public String getEmailRepresentantesEntes() {
        return emailRepresentantesEntes;
    }

    public void setEmailRepresentantesEntes(String emailRepresentantesEntes) {
        this.emailRepresentantesEntes = emailRepresentantesEntes;
    }
}
