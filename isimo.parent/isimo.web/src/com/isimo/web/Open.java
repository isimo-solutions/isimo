package com.isimo.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.isimo.core.Action;
import com.isimo.core.AtomicAction;
import com.isimo.core.TestCases;
import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Open extends WebAction {
	public Open(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	@Override
	public void executeAtomic() throws Exception {
		WebDriver driver = WebDriverProvider.getInstance().getWebDriver();
		if(driver==null) {
			super.executeAtomic();
			File file = new File(System.getProperty("user.home")+"/Downloads/selenium_downloads");
			if(file.exists() && !"false".equals(getDefinition().attributeValue("deletedownloads")))
				FileUtils.deleteDirectory(file);
			file.mkdirs();
			isimoWebProperties.isimo.downloads = file.getAbsolutePath();
			isimoWebProperties.isimo.downloadsunix = isimoWebProperties.isimo.downloads.replace("\\", "/");
			isimoWebProperties.isimo.downloadswindows = isimoWebProperties.isimo.downloads.replace("\\", "\\\\");
			if("firefox".equals(isimoWebProperties.isimo.browser)) {
				if(isimoWebProperties.webdriver.gecko.driver!=null) {
					System.setProperty("webdriver.gecko.driver", isimoWebProperties.webdriver.gecko.driver);
					//System.setProperty("webdriver.firefox.marionette", "true");
				}
				System.out.println("webdriver.gecko.driver="+System.getProperty("webdriver.gecko.driver"));
				System.out.println("firefox.executable="+isimoWebProperties.firefox.executable);
				FirefoxBinary fb = new FirefoxBinary(new File(isimoWebProperties.firefox.executable));
				FirefoxOptions options = new FirefoxOptions();
				options.addPreference("browser.download.dir",isimoWebProperties.isimo.downloads);
				options.addPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream");
			    options.addPreference("extensions.logging.enabled", false);
			    options.addPreference("browser.download.folderList",2);
				options.addPreference("browser.download.manager.showWhenStarting",false);
				options.addPreference("network.proxy.type", 0);
				options.addPreference("network.proxy.type", 0);
	
				/*String testcaptureExtensionPath = TestCases.getInstance().getProperties().getProperty("isimo.testcapture.extension");
			    if(StringUtils.isNotEmpty(testcaptureExtensionPath)) {
					File testcaptureExtension = new File(TestCases.getInstance().getProperties().getProperty("isimo.testcapture.extension")).getAbsoluteFile();
					log("Installing capture extension from "+testcaptureExtension);
					if(testcaptureExtension.exists()) {
						fp.addExtension(testcaptureExtension);
					}
			    }*/
			    
			    /*
			    options.addArguments("-vv");
			    
			   
	
			    options.addPreference("extensions.logging.enabled", false);
			    options.addPreference("app.update.enabled", false);
			    options.addPreference("app.update.service.enabled", false);
			    options.addPreference("app.update.auto", false);
			    options.addPreference("app.update.staging.enabled", false);
			    options.addPreference("app.update.silent", false);
			    options.addPreference("media.gmp-provider.enabled", false);
	
			    options.addPreference("extensions.update.autoUpdate", false);
			    options.addPreference("extensions.update.autoUpdateEnabled", false);
			    options.addPreference("extensions.update.enabled", false);
			    options.addPreference("extensions.update.autoUpdateDefault", false);
			    options.addPreference("browser.helperApps.alwaysAsk.force", false);
			    options.addPreference("browser.download.folderList",2);
			    options.addPreference("browser.download.manager.showWhenStarting",false);
			    options.addPreference("browser.download.panel.shown",false);
			        options.addPreference("extensions.blocklist.enabled", false);
			    options.addPreference("browser.download.manager.alertOnEXEOpen", false);
			    options.addPreference("browser.download.manager.focusWhenStarting", false);
			    options.addPreference("browser.download.manager.useWindow", true);
			    options.addPreference("browser.download.manager.closeWhenDone", true);
			    options.addPreference("media.gmp-manager.cert.requireBuiltIn",false);
			    options.addPreference("media.gmp-manager.cert.checkAttributes",false);
			    options.addPreference("media.gmp-provider.enabled",false);
			    options.addPreference("media.gmp-widevinecdm.enabled",false);
			    options.addPreference("media.gmp-widevinecdm.visible",false);
			    options.addPreference("media.gmp.trial-create.enabled",false);
			    options.addPreference("marionette.port","19999");
			    options.addPreference("marionette.log.level","TRACE");*/
			    //options.addPreference("extensions.bootstrappedAddons", "{}");
			    //options.setCapability("marionette", false);
			    options.setBinary(fb);
			    FirefoxDriver fd = new FirefoxDriver(options);
				driver = fd;
	
			} else if("internetExplorer".equals(isimoWebProperties.isimo.browser)) {
				DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
	
				caps.setCapability("nativeEvents", Boolean.parseBoolean(getProperties().getProperty("webdriver.ie.nativeevents")));
				//caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				setSystemProperty("webdriver.ie.driver", isimoWebProperties.webdriver.ie.driver); 
				setSystemProperty("webdriver.ie.driver.loglevel", isimoWebProperties.webdriver.ie.driverloglevel);
				setSystemProperty("webdriver.ie.driver.logfile", isimoProperties.isimo.reportDir+File.separator+"ie.log", null);
				caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				caps.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
				caps.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
				//caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				Map map = new HashMap();
				caps.setCapability("proxy", map);
				InternetExplorerOptions options = new InternetExplorerOptions(caps);
				InternetExplorerDriver iedriver = new InternetExplorerDriver(options);
				driver = iedriver;
				String path = isimoWebProperties.isimo.downloads;
				String cmd1 = "REG ADD \"HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\Main\" /F /V \"Default Download Directory\" /T REG_SZ /D "+ path;
				try {
				    Runtime.getRuntime().exec(cmd1);
				} catch (Exception e) {
				    throw new RuntimeException("Coulnd't change the registry for default directory for IE");
				}
			} else if("chrome".equals(isimoWebProperties.isimo.browser)) {
				System.setProperty("webdriver.chrome.driver", isimoWebProperties.webdriver.chrome.driver);
				ChromeOptions options = new ChromeOptions();
				if(null!=isimoWebProperties.webdriver.chrome.binary)
					options.setBinary(isimoWebProperties.webdriver.chrome.binary);
				ChromeDriver chromedriver = new ChromeDriver(options);
				driver = chromedriver;
			} else {
				throw new RuntimeException("Browser not supported: "+getProperties().get("browser.type"));
			}
			WebDriverProvider.getInstance().setWebDriver(driver);
		}
		driver.get(getDefinition().attributeValue("url"));
	}
	
	public void setSystemProperty(String name, String value, String defaultValue) {
		if(value!=null)
			System.setProperty(name, value);
		else
			System.setProperty(name, defaultValue);
	}
	
	public void setSystemProperty(String name, String value) {
		setSystemProperty(name, value, "");
	}
}
