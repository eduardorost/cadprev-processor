package com.cadprev;

import com.cadprev.entities.DairEntity;
import com.cadprev.entities.FormaGestaoAssessoramentoEntity;
import com.cadprev.repositories.DairRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication
public class CadprevReaderApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevReaderApplication.class);

	@Autowired
	private DairRepository dairRepository;

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
			readPdf(
					Files
						.list(pathCity)
						.findFirst()
			);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	private void readPdf(Optional<Path> optionalPath) throws IOException {
		if(!optionalPath.isPresent())
		{
			log.info("não encontrou arquivo para cidade");
			return;
		}

		try (PDDocument document = PDDocument.load(optionalPath.get().toFile())) {
			List<String> lines = getPdfLines(document);

			DairEntity dairEntity = new DairEntity();

			processarDadosEnte(lines, dairEntity);
			processarDadosRepresentanteEnte(lines, dairEntity);
			processarUnidadeGestoraAndRepresentante(lines, dairEntity);
			processarFormaGestaoAssessoramento(lines, dairEntity);
			dairEntity.setTotalRecursosRPPS(getTotalRecursosRPPS(lines));

			DairEntity save = dairRepository.save(dairEntity);

			save.getId();
		} catch (Exception e) {
			log.info("erro processar arquivo", e);
		}
	}

	private void processarDadosEnte(List<String> lines, DairEntity dairEntity) {
		ArrayList<String> headersAndInfos = getSectionInformation(lines, "DEMONSTRATIVO DE APLICAÇÕES E INVESTIMENTOS DOS RECURSOS - DAIR", "DADOS DO REPRESENTANTE LEGAL DO ENTE", false);

		String[] split = headersAndInfos.get(0).split(":");
		ArrayList<String> infos = getInfos(9, null, headersAndInfos);
		infos.add(split[1]);

		dairEntity.setCidade(split[1]);
		dairEntity.setUf(getInfo("[A-Z]{2}", infos));
		dairEntity.setInfosEnte(infos.stream().collect(Collectors.joining(", ")));
	}

	private void processarDadosRepresentanteEnte(List<String> lines, DairEntity dairEntity) {
		ArrayList<String> headersAndInfos = getSectionInformation(lines, "DADOS DO REPRESENTANTE LEGAL DO ENTE", "ENTE", false);

		dairEntity.setInfosRepresentanteEnte(getInfosAsString(7, null, headersAndInfos));
	}

	private void processarUnidadeGestoraAndRepresentante(List<String> lines, DairEntity dairEntity) {
		ArrayList<String> headersAndInfos = getSectionInformation(lines, "DADOS DA UNIDADE GESTORA", "MINISTÉRIO DA PREVIDÊNCIA SOCIAL - MPS", true);

		dairEntity.setInfosUG(getInfosAsString(25, 29, headersAndInfos));
		dairEntity.setInfosRUG(getInfosAsString(29, 31, headersAndInfos));
	}

	private void processarFormaGestaoAssessoramento(List<String> lines, DairEntity dairEntity) {
		ArrayList<String> formaGestao = getSectionInformation(lines, "FORMA DE GESTÃO E ASSESSORAMENTO", "MINISTÉRIO DA PREVIDÊNCIA SOCIAL - MPS", true);

		if(formaGestao.contains("Nenhum registro informado."))
			return;

		dairEntity.setFormaGestaoAssessoramentoEntity(processarFormaGestaoAssessoramentoContratoVigente(formaGestao));
	}

	private FormaGestaoAssessoramentoEntity processarFormaGestaoAssessoramentoContratoVigente(List<String> formaGestao) {
		formaGestao.remove("Informações do contrato vigente");
		FormaGestaoAssessoramentoEntity formaGestaoAssessoramentoEntity = new FormaGestaoAssessoramentoEntity();

		formaGestaoAssessoramentoEntity.setPrazoVigencia(Integer.valueOf(formaGestao.stream().filter(StringUtils::isNumeric).findFirst().orElse("0")));
		formaGestaoAssessoramentoEntity.setDataAssinaturaContrato(getInfo("([0-9]{2}\\/){0,2}([0-9]{4})", formaGestao.stream().filter(s -> s.contains("Data de assinatura do Contrato")).findFirst().orElse("")));

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(formaGestaoAssessoramentoEntity.getDataAssinaturaContrato()));
			c.add(Calendar.MONTH, formaGestaoAssessoramentoEntity.getPrazoVigencia());
			formaGestaoAssessoramentoEntity.setDataFinalContrato(sdf.format(c.getTime()));
		} catch (ParseException e) {
			log.error("Erro ao converter dataAssinaturaContrato", e);
		}

		formaGestaoAssessoramentoEntity.setRazaoSocial(formaGestao.stream().filter(s -> s.contains("Razão Social:")).findFirst().orElse("").replace("Razão Social:", ""));
		formaGestaoAssessoramentoEntity.setValorContratualMensal(formaGestao.stream().filter(s -> s.contains("Valor contratual Mensal (R$):")).findFirst().orElse("").replace("Valor contratual Mensal (R$):", ""));

		formaGestaoAssessoramentoEntity.setDetalhes(formaGestao.stream().collect(Collectors.joining(", ")));

		return  formaGestaoAssessoramentoEntity;
	}

	private String getTotalRecursosRPPS(List<String> lines) {
		ArrayList<String> infos = getSectionInformation(lines, "TOTAL DE RECURSOS DO RPPS PARA CÔMPUTO DOS LIMITES:", "MINISTÉRIO DA PREVIDÊNCIA SOCIAL - MPS", true);
		return infos.get(infos.size() - 1);
	}

	private ArrayList<String> getSectionInformation(List<String> lines, String start, String end, boolean removeLast) {
		List<String> strings = lines.subList(lines.indexOf(start) + 1, lines.size());
		return new ArrayList<>(strings.subList(0, removeLast ? strings.indexOf(end) - 1 : strings.indexOf(end)));
	}

	private ArrayList<String> getHeaders(int start, int end, ArrayList<String> headersAndInfos) {
		return new ArrayList<>(headersAndInfos.subList(start, end).stream().map(s -> s.split(":")).flatMap(Arrays::stream).collect(Collectors.toList()));
	}

	private ArrayList<String> getInfos(int start, Integer end, ArrayList<String> headersAndInfos) {
		return new ArrayList<>(headersAndInfos.subList(start, end != null ? end : headersAndInfos.size()));
	}

	private String getInfosAsString(int start, Integer end, ArrayList<String> headersAndInfos) {
		return headersAndInfos.subList(start, end != null ? end : headersAndInfos.size()).stream().collect(Collectors.joining(", "));
	}

	private String getInfo(String pattern, List<String> strings) {
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = strings.stream().map(p::matcher)
				.filter(Matcher::matches)
				.findFirst().orElse(null);

		if (matcher != null && matcher.matches())
			return matcher.group();

		return "";
	}

	private String getInfo(String pattern, String string) {
		Matcher m = Pattern.compile(pattern).matcher(string);

		if (m.find())
			return m.group();

		return "";
	}

	private List<String> getPdfLines(PDDocument document) throws IOException {
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		return Arrays.asList(pdfTextStripper.getText(document).split(pdfTextStripper.getLineSeparator()));
	}

}
