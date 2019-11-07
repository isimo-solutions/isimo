package com.isimo.core.model;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ModelValidator extends Validator {
	ErrorHandler errorHandler;

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public LSResourceResolver getResourceResolver() {
		return null;
	}

	@Override
	public void reset() {
	}

	@Override
	public void setErrorHandler(ErrorHandler pParamErrorHandler) {
		this.errorHandler = pParamErrorHandler;
	}

	@Override
	public void setResourceResolver(LSResourceResolver pParamLSResourceResolver) {

	}

	@Override
	public void validate(Source pParamSource, Result pParamResult) throws SAXException, IOException {
		if (!(pParamSource instanceof DOMSource))
			throw new RuntimeException("Only DOMSource accepted by this validator!");
		DOMSource ds = (DOMSource) pParamSource;
		validate(ds.getNode());
	}

	void validate(Node n) {
		if (n.getNodeType() == Node.DOCUMENT_NODE)
			validate(((Document) n).getDocumentElement());
		if (n.getNodeType() != Node.ELEMENT_NODE)
			return;
		Element elem = (Element) n;
		if (elem.hasAttribute("model")) {
			validateModel(elem, elem.getAttribute("model"));
		}
		NodeList nl = elem.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			validate(nl.item(i));
		}
	}

	void validateModel(Element elem, String modelPath) {
		String existingPath = Model.getInstance().pathExists(modelPath);
		if (!existingPath.equals(modelPath)) {
			try {
				errorHandler.error(new SAXParseException("Path " + modelPath + " not found, the only existing path is " + existingPath, getLocatorFromElement(elem)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	Locator getLocatorFromElement(final Element elem) {
		Locator l = new Locator() {

			@Override
			public String getSystemId() {
				// TODO Auto-generated method stub
				return "";
			}

			@Override
			public String getPublicId() {
				// TODO Auto-generated method stub
				return "";
			}

			@Override
			public int getLineNumber() {
				try {
					return (Integer) elem.getUserData("lineNumber");
				} catch (NullPointerException nlp) {
					return 0;
				}
			}

			@Override
			public int getColumnNumber() {
				try {
					return (Integer) elem.getUserData("columnNumber");
				} catch (NullPointerException nlp) {
					return 0;
				}
			}
		};
		return l;
	}

	public static void validateModelPath(Document model, String modelPath) throws ModelValidationException {
		if (modelPath == null || "".equals(modelPath))
			throw new ModelValidationException("Model path can not be empty");
		if (modelPath.endsWith("."))
			throw new ModelValidationException("Model path should not end with '.'");
		List<Map.Entry<String, Element>> definitions = getDefinitionsFromPath(model, modelPath);
		if (definitions.get(definitions.size() - 1).getValue() == null) {
			String correctPath = "";
			for (Map.Entry<String, Element> def : definitions) {
				if (def.getValue() == null) {
					if (!"".equals(correctPath))
						correctPath = correctPath.substring(0, correctPath.length() - 1);
					throw new ModelValidationException("Model path incorrect: The correct prefix is '" + correctPath + "'; the id '" + def.getKey() + "' can't be found in the model");
				} else {
					correctPath += def.getKey() + ".";
				}
			}
		}
	}

	public static List<Map.Entry<String, Element>> getDefinitionsFromPath(Document model, String path) {
		List<Map.Entry<String, Element>> retval = new ArrayList<Map.Entry<String, Element>>();
		StringTokenizer pathtokenizer = new StringTokenizer(path, ".");
		Element currentmodelelement = model.getDocumentElement();
		while (pathtokenizer.hasMoreTokens()) {
			String token = pathtokenizer.nextToken();

			Element modelforpath = null;
			NodeList nl = currentmodelelement.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node child = nl.item(i);
				if (child.getNodeType() == Document.ELEMENT_NODE && token.equals(child.getAttributes().getNamedItem("id").getNodeValue()))
					modelforpath = (Element) child;
			}
			retval.add(new AbstractMap.SimpleEntry<String, Element>(token, modelforpath));
			currentmodelelement = modelforpath;
			if (modelforpath == null) {
				return retval;
			}
		}
		return retval;
	}

	public static List<String> getCompletionsForPrefix(Document model, String prefix) {
		List<Map.Entry<String, Element>> definitionsFromPath = getDefinitionsFromPath(model, prefix);
		Element root = model.getDocumentElement();

		if (!definitionsFromPath.isEmpty()) {
			Map.Entry<String, Element> lastEntry = definitionsFromPath.get(definitionsFromPath.size() - 1);
			if (lastEntry.getValue() != null) {
				root = lastEntry.getValue();
			} else if (definitionsFromPath.size() > 1) {
				root = definitionsFromPath.get(definitionsFromPath.size() - 2).getValue();
			}
		}
		List<String> allPaths = new ArrayList<String>();
		getAllPathsWithPrefix(root, prefix, allPaths);
		return allPaths;
	}

	public static String getPath(Element elem) {
		StringBuffer path = new StringBuffer();
		getPath(elem, path);
		if(path.length() > 0)
			path.deleteCharAt(path.length()-1);
		return path.toString();
	}

	public static void getPath(Element elem, StringBuffer buffer) {
		if (elem.getParentNode() != null && elem.getParentNode().getNodeType() == Node.ELEMENT_NODE)
			getPath((Element) elem.getParentNode(), buffer);
		if(elem.hasAttribute("id"))
			buffer.append(elem.getAttribute("id")).append('.');
	}

	public static void getAllPathsWithPrefix(Element element, String prefix, List<String> allPaths) {
		String path = getPath(element);
		if (path.startsWith(prefix)) {
			allPaths.add(path);
		}
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			if (child.getNodeType() == Document.ELEMENT_NODE)
				getAllPathsWithPrefix((Element) child, prefix, allPaths);
		}
	}
}
