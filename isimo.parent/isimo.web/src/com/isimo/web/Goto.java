package com.isimo.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Goto extends WebAction {
	Goto(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		WebDriverProvider.getInstance().getWebDriver().get(getDefinition().attributeValue("url"));
	}
}
