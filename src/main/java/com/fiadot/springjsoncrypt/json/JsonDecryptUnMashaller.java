package com.fiadot.springjsoncrypt.json;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.util.ClassUtils;

import com.fiadot.springjsoncrypt.util.CipherDecryptUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppReader;

public class JsonDecryptUnMashaller extends XStreamMarshaller {

	private static final Logger logger = LoggerFactory.getLogger(JsonDecryptUnMashaller.class);

	private ClassLoader classLoader;

	private HierarchicalStreamDriver streamDriver;
	
	private String keyAlgorithm;
	private String cipherAlgorithm;
	private String keyString;
	private String initialVector;
	
	@SuppressWarnings("unused")
	private Map<String, Class<?>> toClassMap(Map<String, ?> map) throws ClassNotFoundException {
		Map<String, Class<?>> result = new LinkedHashMap<String, Class<?>>(map.size());

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Class type;
			if (value instanceof Class) {
				type = (Class) value;
			} else if (value instanceof String) {
				String s = (String) value;
				type = ClassUtils.forName(s, classLoader);
			} else {
				throw new IllegalArgumentException("Unknown value [" + value
						+ "], expected String or Class");
			}
			result.put(key, type);
		}
		return result;
	}

	// Unmarshalling
	@Override
	protected Object unmarshalReader(Reader reader) throws XmlMappingException,
			IOException {
		if (streamDriver != null) {
			try {
				return unmarshal(streamDriver.createReader(decode(reader)));
			} catch (Exception e) {
				logger.error("JsonDecryptUnMashaller - unmarshalReader : {}", e.getMessage(), e);
				return e;
			}
		} else {
			return unmarshal(new XppReader(reader));
		}
	}

	private Object unmarshal(HierarchicalStreamReader streamReader) {
		try {
			return this.getXStream().unmarshal(streamReader);
		} catch (Exception ex) {
			throw convertXStreamException(ex, false);
		}
	}

	/**
	 * Marshals the given graph to the given XStream HierarchicalStreamWriter.
	 * Converts exceptions using {@link #convertXStreamException}.
	 */
	@SuppressWarnings("unused")
	private void marshal(Object graph, HierarchicalStreamWriter streamWriter) {
		try {
			getXStream().marshal(graph, streamWriter);
		} catch (Exception ex) {
			throw convertXStreamException(ex, true);
		} finally {
			try {
				streamWriter.flush();
			} catch (Exception ex) {
				logger.debug("Could not flush HierarchicalStreamWriter", ex);
			}
		}
	}

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Set the autodetection mode of XStream.
	 * <p>
	 * <strong>Note</strong> that auto-detection implies that the XStream is
	 * configured while it is processing the XML streams, and thus introduces a
	 * potential concurrency problem.
	 * 
	 * @see XStream#autodetectAnnotations(boolean)
	 */
	public void setAutodetectAnnotations(boolean autodetectAnnotations) {
		this.getXStream().autodetectAnnotations(autodetectAnnotations);
	}

	/**
	 * Set the XStream hierarchical stream driver to be used with stream readers
	 * and writers.
	 */
	public void setStreamDriver(HierarchicalStreamDriver streamDriver) {
		this.streamDriver = streamDriver;
	}
	
	private Reader decode(final Reader reader) throws Exception {

		String encStr = null;
		try {
			char[] arr = new char[4096]; // 8K at a time
			StringBuffer buf = new StringBuffer();
			int numChars;

			while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
				buf.append(arr, 0, numChars);
			}
			
			CipherDecryptUtils cryptoUtil = new CipherDecryptUtils(keyAlgorithm,  cipherAlgorithm,  keyString,  initialVector);
			encStr = cryptoUtil.decrypt(buf.toString());
			logger.debug(
					"JsonUnMashaller$CryptoMessage - decode : \n{} ==> {}",
					buf.toString(), encStr);
		} catch (Exception e) {
			logger.error(
					"JsonUnMashaller$CryptoMessage - decode : -ERR {}",
					e.getMessage(), e);
			throw e;
		}

		return new BufferedReader(new StringReader(encStr));
	}

	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public String getCipherAlgorithm() {
		return cipherAlgorithm;
	}

	public void setCipherAlgorithm(String cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
	}

	public String getKeyString() {
		return keyString;
	}

	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}

	public String getInitialVector() {
		return initialVector;
	}

	public void setInitialVector(String initialVector) {
		this.initialVector = initialVector;
	}
	
}
