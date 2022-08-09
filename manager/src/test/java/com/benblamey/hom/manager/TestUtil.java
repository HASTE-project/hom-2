package com.benblamey.hom.manager;


import com.benblamey.hom.manager.Util;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;



public class TestUtil extends TestCase {

    @Test
    public void testRandomAlphaString() {
        String str = Util.randomAlphaString(10);
        assert str.length() == 10;
        System.out.println(str);
    }

}
