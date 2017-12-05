package com.cadprev.reader.email;

public class Email {

    private String uf;
    private String cidade;
    private String email;

    public Email(String uf, String cidade, String email) {
        this.uf = uf;
        this.cidade = cidade;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s",uf,cidade,email);
    }
}
