package com.cadprev;

import com.cadprev.services.ProcessService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CadprevGeneratorApplication implements ApplicationRunner {

	static Logger log = Logger.getLogger(CadprevGeneratorApplication.class);

	@Autowired
	private ProcessService processService;

	public static void main(String[] args) {
		SpringApplication.run(CadprevGeneratorApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		processService.clearDB();
		processService.run();
	}

}
