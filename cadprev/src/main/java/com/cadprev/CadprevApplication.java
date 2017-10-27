package com.cadprev;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class CadprevApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(CadprevApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		WebDriver driver = new FirefoxDriver();
		driver.get("http://www.google.com");
		WebElement element = driver.findElement(By.name("q"));
		element.sendKeys("Cheese!\n"); // send also a "\n"
		element.submit();

		// wait until the google page shows the result
		WebElement myDynamicElement = (new WebDriverWait(driver, 10))
				.until(ExpectedConditions.presenceOfElementLocated(By.id("resultStats")));

		List<WebElement> findElements = driver.findElements(By.xpath("//*[@id='rso']//h3/a"));

		// this are all the links you like to visit
		for (WebElement webElement : findElements)
		{
			System.out.println(webElement.getAttribute("href"));
		}
	}

}
