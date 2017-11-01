package com.cadprev;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CadprevApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevApplication.class);

	private final WebDriver DRIVER = new FirefoxDriver(firefoxOptions());
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

	public static void main(String[] args) {
		SpringApplication.run(CadprevApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		DRIVER.get(URL_CADPREV);

		openDemonstrativosDAIR();
		downloadAllDAIRsUnidadeFederativa("RIO GRANDE DO SUL");
	}

	private void downloadAllDAIRsUnidadeFederativa(String unidadeFederativa) {
		selectDropdown(UNIDADE_FEDERATIVA, unidadeFederativa);

		List<String> cidades = Arrays.asList("Município de Taquara", "Município de Rolante", "Município de Morro Reuter");

		cidades.forEach(this::downloadDAIRCidade);
	}

	private void downloadDAIRCidade(String cidade) {
		selectDropdown(CIDADE, cidade);
		verifyCheckbox(ENCERRAMENTO_MES, true);
		verifyCheckbox(OPERACOES, false);
		verifyCheckbox(INTERMEDIARIO, false);
		clickElement(CONSULTAR);

		try {
			clickElement(DOWNLOAD);
		} catch (NoSuchElementException ex) {
			log.info(cidade + " não tem arquivo disponível");
		}
	}

	private FirefoxOptions firefoxOptions() {
		return new FirefoxOptions() {{
			setProfile(firefoxProfile());

		}};
	}

	private FirefoxProfile firefoxProfile() {
		return new FirefoxProfile() {{
			setPreference("browser.download.dir", "~/Download/DAIR");
			setPreference("browser.download.folderList", 2);
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

	private void selectDropdown(String element, String value) {
		Select dropdown = new Select(DRIVER.findElement(By.xpath(element)));
		dropdown.selectByVisibleText(value);
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
