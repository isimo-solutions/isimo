package com.isimo.core.properties;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A subclass of Properties that allows recursive references for property values. For example,
 */
public class XProperties extends Properties {
	Logger LOGGER = Logger.getLogger(XProperties.class.getName());

	// The prefix and suffix for constant names
	// within property values
	private static final String START_CONST = "${";
	private static final String END_CONST = "}";

	// The maximum depth for recursive substitution
	// of constants within property values
	// (e.g., A={B} .. B={C} .. C={D} .. etc.)
	private static final int MAX_SUBST_DEPTH = 5;

	/**
	 * Creates an empty property list with no default values.
	 */
	public XProperties() {
		super();
	}

	/**
	 * Creates an empty property list with the specified defaults.
	 * 
	 * @param defaults
	 *            java.util.Properties
	 */
	public XProperties(Properties defaults) {
		super(defaults);
	}

	/**
	 * Searches for the property with the specified key in this property list. If the key is not found in this property list, the default property list, and its defaults, recursively, are then
	 * checked. The method returns <code>null</code> if the property is not found.
	 *
	 * @param key
	 *            the property key.
	 * @return the value in this property list with the specified key value.
	 */
	@Override
	public Object get(Object key) {

		// Return the property value starting at level 0
		return get((String) key, 0);
	}

	/**
	 * Searches for the property with the specified key in this property list. If the key is not found in this property list, the default property list, and its defaults, recursively, are then
	 * checked. The method returns <code>null</code> if the property is not found.
	 *
	 * <p>
	 * The level parameter specifies the current level of recursive constant substitution. If the requested property value includes a constant, its value is substituted in place through a recursive
	 * call to this method, incrementing the level. Once level exceeds MAX_SUBST_DEPTH, no further constant substitutions are performed within the current requested value.
	 *
	 * @param key
	 *            the property key.
	 * @param level
	 *            the level of recursion so far
	 * @return the value in this property list with the specified key value.
	 */
	private String get(String key, int level) {

		String value = (String) super.get(key);
		if (value != null) {

			// Get the index of the first constant, if any
			int beginIndex = 0;
			int startName = value.indexOf(START_CONST, beginIndex);
			while (startName != -1) {
				if (level + 1 > MAX_SUBST_DEPTH) {
					// Exceeded MAX_SUBST_DEPTH
					// Return the value as is
					return value;
				}

				int endName = value.indexOf(END_CONST, startName);
				if (endName == -1) {
					// Terminating symbol not found
					// Return the value as is
					return value;
				}

				String constName = value.substring(startName + START_CONST.length(), endName);
				String constValue = get(constName, level + 1);

				if (constValue == null) {
					// Property name not found
					// Return the value as is
					return value;
				}

				// Insert the constant value into the
				// original property value
				String newValue = (startName > 0) ? value.substring(0, startName) : "";
				newValue += constValue;

				// Start checking for constants at this index
				beginIndex = newValue.length();

				// Append the remainder of the value
				newValue += value.substring(endName + 1);

				value = newValue;

				// Look for the next constant
				startName = value.indexOf(START_CONST, beginIndex);
			}
		}

		// Return the value as is
		return value;
	}
	
	public String getProperty(String key) {
        Object oval = get(key);
        String sval = (oval instanceof String) ? (String)oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
	
	@Override
	public synchronized Enumeration<Object> keys() {
		// TODO Auto-generated method stub
		return Collections.enumeration(new TreeSet<Object>(super.keySet()));
	}
	
	@Override
	public synchronized Object put(Object key, Object value) {
		// TODO Auto-generated method stub
		if(key.toString().contains("timeout"))
			LOGGER.info("KEY="+key+";VALUE="+value);
		return super.put(key, value);
	}
}
