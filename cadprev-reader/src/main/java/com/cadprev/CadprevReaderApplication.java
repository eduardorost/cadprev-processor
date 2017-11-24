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
		buildRepresentanteUnidadeGestora(doc);
		buildFormaGestaoAssessoramento(doc);
		String totalRecursosRPPS = getTotalRecursosRPPS(doc);
	}

	private void buildDadosEnte(Document doc) {
		String section = "DADOS DO ENTE";

		String nome = getElementText(doc, section, 6);
		String uf = getElementText(doc, section, 19);
        String cnpj = getElementText(doc, section, 21);

		String endereco = getElementText(doc, section, 15);
        //TODO: PEGAR UM EXEMPLO
        //String complemento = getElementText(doc, section, );

		String bairro = getElementText(doc, section, 17);
        String cep = getElementText(doc, section, 20);

		String telefone = getElementText(doc, section, 16);
        String paginaEletronica = getElementText(doc, section, 18);
		String email = getElementText(doc, section, 18);

	}

	private void buildDadosRepresentanteEnte(Document doc) {
		String section = "DADOS DO REPRESENTANTE LEGAL DO ENTE";

		String nome = getNodeElementText(doc, section, 34, 0);
		String cpf = getNodeElementText(doc, section, 34, 2);

        String cargo = getElementText(doc, section, 30);
        String complementoCargo = getElementText(doc, section, 33);

        String email = getNodeElementText(doc, section, 31, 2);
		String dataInicioGestao = getNodeElementText(doc, section, 31, 0);

		String telefone = getElementText(doc, section, 32);
        //TODO: PEGAR UM EXEMPLO
        //String ramal = getElementText(doc, section, );

		String rppsEmExtincao = getElementText(doc, section, 38);
	}

    //TODO: TEXTAR COM RONDONIA
	private void buildUnidadeGestora(Document doc) {
		String section = "DADOS DA UNIDADE GESTORA";

		String cnpj = getNodeElementText(doc, section, 32, 0);
		String razaoSocial = getNodeElementText(doc, section, 32, 2);

        String endereco = getNodeElementText(doc, section, 33, 0);
        String complemento = getNodeElementText(doc, section, 33, 2);

		String bairro = getNodeElementText(doc, section, 34, 0);
		String cep = getNodeElementText(doc, section, 34, 2);
		String email = getNodeElementText(doc, section, 34, 4);
        String paginaEletronica = getNodeElementText(doc, section, 34, 6);

		String telefone = getNodeElementText(doc, section, 35, 0);
		String ramal = getNodeElementText(doc, section, 35, 2);
        String natureza = getNodeElementText(doc, section, 35, 4);
        String descricao = getNodeElementText(doc, section, 35, 6);
	}

	private void buildRepresentanteUnidadeGestora(Document doc) {
		String section = "DADOS DO REPRESENTANTE LEGAL DA UNIDADE GESTORA";

		String cpf = getNodeElementText(doc, section, 36, 0);
		String nome = getNodeElementText(doc, section, 36, 2);

		String cargo = getNodeElementText(doc, section, 37, 0);
		String complementoCargo = getNodeElementText(doc, section, 37, 2);
        String dataInicioGestao = getNodeElementText(doc, section, 37, 4);
        String email = getNodeElementText(doc, section, 37, 6);

		String telefone = getNodeElementText(doc, section, 38, 0);
		String ramal = getNodeElementText(doc, section, 38, 2);
		String vinculo = getNodeElementText(doc, section, 38, 4);
		String descricao = getNodeElementText(doc, section, 38, 6);
	}

	private void buildFormaGestaoAssessoramento(Document doc) {
		String section = "FORMA DE GESTÃO E ASSESSORAMENTO";

		String formaGestaoRecursoRPPS =  getElementText(doc, section, 12);
		String contratacaoObjetivandoPrestacaoServicosConsultoria =  getElementText(doc, section, 11);

		if(doc.getElementById(getPageSection(doc, section)).getElementsMatchingText("Nenhum registro informado.").isEmpty());
			buildFormaGestaoAssessoramentoContratoVigente(section, doc);
	}

	private void buildFormaGestaoAssessoramentoContratoVigente(String section, Document doc) {
        String cnpj = getElementText(doc, section, 12);
        String razaoSocial = getElementText(doc, section, 22);

        String dataRegistroCVM = getElementText(doc, section, 14);
        String cpfRepresentante = getElementText(doc, section, 16);
        String nomeRepresentante = getElementText(doc, section, 24);

        String dataAssinaturaContrato = getElementText(doc, section, 18);
        String prazoVigencia = getElementText(doc, section, 10);
        String valorContratualMensal = getElementText(doc, section, 29);

        String numeroProcessoAdministrativo = getElementText(doc, section, 26);

        String cpfResponsavelTecnico = getElementText(doc, section, 31);
        String nomeResponsavelTecnico = getElementText(doc, section, 33);
        String dataRegistroResponsavelTecnico = getElementText(doc, section, 35);

        ////TODO: VER UM EXEMPLO
        //String objetoContratacao = getElementText(doc, section, 12);
        //String modalidadeProcedimentoLicitacao = getElementText(doc, section, );
        //String tipoLicitacao = getElementText(doc, section, );
	}

	private String getTotalRecursosRPPS(Document doc) {
		Element page = doc.select("div:contains(CARTEIRA DE INVESTIMENTOS)").last().parent().parent();
		return getElementWithPageId(doc, page.id(), 49).text();
	}

	private String getElementText(Document doc, String section, int index) {
	    try {
            return getElement(doc, section, index).text().replaceAll("\n", "");
        } catch (Exception e) {
	        return null;
        }
	}

	private String getNodeElementText(Document doc, String section, int index, int nodeIndex) {
        try {
		    return getElement(doc, section, index).childNode(nodeIndex).toString().replaceAll("\n", "");
        } catch (Exception e) {
            return null;
        }
	}

	private Element getElement(Document doc, String section, int index) {
		return getElementWithPageId(doc, getPageSection(doc, section), index);
	}

	private Element getElementWithPageId(Document doc, String pageId, int index) {
		return Xsoup.compile("//*[@id=\""+pageId+"\"]/div[1]/div["+index+"]").evaluate(doc).getElements().first();
	}

	private String getPageSection(Document doc, String section) {
		return doc.select("div:contains("+section+")").get(1).id();
	}

}
