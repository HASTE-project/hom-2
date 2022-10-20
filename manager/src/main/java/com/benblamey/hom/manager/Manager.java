package com.benblamey.hom.manager;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;



public class Manager {

    Logger logger = LoggerFactory.getLogger(Manager.class);

    private final List<Tier> m_tiers = new ArrayList<Tier>();

    public List<Tier> getTiers() {
        return m_tiers;
    }

    public void cleanup() throws IOException, InterruptedException {
        // Remove existing deployments.
        String deployments = Util.executeShellLogAndBlock(new String[]{"kubectl", "get", "deployments"}).stdOut;
//        NAME                                            READY   UP-TO-DATE   AVAILABLE   AGE
//        engine-0-00dcaf18-7a68-41bb-a9a7-cda5c604a920   0/1     1            0           11m
//        engine-0-49409c05-5f6a-4e31-b4a8-0a1b63ad549a   0/1     1            0           8m1s
//        engine-0-c10c80a8-6764-4a9a-af02-fffa113bfa47   1/1     1            1           3m14s
//        engine-1-167ee486-1332-473e-a445-0e2a538e566e   1/1     1            1           2m3s
//        engine-2-678b789e-3e7c-4133-a8f9-1c667a69514d   1/1     1            1           111s
//        jexl-deployment-foo                             0/1     1            0           17m
//        py-stream-worker-deployment                     1/1     1            1           11m
        for (String line : deployments.split("\\n")) {
            if (line.startsWith("engine-")) {
                String deployment_name = line.split(" +")[0];
                Util.executeShellLogAndBlock(new String[]{"kubectl", "delete", "deployment", deployment_name});
            }
        }

        // Remove any remaining older JEXL pods. (JexlDeploymentPod)
        String getPods = Util.executeShellLogAndBlock(new String[]{"kubectl", "get", "pods"}).stdOut;
//        NAME                                          READY   STATUS             RESTARTS   AGE
//        demo-data                                     1/1     Running            1          47h
//        engine-1ba4f755-17f6-49d5-a884-403a2e63f66d   1/1     Running            0          4m5s
//        engine-30c37dc8-fc07-4c8a-a9af-729bc2af63bc   0/1     CrashLoopBackOff   6          10m
//        kafka                                         1/1     Running            0          114s
//        manager                                       1/1     Running            0          28m
        for (String line : getPods.split("\\n")) {
            if (line.startsWith("engine-")) {
                String pod_name = line.split(" +")[0];
                Util.executeShellLogAndBlock(new String[]{"kubectl", "delete", "pod", pod_name});
            }
        }
    }

    public void addDemoJexlTier() throws IOException, InterruptedException {
        String jexlExpression = "data.foo > " + (m_tiers.size() + 1) * 5;
        addJexlTier(jexlExpression);
    }

    public void addJexlTier(String jexlExpression) throws IOException, InterruptedException {
        // TODO
//        if (!m_tiers.isEmpty()) {
//            throw new RuntimeException("need a base tier");
//        }

        String inputTopic = m_tiers.isEmpty() ? "haste-input-data" : m_tiers.get(m_tiers.size() - 1).getOutputTopic();
        int tierIndex = m_tiers.size();
        Tier tier = new JexlDeploymentTier(jexlExpression, tierIndex, inputTopic);
        TierSerialization ts = new TierSerialization();
        //re-serialize the current tiers
        ts.removeOldTiersXml();
        for (int i = 0; i < m_tiers.size(); i++) {
            Tier tier1 = m_tiers.get(i);
            ts.serializeTier(tier1);
        }
        m_tiers.add(tier);
    }

    public void addNotebookTier(String filenameAndFunction) throws IOException, InterruptedException {
        // TODO
//        if (!m_tiers.isEmpty()) {
//            throw new RuntimeException("need a base tier");
//        }

        String inputTopic = m_tiers.isEmpty() ? "haste-input-data" : m_tiers.get(m_tiers.size() - 1).getOutputTopic();
        int tierIndex = m_tiers.size();
        Tier tier = new PyWorkerDeploymentTier(filenameAndFunction, tierIndex, inputTopic);
        m_tiers.add(tier);
        TierSerialization ts = new TierSerialization();
        //re-serialize the current tiers
        ts.removeOldTiersXml();
        for (int i = 0; i < m_tiers.size(); i++) {
            Tier tier1 = m_tiers.get(i);
            ts.serializeTier(tier1);
        }
    }

    public void removeTier() throws IOException, InterruptedException {
        if (m_tiers.isEmpty()) {
            throw new RuntimeException("no tiers exist to remove");
        }

        Tier tier = m_tiers.get(m_tiers.size() - 1);
        tier.remove();
        // TODO - remove old kafka data?

        m_tiers.remove(tier);
        TierSerialization ts = new TierSerialization();
        //re-serialize the current tiers
        ts.removeOldTiersXml();
        for (int i = 0; i < m_tiers.size(); i++) {
            Tier tier1 = m_tiers.get(i);
            ts.serializeTier(tier1);
        }
    }

    public void addBaseTier(String topicID) {
        if (!getTiers().isEmpty()) {
            throw new RuntimeException("Can only add base tier if no existing tiers");
        }
        TierSerialization ts = new TierSerialization();
        Tier t = new InputTier(topicID);
        ts.serializeTier(t);
        m_tiers.add(t);
    }

    //deserialize tiers when the manager is restarted
    public void deserializeTiers() {
        if (m_tiers.isEmpty()) {
            try {
                FileInputStream fis = new FileInputStream("serializedTiers.xml");
                Scanner sc = new Scanner(fis);
                while(sc.hasNextLine())
                {
                    XmlMapper xmlMapper = new XmlMapper();
                    Tier tier = xmlMapper.readValue(sc.nextLine(), Tier.class);
                    m_tiers.add(tier);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

class TierSerialization {
    public void serializeTier(Tier tier) {
        try {
            String xmlStr = null;
            XmlMapper xmlMapper = new XmlMapper();
            //String useDir = System.getProperty("user.dir");
            xmlStr = xmlMapper.writeValueAsString(tier);
            FileWriter fileWriter = new FileWriter("serializedTiers.xml",true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(xmlStr);
            printWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removeOldTiersXml() {
        try {
            File f = new File("serializedTiers.xml");
            if (f.delete()){
                System.out.println(f.getName()+" deleted!");
            }
            else {
                System.out.println("Failed!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

