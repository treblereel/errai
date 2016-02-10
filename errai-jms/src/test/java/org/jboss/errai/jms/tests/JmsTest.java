package org.jboss.errai.jms.tests;

import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.URL;

import org.eclipse.jdt.core.dom.ThisExpression;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@RunWith(Arquillian.class)
public abstract class JmsTest {

    private static final String WAR_FILE_NAME = "ErraiJmsDemo.war";
    private static final String MODULE_NAME = "errai-jms";
    private static final String MODULE_VERSION = "4.0.0-SNAPSHOT";

    private static final String[] LIBS = { "com.google.gwt:gwt-user:2.8.0-beta1","com.google.gwt:gwt-dev:2.8.0-beta1",
            "org.jboss.errai:errai-bus:4.0.0-SNAPSHOT", "org.jboss.logging:jboss-logging:3.1.4.GA",
            "org.slf4j:slf4j-api:1.7.2", "org.slf4j:slf4j-log4j12:1.7.2",
            "org.jboss.errai:errai-javaee-all:4.0.0-SNAPSHOT", "org.jboss.errai:errai-jboss-as-support:4.0.0-SNAPSHOT",
            "org.jboss.errai:errai-tools:4.0.0-SNAPSHOT", "org.jboss.errai:errai-ioc:4.0.0-SNAPSHOT",
            "org.jboss.errai:errai-common:4.0.0-SNAPSHOT" };
    
    private static final String[] FILES_TO_ADD = {"target/errai-jms-4.0.0-SNAPSHOT/ErraiJmsDemo","target/classes"};

    private static Logger logger = Logger.getLogger(ThisExpression.class);

    public static WebArchive war() {

        File[] libs = Maven.resolver().loadPomFromFile("../pom.xml").importCompileAndRuntimeDependencies().resolve(LIBS) // Notice
                                                                                                                         // version
                                                                                                                         // number
                .withTransitivity().asFile();

        final WebArchive war = ShrinkWrap.create(WebArchive.class, WAR_FILE_NAME);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setWebXML(new File("src/test/webapp", "WEB-INF/web.xml"))
                .addAsWebInfResource(new File("src/main/resources", "ErraiApp.properties"), "classes/ErraiApp.properties")
                .addAsWebInfResource(new File("src/main/resources", "ErraiService.properties"), "classes/ErraiService.properties")

                .addAsWebResource(new File("src/test/webapp", "ErraiJmsDemo.css"))
                .addAsWebResource(new File("src/test/webapp", "ErraiJmsDemo.html"));
        
        prepareWebApp(war, new File("target/errai-jms-4.0.0-SNAPSHOT").listFiles(),"target/errai-jms-4.0.0-SNAPSHOT/");
        prepareWebInfApp(war, new File("target/classes").listFiles());
        addDeps(war, libs);

        war.as(ZipExporter.class).exportTo(new File("target/" + war.getName()), true);
        logger.warn(war.toString(true));
        return war;
    }

    private static void addDeps(WebArchive war, File[] files) {
        for (File f : files) {
            war.addAsLibraries(f);
        }
    }

    private static void prepareWebApp(WebArchive war, File[] files,String replace) {
        for (File f : files) {
            if (f.isFile()) {
                war.addAsWebResource(f, f.getPath().replaceFirst(replace, ""));
            } else if (f.isDirectory()) {
                File[] childs = f.listFiles();
                if (childs.length > 0) {
                   prepareWebApp(war, childs,replace);
                }
            }
        }
    }
    
    private static void prepareWebInfApp(WebArchive war, File[] files) {
        for (File f : files) {
            if (f.isFile() && !f.getPath().contains("local")) {
                war.addAsWebInfResource(f, f.getPath().replaceFirst("target/", ""));
            } else if (f.isDirectory()) {
                File[] childs = f.listFiles();
                if (childs.length > 0) {
                   prepareWebInfApp(war, childs);
                }
            }
        }
    }

    @ArquillianResource
    URL contextPath;

    @Drone
    WebDriver driver;

    @Deployment(testable = false)
    public static WebArchive gwt() {
        return JmsTest.war();
    }

    public void open() {
        driver.get(contextPath.toString());
    }

}
