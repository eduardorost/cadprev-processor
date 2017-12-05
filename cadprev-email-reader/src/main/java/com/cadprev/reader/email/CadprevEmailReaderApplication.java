package com.cadprev.reader.email;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication
public class CadprevEmailReaderApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevEmailReaderApplication.class);

	private static final String BASE = "/home/eduardorost/Downloads";
	private static final String FOLDER = BASE + "/teste/";
	
	public static void main(String[] args) {
		SpringApplication.run(CadprevEmailReaderApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws IOException, InterruptedException {
		List<Email> emailList = Files.list(Paths.get(FOLDER)).map(this::findUFs).flatMap(List::stream).collect(Collectors.toList());
		generateCSV(emailList);
	}


	private List<Email> findUFs(Path pathUF) {
		log.info("--------------------"+pathUF);
		List<Email> emailList = new ArrayList<>();
		try {
			emailList = Files.list(pathUF).map(this::processCityFile).collect(Collectors.toList());
			emailList.removeAll(Collections.singleton(null));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return emailList;

	}

	private void generateCSV(List<Email> emailList) throws IOException {
		String csv = emailList.stream().map(Email::toString).collect(Collectors.joining(System.getProperty("line.separator")));

		try(PrintWriter out = new PrintWriter(BASE + "/emails.csv")){
			out.println(csv);
		}
	}

	private Email processCityFile(Path pathCity) {
		log.info(pathCity);
		try {
			return readPdf(
					Files
						.list(pathCity)
						.findFirst()
			);
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return null;
	}

	private Email readPdf(Optional<Path> optionalPath) throws IOException {
		if(!optionalPath.isPresent())
		{
			log.info("não encontrou arquivo para cidade");
		}

		try (PDDocument document = PDDocument.load(optionalPath.get().toFile())) {
			List<String> lines = getPdfLines(document);

			ArrayList<String> headersAndInfosENTE = getSectionInformation(lines, "DEMONSTRATIVO DE APLICAÇÕES E INVESTIMENTOS DOS RECURSOS - DAIR", "DADOS DO REPRESENTANTE LEGAL DO ENTE", false);
			String cidade = headersAndInfosENTE.get(0).split(":")[1];
			String uf = headersAndInfosENTE.stream().filter(s -> s.length() == 2).findFirst().orElse("");

			ArrayList<String> headersAndInfos = getSectionInformation(lines, "DADOS DA UNIDADE GESTORA", "MINISTÉRIO DA PREVIDÊNCIA SOCIAL - MPS", true);
			ArrayList<String> infosRUG = getInfos(29, 31, headersAndInfos);
			List<String> collect = infosRUG.stream().map(s -> s.split(" ")).flatMap(Arrays::stream).collect(Collectors.toList());
			String email = getInfo("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+", new ArrayList<>(collect));

			if(!StringUtils.isEmpty(email))
				return new Email(uf, cidade, email);
		} catch (Exception e) {
			log.info("erro processar arquivo", e);
		}

		return null;
	}

	private ArrayList<String> getSectionInformation(List<String> lines, String start, String end, boolean removeLast) {
		List<String> strings = lines.subList(lines.indexOf(start) + 1, lines.size());
		return new ArrayList<>(strings.subList(0, removeLast ? strings.indexOf(end) - 1 : strings.indexOf(end)));
	}

	private ArrayList<String> getInfos(int start, Integer end, ArrayList<String> headersAndInfos) {
		return new ArrayList<>(headersAndInfos.subList(start, end != null ? end : headersAndInfos.size()));
	}

	private String getInfo(String pattern, ArrayList<String> strings) {
		Pattern p = Pattern.compile(pattern);
		Optional<Matcher> matcherOptional = strings.stream().map(p::matcher)
				.filter(Matcher::matches)
				.findFirst();

		if (matcherOptional.isPresent() && matcherOptional.get().matches())
			return matcherOptional.get().group();

		return "";
	}

	private List<String> getPdfLines(PDDocument document) throws IOException {
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		return Arrays.asList(pdfTextStripper.getText(document).split(pdfTextStripper.getLineSeparator()));
	}

}
