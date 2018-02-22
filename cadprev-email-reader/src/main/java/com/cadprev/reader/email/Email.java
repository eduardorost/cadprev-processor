package com.cadprev.reader.email;

public class Email {

    private String uf;
    private String cidade;
    private String emailRUG;
    private String emailRepresentantesEnte;

    public Email(String uf, String cidade, String emailRUG, String emailRepresentantesEnte) {
        this.uf = uf;
        this.cidade = cidade;
        this.emailRUG = emailRUG;
        this.emailRepresentantesEnte = emailRepresentantesEnte;
    }

    @Override
    public String toString() {
        return uf + ";" + cidade + ";" + emailRUG + ";" + emailRepresentantesEnte;
    }
}
