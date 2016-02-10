package org.jboss.errai.demo.jms.test.client.local;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.errai.cdi.server.CDIExtensionPoints;
import org.jboss.errai.demo.jms.client.local.JmsDemoClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SSTest {

	@Deployment
	public static WebArchive createDeployment() {
		
		return ShrinkWrap.create(ZipImporter.class, "errai-jms-demo-4.0.0-SNAPSHOT.war").importFrom(new File("target/errai-jms-demo-4.0.0-SNAPSHOT.war"))
	            .as(WebArchive.class);
		
		
	/*	return ShrinkWrap.create(JavaArchive.class).addClass(CDIExtensionPoints.class)
				//.addClass(JmsDemoClient.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");*/
	}

	@Test
	public void testNullConstructor() {
		Assert.assertEquals("1", "2");
	}
}