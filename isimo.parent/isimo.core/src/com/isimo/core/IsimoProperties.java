package com.isimo.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("file:test.properties")
@ConfigurationProperties
@Configuration
public class IsimoProperties {	
	public Com com = new Com();

	public Isimo isimo = new Isimo();

	
	
	
	
	public Com getCom() {
		return com;
	}

	public void setCom(Com com) {
		this.com = com;
	}


	public Isimo getIsimo() {
		return isimo;
	}

	public void setIsimo(Isimo isimo) {
		this.isimo = isimo;
	}


	public static class Com {
		
		public Isimo isimo = new Isimo();
		
		
		public static class Isimo {
			public String scenarios;

			public String getScenarios() {
				return scenarios;
			}

			public void setScenarios(String scenarios) {
				this.scenarios = scenarios;
			}
		}


		public Isimo getIsimo() {
			return isimo;
		}


		public void setIsimo(Isimo isimo) {
			this.isimo = isimo;
		}
		
		
	}

	
	public static class Isimo {
		public int actiontimeout = 40;
		
		@Value("${isimo.report}")
		public Boolean doreport = false;
		
		@Value("${isimo.report.dir}")
		public String reportDir;

		public int getActiontimeout() {
			return actiontimeout;
		}
		public void setActiontimeout(int actiontimeout) {
			this.actiontimeout = actiontimeout;
		}
		public Boolean getDoreport() {
			return doreport;
		}
		public void setDoreport(Boolean doreport) {
			this.doreport = doreport;
		}
		public String getReportDir() {
			return reportDir;
		}
		public void setReportDir(String reportDir) {
			this.reportDir = reportDir;
		}
	}
	
	
}
