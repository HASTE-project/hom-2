package com.benblamey.hom.manager;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class TierSerialization {

    XmlMapper m_xmlMapper = new XmlMapper();

    public void serializeTiers(List<Tier> tiers) {
        try {
            //String useDir = System.getProperty("user.dir");
            String xmlStr = m_xmlMapper.writeValueAsString(tiers);
            FileWriter fileWriter = new FileWriter("serializedTiers.xml",false);
            fileWriter.write(xmlStr);
            //PrintWriter printWriter = new PrintWriter(fileWriter);
            //printWriter.println(xmlStr);
            fileWriter.close(); // also closes filewriter

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Tier> deserializeTiers() {
        try {
            //FileInputStream fis = new FileInputStream();
            FileReader fr = new FileReader("serializedTiers.xml");
            //Scanner sc = new Scanner(fis);
//            while(sc.hasNextLine())
                //XmlMapper xmlMapper = new XmlMapper();
            List<Tier> tiers = m_xmlMapper.readValue(fr, ArrayList.class);
            return tiers;

        } catch (FileNotFoundException e) {
            // If there is no serialized state, start with an empty set of tiers.
            return new ArrayList<Tier>();
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


