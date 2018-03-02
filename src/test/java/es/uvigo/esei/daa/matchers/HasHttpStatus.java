/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.uvigo.esei.daa.matchers;

import static java.util.Objects.requireNonNull;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasHttpStatus extends TypeSafeMatcher<Response> {
	private final StatusType expectedStatus;

	public HasHttpStatus(StatusType expectedStatus) {
		this.expectedStatus = requireNonNull(expectedStatus);
	}

	public HasHttpStatus(int expectedStatus) {
		this(Status.fromStatusCode(expectedStatus));
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(this.expectedStatus);
	}

	@Override
	protected void describeMismatchSafely(Response item, Description mismatchDescription) {
		mismatchDescription.appendText("was ").appendValue(item.getStatusInfo());
	}

	@Override
	protected boolean matchesSafely(Response item) {
		return item != null && expectedStatus.getStatusCode() == item.getStatusInfo().getStatusCode();
	}

	@Factory
	public static Matcher<Response> hasHttpStatus(StatusType expectedStatus) {
		return new HasHttpStatus(expectedStatus);
	}

	@Factory
	public static Matcher<Response> hasHttpStatus(int expectedStatus) {
		return new HasHttpStatus(expectedStatus);
	}

	@Factory
	public static Matcher<Response> hasOkStatus() {
		return new HasHttpStatus(Response.Status.OK);
	}

	@Factory
	public static Matcher<Response> hasBadRequestStatus() {
		return new HasHttpStatus(Response.Status.BAD_REQUEST);
	}

	@Factory
	public static Matcher<Response> hasInternalServerErrorStatus() {
		return new HasHttpStatus(Response.Status.INTERNAL_SERVER_ERROR);
	}

	@Factory
	public static Matcher<Response> hasUnauthorized() {
		return new HasHttpStatus(Response.Status.UNAUTHORIZED);
	}

	@Factory
	public static Matcher<Response> hasForbidden() {
		return new HasHttpStatus(Response.Status.FORBIDDEN);
	}
}
