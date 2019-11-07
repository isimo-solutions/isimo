package com.isimo.testcapture;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TestCaptureListenerProcess {

	private static PrintWriter out = null; 

	public static void main(String args[]) {
		try {
/*			ServerEndpointConfig.Configurator configurator = new ServerEndpointConfig.Configurator();
			ServerEndpointConfig.Builder serverEndpointConfigBuilder = ServerEndpointConfig.Builder.create(TestCaptureListenerEndpoint.class, "/testcapture");
			serverEndpointConfigBuilder.configurator(configurator);
			ServerEndpointConfig sec = serverEndpointConfigBuilder.build();
			sec.getUserProperties().put("console", console);*/
			out = new PrintWriter(System.out);
			println("Browser connection closed");
			println("Starting TestCaptureListener Process");
			ServerSocket socket = new ServerSocket(9999);
			Socket clientSocket = socket.accept();
			println("Browser connection established");
			
			BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
			byte[] line = new byte[100000];
			int bytes = -1;
			StringBuffer lineBuffer = new StringBuffer();
			String step = null;
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Socket internalListener = new Socket("localhost",9998);
			internalListener.setKeepAlive(true);
			OutputStream listenerOutputStream = internalListener.getOutputStream();
			while ((bytes = in.read(line)) != -1) {
				try {
					listenerOutputStream.write(line, 0, bytes);
					listenerOutputStream.flush();
				} catch(Exception e) {
					e.printStackTrace(out);
					out.flush();
				}
				//out.println("in = "+lineBuffer.toString());
			}
			clientSocket.close();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static void println(String str) {
		out.println(str);
		out.flush();
	}
	
	
	


	/*private TestCaptureListenerConsole findConsole() {
      ConsolePlugin plugin = ConsolePlugin.getDefault();
      IConsoleManager conMan = plugin.getConsoleManager();
      IConsole[] existing = conMan.getConsoles();
      for (int i = 0; i < existing.length; i++)
         if (TestScenarioPluginConstants.CONSOLE_NAME.equals(existing[i].getName()))
            return (TestCaptureListenerConsole) existing[i];
      //no console found, so create a new one
      TestCaptureListenerConsole myConsole = new TestCaptureListenerConsole(TestScenarioPluginConstants.CONSOLE_NAME, null);
      conMan.addConsoles(new IConsole[]{myConsole});
      return myConsole;
   }*/
}
