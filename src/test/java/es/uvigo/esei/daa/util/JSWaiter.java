package es.uvigo.esei.daa.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Utility class to wait for several JavaScript events to complete.
 * 
 * Code adapted from
 * https://www.swtestacademy.com/selenium-wait-javascript-angular-ajax/
 */
public class JSWaiter {
	private final WebDriverWait jsWait;
	private final JavascriptExecutor jsExec;

	private JSWaiter(WebDriver driver) {
		this.jsWait = new WebDriverWait(driver, 10);
		this.jsExec = (JavascriptExecutor) driver;
	}

	public static JSWaiter wait(WebDriver driver) {
		return new JSWaiter(driver);
	}

	public void untilAngular5Ready() {
		try {
			final Object angular5Check = jsExec.executeScript("return getAllAngularRootElements()[0].attributes['ng-version']");
			if (angular5Check != null) {
				final Boolean angularPageLoaded = (Boolean) jsExec
						.executeScript("return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1");
				if (!angularPageLoaded) {
					poll(20);

					waitForAngular5Load();

					poll(20);
				}
			}
		} catch (WebDriverException ignored) {}
	}

	private void waitForAngular5Load() {
		String angularReadyScript = "return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1";
		
		try {
			final ExpectedCondition<Boolean> angularLoad = driver -> Boolean
					.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());

			final boolean angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());

			if (!angularReady) {
				jsWait.until(angularLoad);
			}
		} catch (WebDriverException ignored) {}
	}

	private void poll(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
