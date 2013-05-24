package org.jboss.seam.example.hibernate.test;

import java.io.File;
import org.jboss.seam.example.hibernate.Booking;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {

    public static WebArchive hibernateDeployment() {

        // Get File reference to src/main/webapp/WEB-INF, to avoid duplication of resources for the test archive
        // Depends on shrinkwrap-resolver-maven-plugin to set the property
        String pomFile = System.getProperty("maven.execution.pom-file");
        File webInf = new File(new File(pomFile).getParentFile(), "src/main/webapp/WEB-INF");
        
        File[] libs = Maven.configureResolverViaPlugin().importRuntimeDependencies().asFile();

        return ShrinkWrap.create(WebArchive.class, "hibernate-web.war")
                .addPackage(Booking.class.getPackage())
                
                // copy classpath resources to WAR classpath
                .addAsWebInfResource("import.sql", "classes/import.sql")
                .addAsWebInfResource("seam.properties", "classes/seam.properties")
                .addAsWebInfResource("hibernate.cfg.xml", "classes/hibernate.cfg.xml")
                
                // copy files from src/main/webapp to test-archive's WEB-INF
                .addAsWebInfResource(new File(webInf, "components.xml"))
                .addAsWebInfResource(new File(webInf, "jboss-deployment-structure.xml"))
                .addAsWebInfResource(new File(webInf, "pages.xml"))
                
                // modified web.xml, defines only MockSeamListener
                .addAsWebInfResource("web.xml")
                .addAsLibraries(libs);
    }
}
