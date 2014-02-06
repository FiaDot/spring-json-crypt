package com.fiadot.springjsoncrypt.json;




import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;


public class JsonMarshallerDriver extends JsonHierarchicalStreamDriver {

	public HierarchicalStreamWriter createWriter(Writer out) {
		// ´Ù³ª¿È ¤»¤»
//		return new JsonWriter(out, JsonWriter.EXPLICIT_MODE, new JsonWriter.Format(new char[0], new char[0], JsonWriter.Format.SPACE_AFTER_LABEL));
//		return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE, new JsonWriter.Format(new char[0], new char[0], JsonWriter.Format.SPACE_AFTER_LABEL));
		return new JsonWriter(out, JsonWriter.STRICT_MODE, new JsonWriter.Format(new char[0], new char[0], JsonWriter.Format.SPACE_AFTER_LABEL));
	}
}
