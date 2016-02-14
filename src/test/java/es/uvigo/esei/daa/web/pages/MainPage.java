package es.uvigo.esei.daa.web.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import es.uvigo.esei.daa.entities.Person;

public class MainPage {
	private static final String TABLE_ID = "people-list";
	private static final String FORM_ID = "people-form";
	
	private static final String ID_PREFIX = "person-";
	
	private final WebDriver driver;
	
	private final WebDriverWait wait;
	
	private final String baseUrl;
	
	public MainPage(WebDriver driver, String baseUrl) {
		this.driver = driver;
		this.baseUrl = baseUrl;
		
		this.wait = new WebDriverWait(driver, 1);
	}

	public void navigateTo() {
		this.driver.get(this.baseUrl + "main.html");
		
	    this.wait.until(presenceOfElementLocated(By.id("people-list")));
	}
	
	public int countPeople() {
		return new PeopleTable(this.driver).countPeople();
	}
	
	public Person getLastPerson() {
		return new PeopleTable(this.driver).getPersonInLastRow();
	}
	
	public Person getPerson(int id) {
		return new PeopleTable(this.driver).getPersonById(id);
	}
	
	public boolean hasPerson(int id) {
		return new PeopleTable(this.driver).hasPerson(id);
	}
	
	public Person addPerson(String name, String surname) {
		final PersonForm form = new PersonForm(this.driver);
		
		form.clear();
		form.setName(name);
		form.setSurname(surname);
		form.submit();
		
		final PeopleTable table = new PeopleTable(driver);
		return table.getPerson(name, surname);
	}
	
	public void editPerson(int id, String newName, String newSurname) {
		final PeopleTable table = new PeopleTable(this.driver);
		table.editPerson(id);

		final PersonForm form = new PersonForm(this.driver);
		form.setName(newName);
		form.setSurname(newSurname);
		form.submit();
	}
	
	public void deletePerson(int id) {
		final PeopleTable table = new PeopleTable(this.driver);
		
		table.deletePerson(id);
	}
	
	private final static class PeopleTable {
		
		private final WebDriver driver;
		
		private final WebElement table;

		public PeopleTable(WebDriver driver) {
			this.driver = driver;
			
			this.table = this.driver.findElement(By.id(TABLE_ID));
		}
		
		public boolean hasPerson(int id) {
			try {
				return this.getPersonRow(id) != null;
			} catch (NoSuchElementException nsee) {
				return false;
			}
		}
		
		public void editPerson(int id) {
			final WebElement personRow = this.getPersonRow(id);
			
			personRow.findElement(By.className("edit")).click();
		}
		
		public void deletePerson(int id) {
			final WebElement personRow = this.getPersonRow(id);
			
			personRow.findElement(By.className("delete")).click();
			
			this.acceptDialog();
		}
		
		public Person getPersonById(int id) {
			return rowToPerson(getPersonRow(id));
		}
		
		public Person getPerson(String name, String surname) {
			return rowToPerson(getPersonRow(name, surname));
		}
		
		public Person getPersonInLastRow() {
			final WebElement row = this.table.findElement(By.cssSelector("tr:last-child"));
			
			return rowToPerson(row);
		}
		
		private WebElement getPersonRow(int id) {
			return this.table.findElement(By.id(ID_PREFIX + id));
		}
		
		public WebElement getPersonRow(String name, String surname) {
			final String xpathQuery = String.format(
				"//td[@class = 'name' and text() = '%s']"
				+ "/following-sibling::td[@class = 'surname' and text() = '%s']"
				+ "/parent::tr",
				name, surname
			);

			return table.findElement(By.xpath(xpathQuery));
		}
		
		public int countPeople() {
			final String xpathQuery = "//tr[starts-with(@id, '" + ID_PREFIX + "')]";
			
			final List<WebElement> peopleRows =
				this.table.findElements(By.xpath(xpathQuery));
			
			return peopleRows.size();
		}
		
		private Person rowToPerson(WebElement row) {
			return new Person(
				Integer.parseInt(row.getAttribute("id").substring(ID_PREFIX.length())),
				row.findElement(By.className("name")).getText(),
				row.findElement(By.className("surname")).getText()
			);
		}
		
		private void acceptDialog() {
			driver.switchTo().alert().accept();
		}
	}
	
	public final static class PersonForm {
		private final WebDriverWait wait;
		
		private final WebElement fieldName;
		private final WebElement fieldSurname;
		private final WebElement buttonClear;
		private final WebElement buttonSubmit;

		public PersonForm(WebDriver driver) {
			this.wait = new WebDriverWait(driver, 1);
			
			final WebElement form = driver.findElement(By.id(FORM_ID));
			
			this.fieldName = form.findElement(By.name("name"));
			this.fieldSurname = form.findElement(By.name("surname"));
			this.buttonClear = form.findElement(By.id("btnClear"));
			this.buttonSubmit = form.findElement(By.id("btnSubmit"));
		}
		
		public void submit() {
			this.buttonSubmit.click();
			
		    this.waitForCleanFields();
		}
		
		public void clear() {
			this.buttonClear.click();
			
			this.waitForCleanFields();
		}
		
		public void setName(String name) {
			this.fieldName.clear();
			this.fieldName.sendKeys(name);
		}
		
		public void setSurname(String surname) {
			this.fieldSurname.clear();
			this.fieldSurname.sendKeys(surname);
		}
		
		public String getName() {
			return this.fieldName.getText();
		}
		
		public String getSurname() {
			return this.fieldSurname.getText();
		}
		
		private void waitForCleanFields() {
			wait.until(textToBePresentInElement(fieldName, ""));
			wait.until(textToBePresentInElement(fieldSurname, ""));
		}
	}
}
