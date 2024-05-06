package com.casi.ws.image.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class HttpHeaderTest {

	
	
	@Test
	@Tag("header-tag")
	public void headerValid() {
		String header = "Authorization: API_KEY generatedChar";

		assertTrue(header.startsWith("Authorization"));
	}
	
	@Test
	@Tag("header-tag")
	public void headerContent() {
		String headerContent = "API_KEY generatedChar";

		assertTrue(headerContent.startsWith("API_KEY"));
	}
	
	
	
}
