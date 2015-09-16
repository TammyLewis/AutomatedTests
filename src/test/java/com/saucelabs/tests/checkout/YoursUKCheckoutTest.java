package com.saucelabs.tests.checkout;

import com.saucelabs.Logger;
import com.saucelabs.SendMessages;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.ConcurrentParameterized;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@RunWith(ConcurrentParameterized.class)
public class YoursUKCheckoutTest implements SauceOnDemandSessionIdProvider {

	/**
	 * Gets SauceLab authentication details from the file C:\\Tests\\configs\\SauceLabs.properties
	 * Exits the program if it can't be found
	 */
	private static String slUser;
	private static String slAuth;

	@BeforeClass
	public static void sauceLabs() throws IOException {
		Properties config = new Properties();
		try {
			InputStream slCfg = new FileInputStream("C:\\Tests\\configs\\SauceLabs.properties");
			config.load(slCfg);

			slUser = config.getProperty("user");
			slAuth = config.getProperty("auth");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Constructs a {@link SauceOnDemandAuthentication} instance using the
	 * supplied user name/access key.
	 *
	 */
	public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(slUser, slAuth);

	// JUnit Rule which will mark the Sauce Job as passed/failed when the test succeeds or fails.
	@Rule
	public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

	private String browser; 			// Represents the browser to be used as part of the test run.
	private String os; 					// Represents the operating system to be used as part of the test run.
	private String version; 			// Represents the version of the browser to be used as part of the test run.
	private static String sessionId; 	// Instance variable which contains the Sauce Job Id.
	private WebDriver driver; 			// The {@link WebDriver} instance which is used to perform browser interactions with.

	private static String testName = "Yours Clothing (UK) Checkout"; // TODO Test Name
	private static String fileName = "YoursClothing"; // TODO File name

	/**
	 * Constructs a new instance of the test. The constructor requires three
	 * string parameters, which represent the operating system, version and
	 * browser to be used when launching a Sauce VM. The order of the parameters
	 * should be the same as that of the elements within the
	 * {@link #browsersStrings()} method.
	 */
	public YoursUKCheckoutTest(String os, String version, String browser) {
		super();
		this.os = os;
		this.version = version;
		this.browser = browser;
	}

	/**
	 * @return a LinkedList containing String arrays representing the browser
	 *         combinations the test should be run against. The values in the
	 *         String array are used as part of the invocation of the test
	 *         constructor
	 */
	@ConcurrentParameterized.Parameters
	public static LinkedList browsersStrings() {
		LinkedList<String[]> browsers = new LinkedList<String[]>();
		// browsers.add(new String[]{"Windows 10", "20.10240", "microsoftedge"});
		browsers.add(new String[] { "Windows 10", "45.0", "chrome"});
		// browsers.add(new String[]{"Windows 10", "40.0", "firefox"});
		// browsers.add(new String[]{"Windows 10", "11.0", "internet
		// explorer"});
		// browsers.add(new String[]{"OS X 10.11", "8.1", "safari"});
		return browsers;
	}

	/**
	 * Constructs a new {@link RemoteWebDriver} instance which is configured to
	 * use the capabilities defined by the {@link #browser}, {@link #version}
	 * and {@link #os} instance variables, and which is configured to run
	 * against ondemand.saucelabs.com, using the username and access key
	 * populated by the {@link #authentication} instance.
	 *
	 * @throws Exception
	 *             if an error occurs during the creation of the
	 *             {@link RemoteWebDriver} instance.
	 */
	@Before
	public void setUp() throws Exception {

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
		if (version != null) {
			capabilities.setCapability(CapabilityType.VERSION, version);
		}
		capabilities.setCapability(CapabilityType.PLATFORM, os);
		capabilities.setCapability("name", testName);
		this.driver = new RemoteWebDriver(new URL("http://" + authentication.getUsername() + ":"
				+ authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"), capabilities);
		YoursUKCheckoutTest.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
	}
	
	@Test
	public void checkoutJourney() throws Exception {
		
		Logger log = new Logger(fileName, sessionId);
		log.add("Starting test");
		
		String cardError = null;
		Properties assertCfg = new Properties();
		try {
			InputStream input = new FileInputStream("C:\\Tests\\configs\\" + fileName + ".properties");
			assertCfg.load(input);
			cardError = assertCfg.getProperty("cardError");
		} catch (FileNotFoundException e) {
			log.add("Could not find file - C:\\Tests\\configs\\" + fileName + ".properties");
			System.exit(1);
		} 
		if (cardError == null) {
			log.add("Card error value could not be found in C:\\Tests\\configs\\" + fileName + ".properties");
			System.exit(1);
		}

		SendMessages msg = new SendMessages(testName, fileName, sessionId);

		try {

			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			WebDriverWait wait = new WebDriverWait(driver, 10);

			driver.get("http://www.yoursclothing.co.uk/");
			driver.findElement(By.id("search-query")).clear();
			driver.findElement(By.id("search-query")).sendKeys("bracelet");
			driver.findElement(By.id("search-query")).submit();

            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.className("slideInBox"))));

            if (driver.findElement(By.className("slideInBox")).isDisplayed()) {
                driver.findElement(By.xpath("//*[@id=\"divSlideIn-1252\"]/div/map/area")).click();
            }
            driver.findElement(By.cssSelector(".listing-items > .row > div:nth-of-type(1) > div > div > a > img")).click();

			// Add to Bag and go to Basket
            driver.findElement(By.className("addToBag")).click();
            driver.findElement(By.className("goToBasket")).click();

			// tests
            driver.findElements(By.className("btn-secondary")).get(0).click();

            // Login
            driver.findElement(By.id("email")).clear();
			driver.findElement(By.id("email")).sendKeys("checkouttester@remarkable.net");
			driver.findElement(By.id("password")).clear();
			driver.findElement(By.id("password")).sendKeys("checkouttester");
			driver.findElement(By.name("btnSubmitOption1")).click();

			// Enter payment details and submit
            driver.findElement(By.id("add_new_card")).click();
            driver.findElement(By.id("txtCardNumber")).clear();
			driver.findElement(By.id("txtCardNumber")).sendKeys("5404000000000043");

            driver.findElement(By.id("txtCardHolder")).clear();
            driver.findElement(By.id("txtCardHolder")).sendKeys("tests Tester");

			Select expiryMonth = new Select(driver.findElement(By.id("drpEndDateMonth")));
			expiryMonth.selectByVisibleText("05");
			Select expiryYear = new Select(driver.findElement(By.id("drpEndDateYear")));
			expiryYear.selectByVisibleText("2017");

			driver.findElement(By.id("txtCV2")).clear();
			driver.findElement(By.id("txtCV2")).sendKeys("123");

            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).sendKeys(Keys.HOME);
            driver.findElement(By.xpath("//*[@id='ajaxTotals']/div/div[1]/div/div[2]/button")).click();

            String errorText = driver.findElement(By.className("alert")).getText();
			Boolean checkExpected = errorText.contains(cardError);
			
			if (!checkExpected) {
				msg.send("Expected text could not be found. \n\nExpected: " + cardError + "\n\nReturned: " + errorText);
			}
			
			assertTrue(checkExpected);

		} catch (Exception e) {
			String eS = e.toString();
			log.add("Exception found");
			log.add(eS);

			msg.send(eS);
			
			// Force fail for Sauce Labs
			assertTrue(false);
		}

	}

	/**
	 * Closes the {@link WebDriver} session.
	 *
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		Logger log = new Logger(fileName, sessionId);
		log.add("Test with ID " + sessionId + " finished");
		log.add("Test details: http://saucelabs.com/tests/" + sessionId);
		driver.quit();
	}

	/**
	 *
	 * @return the value of the Sauce Job id.
	 */
	@Override
	public String getSessionId() {
		return sessionId;
	}
}