package com.cadprev;

import com.cadprev.entities.ProcessamentoDairEntity;
import com.cadprev.entities.ProcessamentoErroEntity;
import com.cadprev.services.SeleniumService;
import com.cadprev.repositories.ProcessamentoDairRepository;
import com.cadprev.repositories.ProcessamentoErroRepository;
import com.google.common.io.Files;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class CadprevGeneratorApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevGeneratorApplication.class);

	private static final String FOLDER_DOWNLOAD = "/home/eduardorost/Downloads";
	private static final String FOLDER_DOWNLOAD_DAIR = FOLDER_DOWNLOAD + "/DAIR/";
	private static final String URL_CADPREV = "http://cadprev.previdencia.gov.br/Cadprev/faces/pages/index.xhtml";
	private static final String CONSULTAS_PUBLICAS = "//*[@id=\"udm\"]/li[2]";
	private static final String APLICACOES = "//*[@id=\"udm\"]/li[2]/ul/li[2]/a/label";
	private static final String CONSULTAS_DEMONSTRATIVOS = "//*[@id=\"udm\"]/li[2]/ul/li[2]/ul/li[2]/a/label";
	private static final String UNIDADE_FEDERATIVA = "//*[@id=\"form:unidadeFederativa\"]";
	private static final String CIDADE = "//*[@id=\"form:ente\"]";
	private static final String ENCERRAMENTO_MES = "//*[@id=\"form:finalidadeDAIR:0\"]";
	private static final String OPERACOES = "//*[@id=\"form:finalidadeDAIR:1\"]";
	private static final String INTERMEDIARIO = "//*[@id=\"form:finalidadeDAIR:2\"]";
	private static final String CONSULTAR = "//*[@id=\"form:botaoConsultar\"]";
	private static final String DOWNLOAD = "//*[@id=\"formTabela:tabDAIR:0:botaoImprimirDairPdf\"]";

	@Autowired
	private SeleniumService seleniumService;
	@Autowired
	private ProcessamentoDairRepository processamentoDairRepository;
	@Autowired
	private ProcessamentoErroRepository processamentoErroRepository;

	public static void main(String[] args) {
		SpringApplication.run(CadprevGeneratorApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		//processamentoDairRepository.deleteAll();
		//processamentoErroRepository.deleteAll();
		run();
	}

	private void run() {
		try {
			seleniumService.get(URL_CADPREV);

			openDemonstrativosDAIR();
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

	private void downloadAllDAIRsUnidadeFederativa() {
		List<String> estadosExecutados = processamentoDairRepository.findAllUF();
		if(estadosExecutados.size() > 0)
			estadosExecutados.remove(estadosExecutados.size() - 1);

		seleniumService.getAllOptions(UNIDADE_FEDERATIVA).forEach(uf -> {
			if(estadosExecutados.contains(uf))
				return;

			seleniumService.getDropdown(UNIDADE_FEDERATIVA).selectByVisibleText(uf);

			List<String> cidadesExecutadas = processamentoDairRepository.findAllCidadesBYUF(uf);
			List<String> cidades = seleniumService.getAllOptions(CIDADE);
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

	private void downloadDAIRCidade(String cidade, String uf) {
		seleniumService.selectDropdown(CIDADE, cidade);
		seleniumService.verifyCheckbox(ENCERRAMENTO_MES, true);
		seleniumService.verifyCheckbox(OPERACOES, false);
		seleniumService.verifyCheckbox(INTERMEDIARIO, false);
		seleniumService.clickElement(CONSULTAR);

		try {
			seleniumService.clickElement(DOWNLOAD);
			renameFile(uf, cidade);
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

	private void renameFile(String uf, String cidade) throws IOException {
		File dairFile = new File(String.format("%s/DAIR_%s.pdf", FOLDER_DOWNLOAD, new SimpleDateFormat("yyyyMMdd").format(new Date())));
		File dairNewFile = new File(String.format("%s/%s/%s/", FOLDER_DOWNLOAD_DAIR, uf.replace(" ",""), cidade.replace(" ","")) + dairFile.getName());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			log.info("erro ao dormir thread");
		}

		Files.createParentDirs(dairNewFile);
		Files.move(dairFile, dairNewFile);

	}

	private void openDemonstrativosDAIR() {
		seleniumService.clickElement(CONSULTAS_PUBLICAS);
		seleniumService.clickElement(APLICACOES);
		seleniumService.clickElement(CONSULTAS_DEMONSTRATIVOS);
	}



}
