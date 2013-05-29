package org.jboss.seam.example.booking.test;

import java.io.File;
import org.jboss.seam.example.booking.Booking;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {

    public static WebArchive bookingDeployment() {
        
        // use profiles defined in 'maven.profiles' property in pom.xml
        String profilesString = System.getProperty("maven.profiles");
        String[] profiles = profilesString != null ? profilesString.split(", ?") : new String[0];
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml", profiles)
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "seam-booking.war")
                .addPackage(Booking.class.getPackage())
                .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
                .addAsWebInfResource("import.sql", "classes/import.sql")
                .addAsWebInfResource("persistence.xml", "classes/META-INF/persistence.xml")
                .addAsWebInfResource("components.xml", "components.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
                .addAsWebInfResource("seam.properties", "classes/seam.properties")
                .addAsWebInfResource("web.xml", "web.xml")
                .addAsLibraries(libs);
    }
}
