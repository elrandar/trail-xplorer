package com.bourquelot.trailxplorer;

import android.location.Location;
import android.location.LocationManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class gpxParser {

    public static void parse(String path){
        try{
            File inputFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("trkpt");

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

            arraygpx.setlocationArray(new ArrayList<Location>());
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(Double.parseDouble(eElement.getAttribute("lat")));
                    location.setLongitude(Double.parseDouble(eElement.getAttribute("lon")));
                    location.setAltitude(Double.parseDouble(eElement.getElementsByTagName("ele").item(0).getTextContent()));
                    String dateStr = eElement.getElementsByTagName("time").item(0).getTextContent();
                    Date date = df.parse(dateStr);
                    long nanos = date.getTime()*1000000;
                    location.setElapsedRealtimeNanos(nanos);
                    arraygpx.addlocationArray(location);
                    System.out.println("lat : "
                            + eElement.getAttribute("lat"));
                    System.out.println("lon : "
                            + eElement.getAttribute("lon"));
                    System.out.println("Elevation : "
                            + eElement
                            .getElementsByTagName("ele")
                            .item(0)
                            .getTextContent());
                    System.out.println("Time : "
                            + eElement
                            .getElementsByTagName("time")
                            .item(0)
                            .getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
