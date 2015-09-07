package com.fiadot.springjsoncrypt.json;


import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.util.ClassUtils;
import org.springframework.util.xml.StaxUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

import com.fiadot.springjsoncrypt.util.CipherEncryptUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.SaxWriter;
import com.thoughtworks.xstream.io.xml.StaxWriter;

public class JsonEncryptMashaller extends XStreamMarshaller {

	private static final Logger logger = LoggerFactory.getLogger(JsonEncryptMashaller.class);

	private ClassLoader classLoader;

	private HierarchicalStreamDriver streamDriver;
	
	private XStream xStream;
	
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

	// Marshalling

		@Override
		protected void marshalXmlEventWriter(Object graph, XMLEventWriter eventWriter) throws XmlMappingException {
			ContentHandler contentHandler = StaxUtils.createContentHandler(eventWriter);
			marshalSaxHandlers(graph, contentHandler, null);
		}

		@Override
		protected void marshalXmlStreamWriter(Object graph, XMLStreamWriter streamWriter) throws XmlMappingException {
			try {
				marshal(graph, new StaxWriter(new QNameMap(), streamWriter));
			}
			catch (XMLStreamException ex) {
				throw convertXStreamException(ex, true);
			}
		}

		@Override
		protected void marshalSaxHandlers(Object graph, ContentHandler contentHandler, LexicalHandler lexicalHandler)
				throws XmlMappingException {

			SaxWriter saxWriter = new SaxWriter();
			saxWriter.setContentHandler(contentHandler);
			marshal(graph, saxWriter);
		}

		@Override
		public void marshalWriter(Object graph, Writer writer) throws XmlMappingException, IOException {
			
			if (this.xStream != null) {
				try{
					String content = xStream.toXML(graph);
					writer.write(encode(content));
				}catch(Exception e){
					logger.error(
							"marshalWriter$CryptoMessage - encode : -ERR {}",
							e.getMessage(), e);
				}finally{
					if(writer != null){
						writer.flush();
					}
				}
			}else if (this.streamDriver != null) {
				try{
					XStream xStream = new XStream(this.streamDriver);
					xStream.autodetectAnnotations(true);
					String content = xStream.toXML(graph);
					writer.write(encode(content));
				}catch(Exception e){
					logger.error(
							"marshalWriter$CryptoMessage - encode : -ERR {}",
							e.getMessage(), e);
				}finally{
					if(writer != null){
						writer.flush();
					}
				}
			}
			else {
				marshal(graph, new CompactWriter(writer));
			}
		}

		/**
		 * Marshals the given graph to the given XStream HierarchicalStreamWriter.
		 * Converts exceptions using {@link #convertXStreamException}.
		 */
		private void marshal(Object graph, HierarchicalStreamWriter streamWriter) {
			try {
				getXStream().marshal(graph,streamWriter);
			}
			catch (Exception ex) {
				throw convertXStreamException(ex, true);
			}
			finally {
				try {
					streamWriter.flush();
				}
				catch (Exception ex) {
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
	
	private String encode(final String content) throws Exception {

		String encStr = null;
		try {
			
			CipherEncryptUtils cryptoUtil = new CipherEncryptUtils(keyAlgorithm,  cipherAlgorithm,  keyString,  initialVector);
			encStr = cryptoUtil.encrypt(content);
			logger.debug("JsonUnMashaller$CryptoMessage - encode : \n{} ==> {}",content, encStr);
		} catch (Exception e) {
			logger.error("JsonUnMashaller$CryptoMessage - encode : -FAIL content={}",content, e);
			throw e;
		}

		return encStr;
	}

	public XStream getxStream() {
		return xStream;
	}

	public void setxStream(XStream xStream) {
		this.xStream = xStream;
		this.xStream.autodetectAnnotations(true);
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
