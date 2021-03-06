package com.yellerapp.client;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class YellerHTTPClient implements YellerClient,
		java.lang.Thread.UncaughtExceptionHandler {
	private static final Map<String, Object> NO_CUSTOM_DATA = new HashMap<String, Object>();
	private static final YellerExtraDetail NO_EXTRA_DETAIL = new YellerExtraDetail();

	public static String[] DEFAULT_URLS = new String[] {
			"https://collector1.yellerapp.com",
			"https://collector2.yellerapp.com",
			"https://collector3.yellerapp.com",
			"https://collector4.yellerapp.com",
			"https://collector5.yellerapp.com" };

	private final String apiKey;
	private Reporter reporter;

	private String[] urls = DEFAULT_URLS;
	private YellerErrorHandler errorHandler = new STDERRErrorHandler();
	private HTTPClient http = new ApacheHTTPClient();

	private ExceptionFormatter formatter = new ExceptionFormatter(null);

	public YellerHTTPClient(String apiKey) throws Exception {
		this.apiKey = apiKey;
		this.reporter = new Reporter(apiKey, DEFAULT_URLS, http, errorHandler);
		if (Arrays.deepEquals(urls, DEFAULT_URLS)) {
			this.http = new ApacheYellerAppSSLHTTPClient();
			this.reporter = new Reporter(this.apiKey, urls, this.http,
					this.errorHandler);
		} else {
			this.http = new ApacheHTTPClient();
		}
	}

	public static YellerHTTPClient withApiKey(String apiKey) throws Exception {
		return new YellerHTTPClient(apiKey);
	}

	public void report(Throwable t) {
		report(t, NO_CUSTOM_DATA);
	}

	public void report(Throwable t, Map<String, Object> custom) {
		FormattedException formattedException = formatter.format(t,
				NO_EXTRA_DETAIL, custom);
		reporter.report(formattedException);
	}

	public void report(Throwable t, YellerExtraDetail extraDetail,
			Map<String, Object> custom) {
		FormattedException formattedException = formatter.format(t,
				extraDetail, custom);
		reporter.report(formattedException);
	}

	public void report(Throwable t, YellerExtraDetail extraDetail) {
		FormattedException formattedException = formatter.format(t,
				extraDetail, NO_CUSTOM_DATA);
		reporter.report(formattedException);
	}

	public YellerHTTPClient setUrls(String... urls) throws Exception {
		this.reporter = new Reporter(apiKey, urls, http, errorHandler);
		this.urls = urls;
		if (Arrays.deepEquals(this.urls, DEFAULT_URLS)) {
			this.http = new ApacheYellerAppSSLHTTPClient();
			this.reporter = new Reporter(this.apiKey, urls, this.http,
					this.errorHandler);
		} else {
			this.http = new ApacheHTTPClient();
			this.reporter = new Reporter(this.apiKey, urls, this.http,
					this.errorHandler);
		}
		return this;
	}

	public YellerHTTPClient enableDebug() {
		this.reporter = new Reporter(apiKey, urls, http, errorHandler,
				new Debug());
		return this;
	}

	public YellerHTTPClient setErrorHandler(YellerErrorHandler handler) {
		this.reporter = new Reporter(apiKey, this.urls, http, handler);
		return this;
	}

	public void close() {
		this.http.close();
	}

	public void uncaughtException(Thread t, Throwable e) {
		HashMap<String, Object> threadDetail = new HashMap<String, Object>();
		threadDetail.put("id", t.getId());
		threadDetail.put("group", t.getThreadGroup().getName());
		threadDetail.put("priority", t.getPriority());
		threadDetail.put("name", t.getName());
		HashMap<String, Object> detail = new HashMap<String, Object>();
		detail.put("thread", threadDetail);
		report(e, detail);
	}

	public YellerClient setApplicationPackages(String... applicationPackages) {
		this.formatter = new ExceptionFormatter(applicationPackages);
		return this;
	}

}
