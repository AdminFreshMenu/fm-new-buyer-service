package com.buyer;

import org.testng.annotations.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@SpringBootTest
public class FmBuyerApplicationTests extends AbstractTestNGSpringContextTests {

	@Test(description = "Test that the Spring context loads properly")
	public void contextLoads() {
		// This test will pass if the application context loads successfully
	}

}
