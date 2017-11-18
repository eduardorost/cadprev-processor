package com.cadprev;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.codecraft.xsoup.Xsoup;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@SpringBootApplication
public class CadprevReaderApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevReaderApplication.class);

	private static final String FOLDER = "/home/eduardorost/Downloads/DAIR/";
	
	public static void main(String[] args) {
		SpringApplication.run(CadprevReaderApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws IOException, InterruptedException {
		Files.list(Paths.get(FOLDER)).forEach(this::findUFs);
	}

	private void findUFs(Path pathUF) {
		log.info("--------------------"+pathUF);
		try {
			Files.list(pathUF).forEach(this::processCityFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processCityFile(Path pathCity) {
		log.info(pathCity);
		try {
			readHtml(
					Files
						.list(pathCity)
						.filter(path -> path.toString().endsWith(".html"))
						.findFirst()
			);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	private void readHtml(Optional<Path> optionalPath) throws IOException, ParserConfigurationException {
		if(!optionalPath.isPresent())
		{
			log.info("não encontrou arquivo para cidade");
			return;
		}

		Document doc = Jsoup.parse(new String(Files.readAllBytes(optionalPath.get())));
		buildDadosEnte(doc);
		buildDadosRepresentanteEnte(doc);
		buildUnidadeGestora(doc);
	}

	private void buildUnidadeGestora(Document doc) {
		String section = "DADOS DA UNIDADE GESTORA";
		
		String cnpj = getNodeElementText(doc, section, 32, 0);
		String razaoSocial = getNodeElementText(doc, section, 32, 2);
		String endereco = getElementText(doc, section, 33);
		String bairro = getNodeElementText(doc, section, 34, 0);
		String cep = getNodeElementText(doc, section, 34, 2);
		String email = getNodeElementText(doc, section, 34, 4);
		String telefone = getNodeElementText(doc, section, 35, 0);
		String ramal = getNodeElementText(doc, section, 35, 2);
		String natureza = getNodeElementText(doc, section, 35, 4);

		//TODO: PEGAR UM EXEMPLO
		//String complemento = getElementText(doc, section, );
		//String paginaEletronica = getElementText(doc, section, );
		//String descricao = getElementText(doc, section, );
	}

	private void buildDadosRepresentanteEnte(Document doc) {
		String section = "DADOS DO REPRESENTANTE LEGAL DO ENTE";

		String nome = getNodeElementText(doc, section, 34, 0);
		String cpf = getNodeElementText(doc, section, 34, 2);
		String dataInicioGestao = getNodeElementText(doc, section, 31, 0);
		String email = getNodeElementText(doc, section, 31, 2);
		String cargo = getElementText(doc, section, 30);
		String telefone = getElementText(doc, section, 32);
		String complementoCargo = getElementText(doc, section, 33);
		String rpps = getElementText(doc, section, 38);

		//TODO: PEGAR UM EXEMPLO
		//String ramal = getElementText(doc, section, );
	}

	private void buildDadosEnte(Document doc) {
		String section = "DADOS DO ENTE";

		String nome = getElementText(doc, section, 6);
		String uf = getElementText(doc, section, 18);
		String endereco = getElementText(doc, section, 15);
		String bairro = getElementText(doc, section, 17);
		String telefone = getElementText(doc, section, 16);
		String cnpj = getElementText(doc, section, 21);
		String cep = getElementText(doc, section, 20);
		String email = getElementText(doc, section, 19);

		//TODO: PEGAR UM EXEMPLO
		// String complemento = getElementText(doc, section, );
		//String paginaEletronica = getElementText(doc, section, );

	}

	private String getElementText(Document doc, String section, int index) {
		return getElement(doc, section, index).text().replaceAll("\n", "");
	}

	private String getNodeElementText(Document doc, String section, int index, int nodeIndex) {
		return getElement(doc, section, index).childNode(nodeIndex).toString().replaceAll("\n", "");
	}

	private Element getElement(Document doc, String section, int index) {
		return Xsoup.compile("//*[@id=\""+getPageSection(doc, section)+"\"]/div[1]/div["+index+"]").evaluate(doc).getElements().first();
	}

	private String getPageSection(Document doc, String section) {
		return doc.select("div:contains("+section+")").get(1).id();
	}

}
