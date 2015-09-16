package com.saucelabs.tests.checkout;


import com.saucelabs.Logger;
import com.saucelabs.SendMessages;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.ConcurrentParameterized;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
public class MossCheckoutTest implements SauceOnDemandSessionIdProvider {

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

	private static String testName = "Moss Bros Checkout"; // TODO Test Name
	private static String fileName = "MossBros"; // TODO File name

	/**
	 * Constructs a new instance of the test. The constructor requires three
	 * string parameters, which represent the operating system, version and
	 * browser to be used when launching a Sauce VM. The order of the parameters
	 * should be the same as that of the elements within the
	 * {@link #browsersStrings()} method.
	 */
	public MossCheckoutTest(String os, String version, String browser) {
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
		MossCheckoutTest.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
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

			String[] products = { "965460815", "965460807", "965462213", "965462204", "965284601", "965284813",
                    "965461322", "965461501", "965461414", "965461809", "965326003", "965570214", "965326018",
                    "965461219", "965569719", "965461263", "965570119", "965461204", "965461291", "965326071",
                    "965569909", "965326002", "965570798", "965326009", "965570509", "965284109", "965570137",
                    "965570534", "965446515", "965284009", "965434715", "965284118", "965446709", "965434815",
                    "965569918", "965446415", "965446307", "965435715", "965570004", "965570209", "965569801",
                    "965461619", "965570377", "965569709", "965284001", "965435817", "965457098", "965489709",
                    "965446621", "965570991", "965569704", "965569891", "965570819", "965570414", "965461292",
                    "965570418", "965570201", "965570413" };

			driver.get("http://www.moss.co.uk/");
			driver.findElement(By.id("search-query")).clear();

			log.add("Trying product with ID " + products[0]);
			driver.findElement(By.id("search-query")).sendKeys(products[0]);
			driver.findElement(By.id("search-query")).submit();

            //Attempt to add to bag
            driver.findElement(By.className("addToBag")).click();

			// Checks if product can not be found. Iterate through array until a product is found.
			if (driver.findElement(By.className("basketStatus")).getText()
					.contains("Sorry. We have no stock of that item at the moment.")) {
				for (int i = 1; i < products.length; i++) {
					driver.findElement(By.id("search-query")).clear();

					log.add("Trying product with ID " + products[i]);
					driver.findElement(By.id("search-query")).sendKeys(products[i]);
					driver.findElement(By.id("search-query")).submit();
                    driver.findElement(By.className("addToBag")).click();

					// Check if product is found, exit loop if present
					if (!(driver.findElement(By.className("basketStatus")).getText()
							.contains("Sorry. We have no stock of that item at the moment."))) {
						break;
					}
				}
			}


			// Pay now
            driver.findElement(By.linkText("Pay Now")).click();

			// Checkout
			driver.findElements(By.linkText("Go to Checkout")).get(0).click();

			// Login
			driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/div/input[1]")).clear();
			driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/div/input[1]")).sendKeys("checkouttester@remarkable.net");
			driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/div/input[2]")).clear();
			driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/div/input[2]")).sendKeys("checkouttester");
			driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/input")).click();

			// Go to Payment
			driver.findElement(By.className("btn-topRight")).click();


			// Enter payment details and submit
			driver.findElement(By.id("card.cardNumber")).clear();
			driver.findElement(By.id("card.cardNumber")).sendKeys("5404000000000043");

            driver.findElement(By.id("card.cardHolderName")).clear();
            driver.findElement(By.id("card.cardHolderName")).sendKeys("Checkout Tester");

			Select expiryMonth = new Select(driver.findElement(By.id("card.expiryMonth")));
			expiryMonth.selectByVisibleText("05");
			Select expiryYear = new Select(driver.findElement(By.id("card.expiryYear")));
			expiryYear.selectByVisibleText("2017");

			driver.findElement(By.id("card.cvcCode")).clear();
			driver.findElement(By.id("card.cvcCode")).sendKeys("123");

			driver.findElement(By.className("paySubmit")).click();

			String errorText = driver.findElement(By.id("errorFrameValidationErrors")).getText();
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