package dbot.comm;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by niklas on 24.08.16.
 */
final class XmlReader {// TODO: abstract machen?
	private static final String FILE_PATH = "./items.xml";
	HashMap<String, String> parse(String params) {
		try {//TODO: try with resource
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(FILE_PATH));
			StartElement startElement = null;
			while(eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				int eventType = event.getEventType();
				if (eventType == XMLStreamConstants.START_ELEMENT) {
					startElement = event.asStartElement();
					String foundElement = startElement.getName().getLocalPart();
					if (foundElement.equals(params)) {//item gefunden
						HashMap<String, String> paramMap = new HashMap<>(5, 0.8f);//TODO: loadfactor und co?
						while (eventReader.hasNext()) {//item durchsuchen nach params.substring (mit regex)
							event = eventReader.nextEvent();
							eventType = event.getEventType();
							if (eventType == XMLStreamConstants.START_ELEMENT) {
								paramMap.put(event.asStartElement().getName().getLocalPart(), eventReader.nextEvent().asCharacters().getData());
							}
							if (eventType == XMLStreamConstants.END_ELEMENT) {
								if (event.asEndElement().getName().getLocalPart().equals(foundElement)) {//gefundenes item geschlossen
									return paramMap;
								}
							}
						}
					}
				}
			}
		} catch(IOException e) {
			System.out.println("XML-Datei nicht gefunden: " + e);
		} catch(XMLStreamException e) {
			System.out.println("XMLStreamException: " + e);
		}
		return null;
	}
}
