package com.cadprev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DAIRService {

    @Value("${consultas-publicas}")
    private String consultasPublicas;
    @Value("${aplicacoes}")
    private String aplicacoes;
    @Value("${consultas-demonstrativos}")
    private String consultasDemonstrativos;
    @Value("${cidade}")
    private String cidade;
    @Value("${encerramento-mes}")
    private String encerramentoMes;
    @Value("${operacoes}")
    private String operacoes;
    @Value("${intermediario}")
    private String intermediario;
    @Value("${consultar}")
    private String consultar;
    @Value("${unidade-federativa}")
    private String unidadeFederativa;

    @Autowired
    private SeleniumService seleniumService;

    public List<String> getCidades() {
        return seleniumService.getAllOptions(cidade);
    }

    public void getEstado(String uf) {
        seleniumService.getDropdown(unidadeFederativa).selectByVisibleText(uf);
    }

    public List<String> getEstados() {
        return seleniumService.getAllOptions(unidadeFederativa);
    }

    public void openDemonstrativosDAIR() {
        seleniumService.clickElement(consultasPublicas);
        seleniumService.clickElement(aplicacoes);
        seleniumService.clickElement(consultasDemonstrativos);
    }

    public void consultar(String cidade) {
        seleniumService.selectDropdown(this.cidade, cidade);
        seleniumService.verifyCheckbox(encerramentoMes, true);
        seleniumService.verifyCheckbox(operacoes, false);
        seleniumService.verifyCheckbox(intermediario, false);
        seleniumService.clickElement(consultar);
    }

}
