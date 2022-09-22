package com.benblamey.hom.manager;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ManagerTest {

    // Note the env variables need to set to get the manager to work outside the container:
    // DATA_PATH=/Users/ben/projects/2021.hom-impl-2/persistentvolume/;KAFKA_BOOTSTRAP_SERVER=dummy

    @Test
    public void testTiers() throws IOException, InterruptedException {

        boolean deleted = new File(TierSerialization.SERIALIZED_TIERS_FILENAME).delete();

        Manager m = new Manager();

        // Manager is now empty. No file to deserialize.

        m.addDemoJexlTier();
        m.addDemoJexlTier();
        m.addDemoJexlTier();

        // The XML file has been overwritten several times.
        // Now we instantiate an additional manager, which will trigger deserialization.

        Manager m2 = new Manager();

        // we get this error:
        // java.lang.RuntimeException: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.benblamey.hom.manager.JexlDeploymentTier` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)

        // Now we can assert the number of tiers, their details, etc.

    }

}
