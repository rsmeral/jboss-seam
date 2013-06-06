package org.jboss.seam.example.quartz.test;

import java.io.File;

import org.jboss.seam.example.quartz.PaymentProcessor;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {
   /* public static EnterpriseArchive quartzDeployment() {
        EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-quartz.ear").importFrom(new File("../quartz-ear/target/seam-quartz.ear"))
              .as(EnterpriseArchive.class);

        // Install org.jboss.seam.mock.MockSeamListener
        WebArchive web = ear.getAsType(WebArchive.class, "quartz-web.war");
        web.delete("/WEB-INF/web.xml");
        web.addAsWebInfResource("web.xml");
        
        web.addAsResource("BaseData.xml", "BaseData.xml");
        
        web.delete("/WEB-INF/components.xml");
        web.addAsWebInfResource("WEB-INF/components.xml", "components.xml");
        
        JavaArchive ejb =  ear.getAsType(JavaArchive.class, "quartz-ejb.jar");
        ejb.addClasses(TestPaymentController.class, TestPaymentProcessor.class, TransactionStatus.class);
        
        ear.addAsLibraries(DependencyResolvers.use(MavenDependencyResolver.class)
              .configureFrom("pom.xml")
              .artifact("org.dbunit:dbunit:jar:2.2")
              .resolveAsFiles());

        return ear;
    }*/
    
    public static WebArchive quartzDeployment() {
       
       // use profiles defined in 'maven.profiles' property in pom.xml
       String profilesString = System.getProperty("maven.profiles");
       String[] profiles = profilesString != null ? profilesString.split(", ?") : new String[0];
       
       File[] libs = Maven.resolver().loadPomFromFile("pom.xml", profiles)
             .importCompileAndRuntimeDependencies()
             // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
             .resolve("org.jboss.seam:jboss-seam").withTransitivity().asFile();
       
       File[] dbunitLibs = Maven.resolver().loadPomFromFile("pom.xml")
             .resolve("org.dbunit:dbunit:jar:2.2")
             .withoutTransitivity().asFile();
       
       JavaArchive ejb =  ShrinkWrap.create(JavaArchive.class,"quartz-ejb.jar");
       ejb.addPackage(PaymentProcessor.class.getPackage());

       return ShrinkWrap.create(WebArchive.class, "seam-quartz.war")
               .addPackage(AsynchronousTest.class.getPackage())
               .addPackage(PaymentProcessor.class.getPackage())
               .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
               .addAsWebInfResource("components.xml", "components.xml")
               .addAsWebInfResource("persistence.xml", "classes/META-INF/persistence.xml")
               .addAsWebInfResource("import.sql", "classes/import.sql")
               .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
               .addAsWebInfResource("seam.properties", "classes/seam.properties")
               .addAsWebInfResource("seam.quartz.properties", "classes/seam.quartz.properties")
               .addAsWebInfResource("web.xml", "web.xml")
               .addAsWebInfResource("pages.xml", "pages.xml")
               .addAsResource("BaseData.xml", "BaseData.xml")
               .addAsWebResource("index.html")
               .addAsWebResource("search.xhtml")
               .addAsLibraries(libs)
               .addAsLibraries(dbunitLibs);
       
    }
}
