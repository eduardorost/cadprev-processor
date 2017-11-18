package com.cadprev;

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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class CadprevGeneratorApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevGeneratorApplication.class);

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
		SpringApplication.run(CadprevGeneratorApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		DRIVER.get(URL_CADPREV);

		openDemonstrativosDAIR();
		downloadAllDAIRsUnidadeFederativa();
	}

	private void downloadAllDAIRsUnidadeFederativa() {
		getAllOptions(UNIDADE_FEDERATIVA).forEach(uf -> {
			getDropdown(UNIDADE_FEDERATIVA).selectByVisibleText(uf);
			getAllOptions(CIDADE).forEach(cidade -> downloadDAIRCidade(cidade, uf));
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
			generateHtml(renameFile(uf, cidade));
		} catch (NoSuchElementException ex) {
			log.info(cidade + " não tem arquivo disponível");
		} catch (IOException e) {
			log.info(String.format("erro ao renomear arquivo cidade %s estado %s", cidade, uf), e);
		} catch (InterruptedException e) {
			log.info("não conseguiu gerar o html");
		}
	}

	private void generateHtml(File path) throws IOException, InterruptedException {
		Process process = new ProcessBuilder("pdftohtmljs", path.getName())
								.inheritIO()
								.directory(new File(path.getParent()))
								.start();

		if(process.waitFor() == 0)
			log.info("html gerado com sucesso");
		else
			log.info("não conseguiu gerar o html");
	}

	private File renameFile(String uf, String cidade) throws IOException {
		String urlFile = "/home/eduardorost/Downloads";

		File dairFile = new File(String.format("%s/DAIR_%s.pdf", urlFile, new SimpleDateFormat("yyyyMMdd").format(new Date())));
		File dairNewFile = new File(String.format("%s/DAIR/%s/%s/", urlFile, uf.replace(" ",""), cidade.replace(" ","")) + dairFile.getName());

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
