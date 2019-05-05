package com.bourquelot.trailxplorer;

import android.location.Location;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class gpxWriter {

    public XmlSerializer xmlSer;
    public StringWriter writer;
    public FileOutputStream stream;
    public File currentFile;
    private SimpleDateFormat df;

    public void startWriting(){

        //Define date encoding
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        try {
        stream = new FileOutputStream(currentFile);
        xmlSer = Xml.newSerializer();
        writer = new StringWriter();
        xmlSer.setOutput(writer);

        xmlSer.startDocument("UTF-8", true);
        xmlSer.startTag(null, "gpx");
        xmlSer.attribute(null, "creator", "trailXplorer");
        xmlSer.attribute(null, "version", "1.0");

        xmlSer.startTag(null, "metadata");
        xmlSer.startTag(null, "time");
        xmlSer.text(df.format(Calendar.getInstance().getTime()));
        xmlSer.endTag(null, "time");
        xmlSer.endTag(null, "metadata");

        xmlSer.startTag(null, "trk");
        xmlSer.startTag(null, "name");
        xmlSer.text("GPX trailXplorer Document");
        xmlSer.endTag(null, "name");

        xmlSer.startTag(null, "trkseg");

        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void writeNewLocation(Location location){
        try {
            xmlSer.startTag(null, "trkpt");
            xmlSer.attribute(null, "lat", String.valueOf(location.getLatitude()));
            xmlSer.attribute(null, "lon", String.valueOf(location.getLongitude()));

            xmlSer.startTag(null, "ele");
            xmlSer.text(String.valueOf(location.getAltitude()));
            xmlSer.endTag(null, "ele");
            xmlSer.startTag(null, "time");
            xmlSer.text((df.format(Calendar.getInstance().getTime())));
            xmlSer.endTag(null, "time");

            xmlSer.endTag(null, "trkpt");
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public void finishWriting(){
        try {
            xmlSer.endTag(null, "trkseg");
            xmlSer.endTag(null, "trk");
            xmlSer.endTag(null, "gpx");
            xmlSer.endDocument();
            xmlSer.flush();

            String dataWrite = writer.toString();
            dataWrite = toPrettyString(dataWrite);
            stream.write(dataWrite.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String toPrettyString(String xml) {
        try {
            // Turn xml string into a document
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            document.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                    document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

