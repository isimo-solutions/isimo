package com.isimo.core.xml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.XMLReader;

import com.isimo.core.SpringContext;
import com.isimo.core.TestCases;
import com.isimo.core.TestExecutionManager;

public class LocationSAXReader extends SAXReader {
	private Path lastRead;
	private Path lastReadRelative;
	private String rootDir;
	
	@Autowired
	TestExecutionManager testExecutionManager;
	
	
	
	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	@Override
    protected SAXContentHandler createContentHandler(XMLReader reader) {
        return new LocationSAXContentHandler(getDocumentFactory(),
                getDispatchHandler());
    }

    @Override
    public void setDocumentFactory(DocumentFactory documentFactory) {
        super.setDocumentFactory(documentFactory);
    }
    
    @Override
    public Document read(File pFile) throws DocumentException {
    	File root = new File(SpringContext.getBean(TestExecutionManager.class).getTestRootDir());
    	if(pFile!=null) {
    		lastRead = Paths.get(pFile.getAbsolutePath());
    		Path pathroot = Paths.get(root.getAbsolutePath());
    		lastReadRelative = pathroot.relativize(lastRead);
    	}
    	// TODO Auto-generated method stub
    	return super.read(pFile);
    }

	public Path getLastRead() {
		return lastRead;
	}

	public Path getLastReadRelative() {
		return lastReadRelative;
	}
}
