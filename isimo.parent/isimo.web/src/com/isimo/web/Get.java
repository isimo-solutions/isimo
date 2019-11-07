package com.isimo.web;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Get extends AtomicAction {

	Get(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void executeAtomic() throws Exception {
		super.executeAtomic();
		String count = getDefinition().attributeValue("repeatcount");
		int repeatcount = 1;
		if(count!=null) {
			repeatcount = new Integer(count);
		}
		RuntimeException ex = null;
		for (int i = 0; i < repeatcount; i++) {
			try {
				sendRequest();
				return;
			} catch(RuntimeException e) {
				ex = e;
				testExecutionManager.sleep(3);
				System.out.println("Failed, retry ...."+i);
			}
		}
		throw ex;
	}

	void sendRequest() {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();	
			HttpUriRequest request = new HttpGet(getDefinition().attributeValue("url"));
			if(getDefinition().attributeValue("username") != null) {
				httpClient.getCredentialsProvider().setCredentials(
	                    AuthScope.ANY,
	                    new UsernamePasswordCredentials(getDefinition().attributeValue("username"), getDefinition().attributeValue("password")));
			}
			HttpResponse resp = httpClient.execute(request);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resp.getEntity().writeTo(baos);
			baos.flush();
			if(resp.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Service call returned "+resp.getStatusLine().getStatusCode()+" response body is: "+new String(baos.toByteArray()));
			}
			if(getDefinition().attributeValue("response") != null) {
				FileUtils.writeByteArrayToFile(new File(getDefinition().attributeValue("response")), baos.toByteArray());
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
