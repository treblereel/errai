package org.jboss.errai.demo.jms.test.client.local;

import org.junit.Test;


public class GwtTestSimple extends AbstractErraiCDITest {
	
    @Override
    public String getModuleName() {
        return "org.jboss.errai.demo.jms.test.client.local.ErraiJmsDemoTest";
    }

    @Test
    public void testNullConstructor(){
            assertFalse(false);
    }
}