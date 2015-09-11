package com.saucelabs;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
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

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.ConcurrentParameterized;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

@RunWith(ConcurrentParameterized.class)
public class SuitDirectCheckoutTest implements SauceOnDemandSessionIdProvider {

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

	private static String testName = "Suit Direct Checkout"; // TODO Test Name
	private static String fileName = "SuitDirect"; // TODO File name

	/**
	 * Constructs a new instance of the test. The constructor requires three
	 * string parameters, which represent the operating system, version and
	 * browser to be used when launching a Sauce VM. The order of the parameters
	 * should be the same as that of the elements within the
	 * {@link #browsersStrings()} method.
	 * 
	 * @param os
	 * @param version
	 * @param browser
	 */
	public SuitDirectCheckoutTest(String os, String version, String browser) {
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
		LinkedList browsers = new LinkedList();
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
	 * @return 
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
		SuitDirectCheckoutTest.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
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

			WebDriverWait wait = new WebDriverWait(driver, 5);

			String[] products = { "TE910975", "TE910976", "TE910977", "TE910978", "SC910787", "AS910947", "SC910786",
					"SC910905", "SC910792", "SC910900", "SC910902", "SC910901", "TE9140450", "LH920678", "TE920979",
					"LH920448", "TE920980", "SC910790", "SC910908", "SC910788", "SC910911", "SC910913", "SC910789",
					"SC910912", "ST940959", "ST940960", "ST940961", "ST940962", "TE960883", "LH940888", "LH980884",
					"TE960881", "LH980886", "ST960937", "9222107", "SC910906", "ST960963", "OC511011", "OC511012",
					"OC511013", "OC511015", "OC511016", "BR930951", "LH970942", "RG970921", "LH970943", "RG970922",
					"AS970945", "9850006", "0040300", "0040301", "LH910913", "TE910912", "TE910915", "0037657",
					"0037661", "0037666", "TE910917", "TE910795", "TE910916", "TE910914", "LH910616", "WH510426",
					"WH910425", "LH910967", "LH910968", "TE910970", "TE910971", "TE910972", "TE910973", "TE910974" };
			driver.get("https://www.suitdirect.co.uk/");
			driver.findElement(By.name("search")).clear();

			log.add("Trying product with ID " + products[0]);
			driver.findElement(By.name("search")).sendKeys(products[0]);
			driver.findElement(By.xpath("//*[@id='search']/a")).click();

			// Checks if product can not be found. Iterate through array until a product is found.
			if ((driver.findElement(By.className("itemListContainer")).getText()
					.contains("Sorry no products were found.")) == true) {
				for (int i = 1; i < products.length; i++) {
					driver.findElement(By.name("search")).clear();

					log.add("Trying product with ID " + products[i]);
					driver.findElement(By.name("search")).sendKeys(products[i]);
					driver.findElement(By.xpath("//*[@id='search']/a")).click();

					// Check if product is found, exit loop if present
					if ((driver.findElement(By.className("itemListContainer")).getText()
							.contains("Sorry no products were found.")) == false) {
						break;
					}
				}
			}

			// Add to Bag and go to Basket
			driver.findElement(By.cssSelector("div.itemImage > a")).click();
			driver.findElement(By.className("btnAddToBag")).click();

			// Wait 5 seconds max for modal to be clickable, then proceed
			WebElement modal = driver.findElement(By.xpath("//*[@id='cboxLoadedContent']/div/div/div/a[1]"));
			wait.until(ExpectedConditions.elementToBeClickable(modal));
			modal.click();

			// Checkout
			driver.findElement(By.linkText("Checkout")).click();

			// Login
			driver.findElement(By.id("content_txtUserName")).clear();
			driver.findElement(By.id("content_txtUserName")).sendKeys("checkouttester@remarkable.net");
			driver.findElement(By.id("content_txtPassword")).clear();
			driver.findElement(By.id("content_txtPassword")).sendKeys("checkouttester");
			driver.findElement(By.id("content_btnLogin")).click();

			// Go to Payment
			driver.findElement(By.id("content_chkAgree")).click();
			driver.findElement(By.id("content_LinkButton1")).click();

			WebElement iframe = driver.findElement(By.cssSelector(".adminbox > iframe"));

			// Switch focus to iFrame
			driver.switchTo().frame(iframe);

			// Enter payment details and submit
			driver.findElement(By.id("inputCardNumber")).clear();
			driver.findElement(By.id("inputCardNumber")).sendKeys("5404000000000043");

			Select expiryMonth = new Select(driver.findElement(By.id("expiryMonth")));
			expiryMonth.selectByVisibleText("05");
			Select expiryYear = new Select(driver.findElement(By.id("expiryYear")));
			expiryYear.selectByVisibleText("2017");

			driver.findElement(By.id("inputSecurity")).clear();
			driver.findElement(By.id("inputSecurity")).sendKeys("123");

			driver.findElement(By.id("proceedButton")).click();

			Thread.sleep(5000);
			
			String errorText = driver.findElement(By.id("formCardDetails")).getText();
			Boolean checkExpected = errorText.contains(cardError);
			
			if (checkExpected == false) {
				msg.send("Expected text could not be found. \n\nExpected: " + cardError + "\n\nReturned: " + errorText);
			}
			
			assertTrue(checkExpected);

		} catch (Exception e) {
			String eS = e.toString();
			log.add("Exception found");
			log.add(eS);

			msg.send(eS);
			
			// Force fail for Sauce Labs
			boolean success = false;
			assertTrue(success);
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