package com.yellerapp.test.endtoend;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import com.yellerapp.client.YellerClient;
import com.yellerapp.client.YellerHTTPClient;

public class EndToEndTest {
	@Test
	public void itReportsAnExceptionToYeller() throws IOException {
		FakeServer server = new FakeServer("localhost", 6666, "/sample-api-key");
		server.start();
		YellerClient client = YellerHTTPClient.withApiKey("sample-api-key").setUrls("http://localhost:6666");
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			client.report(t);
		}
		server.shouldHaveRecordedExceptionWithType("RuntimeException");
		server.stop();
	}

	@Test
	public void itCanReportAnExceptionWithExtraDetail () throws IOException {
		FakeServer server = new FakeServer("localhost", 6666, "/sample-api-key");
		server.start();
		YellerClient client = YellerHTTPClient.withApiKey("sample-api-key").setUrls("http://localhost:6666");
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			HashMap<String, Object> custom = new HashMap<String, Object>();
			custom.put("user_id", 1);
			client.report(t, custom);
		}
		server.shouldHaveRecordedExceptionWithCustomData("user_id", 1);
		server.stop();
	}
}