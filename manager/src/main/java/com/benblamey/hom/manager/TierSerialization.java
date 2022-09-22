package com.benblamey.hom.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


class TierSerialization {

    public static final String SERIALIZED_TIERS_FILENAME = "serializedTiers.json";

    TierSerialization() {
        m_xmlMapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .allowIfBaseType("com.benblamey.hom")
                .allowIfBaseType("java.lang")
                .build();
        m_xmlMapper.activateDefaultTyping(ptv); // default to using DefaultTyping.OBJECT_AND_NON_CONCRETE
    }

    private final ObjectMapper m_xmlMapper;

    public void serializeTiers(List<Tier> tiers) {
        try {
            //String useDir = System.getProperty("user.dir");
            String xmlStr = m_xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tiers);
            FileWriter fileWriter = new FileWriter(SERIALIZED_TIERS_FILENAME,false);
            fileWriter.write(xmlStr);
            //PrintWriter printWriter = new PrintWriter(fileWriter);
            //printWriter.println(xmlStr);
            fileWriter.close(); // also closes filewriter

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Tier> deserializeTiers() {
        if (!new File(SERIALIZED_TIERS_FILENAME).exists()) {
            return new ArrayList<>();
        }

        try {
//            //FileInputStream fis = new FileInputStream();
            FileReader fr = new FileReader(SERIALIZED_TIERS_FILENAME);
//            //Scanner sc = new Scanner(fis);
////            while(sc.hasNextLine())
//                //XmlMapper xmlMapper = new XmlMapper();
//
//            ArrayList<Tier> foo = new ArrayList<Tier>();

            //List<Tier> tiers = (List<Tier>) m_xmlMapper.readerFor(new TypeReference<List<Tier>>() {}).readValue(fr);
            List<Tier> tiers = (List<Tier>) m_xmlMapper.readValue(fr, ArrayList.class);

            //List<Tier> tiers = m_xmlMapper.readValue(fr, foo.getClass());

            fr.close();
            return tiers;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


//    public void serializeTier(Tier tier) {
//        try {
//            String xmlStr = null;
//            XmlMapper xmlMapper = new XmlMapper();
//            //String useDir = System.getProperty("user.dir");
//            xmlStr = xmlMapper.writeValueAsString(tier);
//            FileWriter fileWriter = new FileWriter("serializedTiers.xml",true);
//            PrintWriter printWriter = new PrintWriter(fileWriter);
//            printWriter.println(xmlStr);
//            printWriter.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    public void removeOldTiersXml() {
//        try {
//            File f = new File("serializedTiers.xml");
//            if (f.delete()){
//                System.out.println(f.getName()+" deleted!");
//            }
//            else {
//                System.out.println("Failed!");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}


