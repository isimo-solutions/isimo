package com.isimo.core.event;

import java.util.HashMap;
import java.util.Map;

public class Message {
	Map<String, String> metadata = new HashMap<String,String>();
	public Message() {
	}
	
	
	public Message(String... metadataValues) {
		if((metadataValues.length / 2)*2 != metadataValues.length)
			throw new RuntimeException("Number of parameters must be even!");
		int i = 0;
		while(i*2 < metadataValues.length) {
			metadata.put(metadataValues[i], metadataValues[(i++)+1]);
		}
	}

	

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> pMetadata) {
		metadata = pMetadata;
	}
}
