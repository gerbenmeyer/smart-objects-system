package utils.kml;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class KMLNamespaceContext implements NamespaceContext {

	@Override
	public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("kml".equals(prefix)) return "http://earth.google.com/kml/2.2";
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String arg0) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String arg0) {
		throw new UnsupportedOperationException();
	}
}