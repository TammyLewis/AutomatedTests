package com.saucelabs.tests.manual;

import com.saucelabs.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class BigListOfNaughtyStrings {
    public static void main(String[] args) throws Exception {

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("C:\\Tests\\configs\\BigListOfNaughtyStrings.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Config file could not be found. Expected location: C:\\Tests\\configs\\BigListOfNaughtyStrings.txt" );
            System.exit(0);
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        String time = sdf.format(calendar.getTime());

        Logger log = new Logger(time, "BigListOfNaughtyStrings");

        log.add("Starting test");

        String value;
        WebDriver driver = new FirefoxDriver();

        String website = "http://simoncarter.preview.remarkable.net/"; // TODO Website
        By search = By.id("search-query"); // TODO Search locator
        By breadcrumb = By.className("navBreadcrumb"); // TODO Breadcrumb locator

        driver.get(website);

        while ((value = br.readLine()) != null) {


            driver.findElement(search).clear();
            driver.findElement(search).sendKeys(value);
            driver.findElement(search).submit();

            String returned;

            try {
                returned = driver.findElement(breadcrumb).getText();
            } catch (NoSuchElementException e) {
                String eTitle = driver.getTitle();
                String eUrl = driver.getCurrentUrl();
                log.add("Could not find element for " + value + "\nReturned page is " + eUrl + " with title \"" + eTitle + "\"\n");
                returned = "ENFE";
                driver.get(website);
            }

            if (!returned.equals("ENFE")) {
                Boolean match = returned.contains(value);

                if (!match) {
                    log.add("Returned value did not match submitted\nReturned: " + returned + "\nExpected: " + value + "\n");
                }
            }

            // TODO Sleep if necessary
            //Thread.sleep(5000);

        }

        log.add("Test finished");

        br.close();
        driver.close();

    }
}

