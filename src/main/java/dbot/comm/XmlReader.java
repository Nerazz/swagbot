package dbot.comm;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.net.URL;


import static java.io.File.separator;

/**
 * Created by niklas on 24.08.16.
 */
final class XmlReader {// TODO: abstract machen?
	//private static final String FILE_PATH = "./items.xml";
	//private final static URL xmlURL = XmlReader.class.getClass().getClassLoader().getResource("files" + separator + "items.xml");

	HashMap<String, String> parse(String params) {
		String path = separator + "files" + separator + "items.xml";
		System.out.println(path);
		//URL xmlURL = Statics.class.getClass().getResource(path);
		URL xmlURL = this.getClass().getResource(path);
		try {//TODO: try with resource
			System.out.println("url: '" + xmlURL + "'");
			System.out.println("class: " + this.getClass());
			//System.out.println(new BufferedReader(new InputStreamReader(xmlURL.openStream())).readLine());
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new InputStreamReader(xmlURL.openStream()));
			StartElement startElement = null;
			while(eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				int eventType = event.getEventType();
				if (eventType == XMLStreamConstants.START_ELEMENT) {
					startElement = event.asStartElement();
					String foundElement = startElement.getName().getLocalPart();
					if (foundElement.equals(params)) {//item gefunden
						System.out.println("found: " + foundElement);
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
