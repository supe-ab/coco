//package com.assignment.product_service.util;
//
//import java.io.StringReader;
//import java.io.StringWriter;
//
//import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.JAXBException;
//import jakarta.xml.bind.Marshaller;
//import jakarta.xml.bind.Unmarshaller;
//
//public class JAXBUtil {
//
//    // Convert POJO to JSON
//    public static String toJSON(Object obj) throws JAXBException {
//        JAXBContext context = JAXBContext.newInstance(obj.getClass());
//        Marshaller marshaller = context.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // Pretty print
//        marshaller.setProperty("eclipselink.media-type", "application/json"); // Specify JSON
//        StringWriter writer = new StringWriter();
//        marshaller.marshal(obj, writer);
//        return writer.toString();
//    }
//
//    // Convert JSON to POJO
//    public static <T> T fromJSON(String json, Class<T> clazz) throws JAXBException {
//        JAXBContext context = JAXBContext.newInstance(clazz);
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//        unmarshaller.setProperty("eclipselink.media-type", "application/json"); // Specify JSON
//        return clazz.cast(unmarshaller.unmarshal(new StringReader(json)));
//    }
//}
