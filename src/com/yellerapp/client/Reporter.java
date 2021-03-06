package com.yellerapp.client;

public class Reporter {

	private final String apiKey;
	private final String[] urls;
	private HTTPClient http;
	private int currentBackend = 0;
	private YellerErrorHandler handler;
    private Debug debug;

	public Reporter(String apiKey, String[] urls, HTTPClient http,
			YellerErrorHandler handler) {
		this.apiKey = apiKey;
		this.urls = urls;
		this.http = http;
		this.handler = handler;
        this.debug = null;
	}

	public Reporter(String apiKey, String[] urls, HTTPClient http,
			YellerErrorHandler handler, Debug debug) {
		this.apiKey = apiKey;
		this.urls = urls;
		this.http = http;
		this.handler = handler;
        this.debug = debug;
	}

	public void report(FormattedException exception) {
		if (exception.applicationEnvironment != null
				&& (exception.applicationEnvironment.equals("test") || exception.applicationEnvironment
						.equals("development"))) {
			// ignore
		} else {
			report(exception, 0, null);
		}
	}

    protected void debugLog(String message) {
        if (this.debug != null) {
            this.debug.debug(message);
        }
    }

	protected void report(FormattedException exception, int retryCount, Exception previousException) {
		if (retryCount > (2 * urls.length)) {
            this.debugLog("ERROR ran-out-of-retries retry-count=" + retryCount + " last-error=" + previousException.toString());
            this.handler.reportIOError(this.urls[this.currentBackend], previousException);
			return;
		} else {
			try {
                this.debugLog("POST to=" + this.urls[this.currentBackend] + "/" + this.apiKey + " retry-count=" + retryCount);
				http.post(this.urls[this.currentBackend] + "/" + this.apiKey,
						exception);
				this.cycleBackend();
			} catch (AuthorizationException e) {
				this.handler.reportAuthError(this.urls[this.currentBackend], e);
			} catch (Exception e) {
				this.handler.reportIOError(this.urls[this.currentBackend], e);
				this.cycleBackend();
				report(exception, retryCount + 1, e);
			}
		}
	}

	protected synchronized void cycleBackend() {
		this.currentBackend = (this.currentBackend + 1) % urls.length;
	}
}
