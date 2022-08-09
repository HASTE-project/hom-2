package com.benblamey.hom.manager;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ManagerTest {

    // Note the env variables need to set to get the manager to work outside the container:
    // DATA_PATH=/Users/ben/projects/2021.hom-impl-2/persistentvolume/;KAFKA_BOOTSTRAP_SERVER=dummy

    @Test
    public void testTiers() throws IOException, InterruptedException {

        boolean deleted = new File("serializedTiers.xml").delete();

        Manager m = new Manager();

        // Manager is now empty. No file to deserialize.

        m.addDemoJexlTier();
        m.addDemoJexlTier();
        m.addDemoJexlTier();

        // The XML file has been overwritten several times.
        // Now we instantiate an additional manager, which will trigger deserialization.

        //Manager m2 = new Manager();

        // Now we can assert the number of tiers, their details, etc.

    }

}
