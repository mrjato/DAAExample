package es.uvigo.esei.daa.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedCondition;

/*
 * Implementation based on https://stackoverflow.com/questions/33348600/selenium-wait-for-ajax-content-to-load-universal-approach
 */
public class AdditionalConditions {
	public static ExpectedCondition<Boolean> jQueryAjaxCallsHaveCompleted() {
		return driver -> 
			(Boolean) ((JavascriptExecutor) driver).executeScript("return (window.jQuery !== null) && (jQuery.active === 0)");
	}
}
