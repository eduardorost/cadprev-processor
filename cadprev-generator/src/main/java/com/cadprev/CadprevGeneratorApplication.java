package com.cadprev;

import com.cadprev.entities.ProcessamentoDairEntity;
import com.cadprev.repositories.ProcessamentoDairRepository;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class CadprevGeneratorApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevGeneratorApplication.class);

	private WebDriver DRIVER = new FirefoxDriver(firefoxOptions());
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
	private ProcessamentoDairRepository processamentoDairRepository;

	public static void main(String[] args) {
		SpringApplication.run(CadprevGeneratorApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
	//	processamentoDairRepository.deleteAll();
		run();
	}

	private void run() {
		try {
			DRIVER.get(URL_CADPREV);

			openDemonstrativosDAIR();
			downloadAllDAIRsUnidadeFederativa();
		} catch (Exception e) {
			log.info("erro na aplicação", e);
			try {
				DRIVER.close();
				Thread.sleep(60000);
			} catch (Exception e1) {
				log.info("erro ao fechar o driver", e1);
			}
			DRIVER = new FirefoxDriver(firefoxOptions());
			run();
		}
	}

	private void downloadAllDAIRsUnidadeFederativa() {
		List<String> estadosExecutados = processamentoDairRepository.findAllUF();
		if(estadosExecutados.size() > 0)
			estadosExecutados.remove(estadosExecutados.size() - 1);

		getAllOptions(UNIDADE_FEDERATIVA).forEach(uf -> {
			if(estadosExecutados.contains(uf))
				return;

			getDropdown(UNIDADE_FEDERATIVA).selectByVisibleText(uf);

			List<String> cidadesExecutadas = processamentoDairRepository.findAllCidadesBYUF(uf);
			List<String> cidades = getAllOptions(CIDADE);
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

	private List<String> getAllOptions(String element) {
		List<String> list = getDropdown(element).getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
		list.remove(0);
		return list;
	}

	private void downloadDAIRCidade(String cidade, String uf) {
		selectDropdown(CIDADE, cidade);
		verifyCheckbox(ENCERRAMENTO_MES, true);
		verifyCheckbox(OPERACOES, false);
		verifyCheckbox(INTERMEDIARIO, false);
		clickElement(CONSULTAR);

		try {
			clickElement(DOWNLOAD);
			renameFile(uf, cidade);
		} catch (NoSuchElementException ex) {
			log.info(cidade + " não tem arquivo disponível");
		} catch (IOException e) {
			log.info(String.format("erro ao renomear arquivo cidade %s estado %s", cidade, uf), e);
		}

		processamentoDairRepository.save(new ProcessamentoDairEntity(cidade, uf));
	}

	private File renameFile(String uf, String cidade) throws IOException {
		File dairFile = new File(String.format("%s/DAIR_%s.pdf", FOLDER_DOWNLOAD, new SimpleDateFormat("yyyyMMdd").format(new Date())));
		File dairNewFile = new File(String.format("%s/%s/%s/", FOLDER_DOWNLOAD_DAIR, uf.replace(" ",""), cidade.replace(" ","")) + dairFile.getName());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			log.info("erro ao dormir thread");
		}

		Files.createParentDirs(dairNewFile);
		Files.move(dairFile, dairNewFile);

		return dairNewFile;
	}

	private FirefoxOptions firefoxOptions() {
		return new FirefoxOptions() {{
			setProfile(firefoxProfile());

		}};
	}

	private FirefoxProfile firefoxProfile() {
		return new FirefoxProfile() {{
			setPreference("browser.download.folderList", 2);
			setPreference("browser.download.dir", "~/Download/DAIR");
			setPreference("browser.download.manager.showWhenStarting", false);
			setPreference("browser.helperApps.alwaysAsk.force", false);
			setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
			setPreference("pdfjs.disabled", true);
			setPreference("plugin.scan.Acrobat", "99.0");
			setPreference("plugin.scan.plid.all", false);
		}};
	}

	private void openDemonstrativosDAIR() {
		clickElement(CONSULTAS_PUBLICAS);
		clickElement(APLICACOES);
		clickElement(CONSULTAS_DEMONSTRATIVOS);
	}

	private Select getDropdown(String element) {
		return new Select(DRIVER.findElement(By.xpath(element)));
	}

	private void selectDropdown(String element, String value) {
		getDropdown(element).selectByVisibleText(value);
	}

	private void clickElement(String element) {
		DRIVER.findElement(By.xpath(element)).click();
	}

	private void verifyCheckbox(String element, boolean checked) {
		WebElement checkboxElement = DRIVER.findElement(By.xpath(element));
		if (checkboxElement.isSelected() != checked)
			checkboxElement.click();
	}

}
