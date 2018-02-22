package com.cadprev.services;

import com.cadprev.entities.ProcessamentoDairEntity;
import com.cadprev.entities.ProcessamentoErroEntity;
import com.cadprev.repositories.ProcessamentoDairRepository;
import com.cadprev.repositories.ProcessamentoErroRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProcessService {

    static Logger log = Logger.getLogger(ProcessService.class);

    @Value("${url-cadprev}")
    private String urlCadprev;
    @Value("${download}")
    private String download;

    @Autowired
    private SeleniumService seleniumService;
    @Autowired
    private DAIRService dairService;
    @Autowired
    private ProcessamentoDairRepository processamentoDairRepository;
    @Autowired
    private ProcessamentoErroRepository processamentoErroRepository;
    @Autowired
    private FileService fileService;

    public void run() {
        try {
            seleniumService.get(urlCadprev);

            dairService.openDemonstrativosDAIR();
            downloadAllDAIRsUnidadeFederativa();
        } catch (Exception e) {
            log.info("erro na aplicação", e);
            try {
                seleniumService.close();
                Thread.sleep(60000);
            } catch (Exception e1) {
                log.info("erro ao fechar o driver", e1);
            }
            seleniumService.restart();
            run();
        }
    }

    public void clearDB() {
        processamentoDairRepository.deleteAll();
        processamentoErroRepository.deleteAll();
    }

    private void downloadAllDAIRsUnidadeFederativa() {
        List<String> estadosExecutados = processamentoDairRepository.findAllUF();
        if(estadosExecutados.size() > 0)
            estadosExecutados.remove(estadosExecutados.size() - 1);

        dairService.getEstados().forEach(uf -> {
            if(estadosExecutados.contains(uf))
                return;

            dairService.getEstado(uf);

            List<String> cidadesExecutadas = processamentoDairRepository.findAllCidadesBYUF(uf);
            List<String> cidades = dairService.getCidades();
            if(cidades.size() < 1) {
                log.info("Não carregou as cidades");
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                run();
            }

            cidades.removeAll(cidadesExecutadas);
            cidades.forEach(cidade -> downloadDAIRCidade(cidade, uf));
        });
    }

    public void downloadDAIRCidade(String cidade, String uf) {
        dairService.consultar(cidade);

        try {
            seleniumService.clickElement(download);
            fileService.renameFile(uf, cidade);
        } catch (NoSuchElementException ex) {
            log.info(String.format("cidade %s estado %s não tem arquivo disponível", cidade, uf));
            processamentoErroRepository.save(new ProcessamentoErroEntity(uf, cidade, String.format("cidade %s estado %s não tem arquivo disponível", cidade, uf), ExceptionUtils.getStackTrace(ex)));
        } catch (IOException e) {
            log.info(String.format("erro ao renomear arquivo cidade %s estado %s", cidade, uf), e);
            processamentoErroRepository.save(new ProcessamentoErroEntity(uf, cidade, String.format("erro ao renomear arquivo cidade %s estado %s", cidade, uf), ExceptionUtils.getStackTrace(e)));
        } catch (Exception ex) {
            log.info(String.format("erro ao processar cidade %s estado %s", cidade, uf));
            processamentoErroRepository.save(new ProcessamentoErroEntity(uf, cidade, ex.getMessage(), ExceptionUtils.getStackTrace(ex)));
        }

        processamentoDairRepository.save(new ProcessamentoDairEntity(cidade, uf));
    }
}
