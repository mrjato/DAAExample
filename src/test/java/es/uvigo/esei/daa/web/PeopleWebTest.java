package es.uvigo.esei.daa.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import es.uvigo.esei.daa.TestUtils;

public class PeopleWebTest {
	private static final int DEFAULT_WAIT_TIME = 1;
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestUtils.createFakeContext();
	}
	
	@Before
	public void setUp() throws Exception {
		TestUtils.initTestDatabase();
		
		driver = new FirefoxDriver();
		baseUrl = "http://localhost:9080/DAAExample/";

		driver.get(baseUrl);
		driver.manage().addCookie(
			new Cookie(
				"token",
				"25d35467c91f0f8bbcc9a4f22bb359170643ccfdf38851599a03a8ffc0756666"
			)
		);
		// Driver will wait DEFAULT_WAIT_TIME if it doesn't find and element.
		driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
		
		driver.get(baseUrl + "main.html");
		driver.findElement(By.id("people-list"));
	}
	
	@After
	public void tearDown() throws Exception {
		TestUtils.clearTestDatabase();
		
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	@Test
	public void testList() throws Exception {
		verifyXpathCount("//tr", 11);
	}

	@Test
	public void testAdd() throws Exception {
		final String name = "Hola";
		final String surname = "Mundo";
		
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys(name);
		driver.findElement(By.name("surname")).clear();
		driver.findElement(By.name("surname")).sendKeys(surname);
		driver.findElement(By.id("btnSubmit")).click();
		driver.findElement(By.xpath("//td[text()='Hola']"));
		
		assertEquals(name, 
			driver.findElement(By.cssSelector("tr:last-child > td.name")).getText()
		);
		assertEquals(surname, 
			driver.findElement(By.cssSelector("tr:last-child > td.surname")).getText()
		);
	}

	@Test
	public void testEdit() throws Exception {
		final String name = "Xián";
		final String surname = "Ximénez";
		
		final String trId = driver.findElement(By.xpath("//tr[last()]")).getAttribute("id");
		driver.findElement(By.xpath("//tr[@id='" + trId + "']//a[text()='Edit']")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys(name);
		driver.findElement(By.name("surname")).clear();
		driver.findElement(By.name("surname")).sendKeys(surname);
		driver.findElement(By.id("btnSubmit")).click();
		waitForTextInElement(By.name("name"), "");
		waitForTextInElement(By.name("surname"), "");
		
		assertEquals(name, 
			driver.findElement(By.xpath("//tr[@id='" + trId + "']/td[@class='name']")).getText()
		);
		assertEquals(surname, 
			driver.findElement(By.xpath("//tr[@id='" + trId + "']/td[@class='surname']")).getText()
		);
	}

	@Test
	public void testDelete() throws Exception {
		final String trId = driver.findElement(By.xpath("//tr[last()]")).getAttribute("id");
		driver.findElement(By.xpath("(//a[contains(text(),'Delete')])[last()]")).click();
		driver.switchTo().alert().accept();
		waitUntilNotFindElement(By.id(trId));
	}
	
	private boolean waitUntilNotFindElement(By by) {
	    return new WebDriverWait(driver, DEFAULT_WAIT_TIME)
	    	.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}
	
	private boolean waitForTextInElement(By by, String text) {
	    return new WebDriverWait(driver, DEFAULT_WAIT_TIME)
	    	.until(ExpectedConditions.textToBePresentInElementLocated(by, text));
	}

	private void verifyXpathCount(String xpath, int count) {
		try {
			assertEquals(count, driver.findElements(By.xpath(xpath)).size());
		} catch (Error e) {
			verificationErrors.append(e.toString());
		}
	}
}
