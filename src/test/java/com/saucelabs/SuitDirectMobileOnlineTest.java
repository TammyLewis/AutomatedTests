package com.saucelabs;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.ConcurrentParameterized;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

@RunWith(ConcurrentParameterized.class)
public class SuitDirectMobileOnlineTest implements SauceOnDemandSessionIdProvider {

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

	String testName = "Suit Direct Online (Mobile)"; // TODO Test Name
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
	public SuitDirectMobileOnlineTest(String os, String version, String browser) {
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
		SuitDirectMobileOnlineTest.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
	}
	
	@Test
	public void checkTitleMobile() throws Exception {
		
		SendMessages msg = new SendMessages(testName, fileName, sessionId);
		Logger log = new Logger(fileName, sessionId);

		log.add("Starting test");
		
		String expected = null;
		Properties assertCfg = new Properties();
		try {
			InputStream input = new FileInputStream("C:\\Tests\\configs\\" + fileName + ".properties");
			InputStreamReader inputReader = new InputStreamReader(input, "UTF-8");
			assertCfg.load(inputReader);
			expected = assertCfg.getProperty("title.mobile");
			
		} catch (FileNotFoundException e) {
			log.add("Could not find file - C:\\Tests\\configs\\" + fileName + ".properties");
			System.exit(1);
		} 
		if (expected == null) {
			log.add("Title value could not be found in C:\\Tests\\configs\\" + fileName + ".properties");
			System.exit(1);
		}
		
		driver.get("http://m.suitdirect.co.uk");
		String title = driver.getTitle();
		Boolean success = title.equals(expected);
		
		if (success == false) {
			log.add("Retrieved title did not match the expected title");
			log.add("Expected: " + expected);
			log.add("Retrieved: " + title);
			msg.send("Retrieved title did not match the expected title \n\nExpected: " + expected + "\nRetrieved: " + title);
		} else {
			log.add("Expected title matched retrieved title");
		}

		assertTrue(success);
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