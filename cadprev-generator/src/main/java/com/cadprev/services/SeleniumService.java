package com.cadprev.services;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeleniumService {

    static Logger log = Logger.getLogger(ProcessService.class);

    private WebDriver driver = new FirefoxDriver(firefoxOptions());

    public void get(String url) {
        driver.get(url);
    }

    public void close() {
        driver.close();
    }

    public void restart() {
        driver = new FirefoxDriver(firefoxOptions());
    }

    public List<String> getAllOptions(String element) {
        List<String> list = getDropdown(element).getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
        list.remove(0);
        return list;
    }

    public FirefoxOptions firefoxOptions() {
        return new FirefoxOptions() {{
            setBinary(firefoxBinary());
            setProfile(firefoxProfile());
        }};
    }

    private FirefoxProfile firefoxProfile() {
        return new FirefoxProfile() {{
            setPreference("browser.download.folderList", 2);
            //setPreference("browser.download.dir", "~/Downloads/DAIR");
            setPreference("browser.download.manager.showWhenStarting", false);
            setPreference("browser.helperApps.alwaysAsk.force", false);
            setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
            setPreference("pdfjs.disabled", true);
            setPreference("plugin.scan.Acrobat", "99.0");
            setPreference("plugin.scan.plid.all", false);
        }};
    }

    private FirefoxBinary firefoxBinary() {
        FirefoxBinary firefoxBinary = new FirefoxBinary() {{
            addCommandLineOptions("--headless");
        }};

        System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");

        return firefoxBinary;
    }

    public void selectDropdown(String element, String value) {
        getDropdown(element).selectByVisibleText(value);
    }

    public Select getDropdown(String element) {
        return new Select(driver.findElement(By.xpath(element)));
    }

    public void clickElement(String element) {
        driver.findElement(By.xpath(element)).click();
    }

    public void verifyCheckbox(String element, boolean checked) {
        WebElement checkboxElement = driver.findElement(By.xpath(element));
        if (checkboxElement.isSelected() != checked)
            checkboxElement.click();
    }

}
