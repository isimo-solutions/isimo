package com.isimo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.isimo.core.SpringContext;

@PropertySource("file:test.properties")
@ConfigurationProperties
@Configuration
public class IsimoWebProperties {
	public Isimo isimo = new Isimo();
	public Webdriver webdriver = new Webdriver();

	public Firefox firefox = new Firefox();
	
	
	
	
	public Isimo getIsimo() {
		return isimo;
	}

	public void setIsimo(Isimo isimo) {
		this.isimo = isimo;
	}

	public Webdriver getWebdriver() {
		return webdriver;
	}

	public void setWebdriver(Webdriver webdriver) {
		this.webdriver = webdriver;
	}

	public Firefox getFirefox() {
		return firefox;
	}

	public void setFirefox(Firefox firefox) {
		this.firefox = firefox;
	}

	public static class Firefox {
		public String executable;

		public String getExecutable() {
			return executable;
		}

		public void setExecutable(String executable) {
			this.executable = executable;
		}
	}

	public static class Isimo {
		public Ready ready = new Ready();
		public String browser = "firefox", 
				downloads, downloadsunix, downloadswindows;
		public Boolean takescreenshotonerror = true;
		public Boolean closebrowseronerror = true;
		public Boolean closebrowseraftertest = true;
		public int shorttimeout = 40, asserttimeout = 10, invisibilitytimeout = 5, retrycount = 3, maxcounter=5;
		
		
		
		public Boolean getClosebrowseraftertest() {
			return closebrowseraftertest;
		}
		public void setClosebrowseraftertest(Boolean closebrowseraftertest) {
			this.closebrowseraftertest = closebrowseraftertest;
		}
		public int getMaxcounter() {
			return maxcounter;
		}
		public void setMaxcounter(int maxcounter) {
			this.maxcounter = maxcounter;
		}
		public Ready getReady() {
			return ready;
		}
		public void setReady(Ready ready) {
			this.ready = ready;
		}
		public String getBrowser() {
			return browser;
		}
		public void setBrowser(String browser) {
			this.browser = browser;
		}
		public String getDownloads() {
			return downloads;
		}
		public void setDownloads(String downloads) {
			this.downloads = downloads;
		}
		public String getDownloadsunix() {
			return downloadsunix;
		}
		public void setDownloadsunix(String downloadsunix) {
			this.downloadsunix = downloadsunix;
		}
		public String getDownloadswindows() {
			return downloadswindows;
		}
		public void setDownloadswindows(String downloadswindows) {
			this.downloadswindows = downloadswindows;
		}
		public Boolean getTakescreenshotonerror() {
			return takescreenshotonerror;
		}
		public void setTakescreenshotonerror(Boolean takescreenshotonerror) {
			this.takescreenshotonerror = takescreenshotonerror;
		}
		public Boolean getClosebrowseronerror() {
			return closebrowseronerror;
		}
		public void setClosebrowseronerror(Boolean closebrowseronerror) {
			this.closebrowseronerror = closebrowseronerror;
		}
		public int getShorttimeout() {
			return shorttimeout;
		}
		public void setShorttimeout(int shorttimeout) {
			this.shorttimeout = shorttimeout;
		}
		public int getAsserttimeout() {
			return asserttimeout;
		}
		public void setAsserttimeout(int asserttimeout) {
			this.asserttimeout = asserttimeout;
		}
		public int getInvisibilitytimeout() {
			return invisibilitytimeout;
		}
		public void setInvisibilitytimeout(int invisibilitytimeout) {
			this.invisibilitytimeout = invisibilitytimeout;
		}
		public int getRetrycount() {
			return retrycount;
		}
		public void setRetrycount(int retrycount) {
			this.retrycount = retrycount;
		}
		public static class Ready {
			public String script;

			public String getScript() {
				return script;
			}

			public void setScript(String script) {
				this.script = script;
			}
			
			
		}
	}
	

	
	public static class Webdriver {
		Ie ie = new Ie();
		Gecko gecko = new Gecko();
		Edge edge = new Edge();
		
		
		
		
		
		public Edge getEdge() {
			return edge;
		}

		public void setEdge(Edge edge) {
			this.edge = edge;
		}

		public Ie getIe() {
			return ie;
		}

		public void setIe(Ie ie) {
			this.ie = ie;
		}

		public Gecko getGecko() {
			return gecko;
		}

		public void setGecko(Gecko gecko) {
			this.gecko = gecko;
		}

		public Chrome getChrome() {
			return chrome;
		}

		public void setChrome(Chrome chrome) {
			this.chrome = chrome;
		}

		Chrome chrome = new Chrome();
		public static class Ie {
			public boolean nativeevents;
			public String driver;
			@Value("webdriver.ie.driver.log.level")
			public String driverloglevel = "TRACE";
			public boolean isNativeevents() {
				return nativeevents;
			}
			public void setNativeevents(boolean nativeevents) {
				this.nativeevents = nativeevents;
			}
			public String getDriver() {
				return driver;
			}
			public void setDriver(String driver) {
				this.driver = driver;
			}
			public String getDriverloglevel() {
				return driverloglevel;
			}
			public void setDriverloglevel(String driverloglevel) {
				this.driverloglevel = driverloglevel;
			}
			
			
			
			
		}
		
		public static class Chrome {
			public String driver, binary, profile;

			public String getDriver() {
				return driver;
			}

			public void setDriver(String driver) {
				this.driver = driver;
			}

			public String getBinary() {
				return binary;
			}

			public void setBinary(String binary) {
				this.binary = binary;
			}
			
			
		}
		
		public static class Gecko {
			public String driver;

			public String getDriver() {
				return driver;
			}

			public void setDriver(String driver) {
				this.driver = driver;
			}
			
			
			
		}
		
		public static class Edge {
			public String driver;
			public Boolean headless = Boolean.FALSE;

			public String getDriver() {
				return driver;
			}

			public void setDriver(String driver) {
				this.driver = driver;
			}

			public Boolean getHeadless() {
				return headless;
			}

			public void setHeadless(Boolean headless) {
				this.headless = headless;
			}
			
			
		}
	}
	
	public static IsimoWebProperties getInstance() {
		return SpringContext.getBean(IsimoWebProperties.class);
	}
		
}
