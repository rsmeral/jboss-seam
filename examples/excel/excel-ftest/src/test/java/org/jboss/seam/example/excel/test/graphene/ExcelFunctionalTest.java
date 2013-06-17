package org.jboss.seam.example.excel.test.graphene;

import java.io.File;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunAsClient
@RunWith(Arquillian.class)
public class ExcelFunctionalTest extends SeamGrapheneTest {

    public static final String HOME_PAGE_TITLE = "Microsoft® Excel® Export examples";

    @Deployment(testable = false)
    public static EnterpriseArchive createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    /**
     * Place holder - just verifies that example deploys
     */
    @Test
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", HOME_PAGE_TITLE, browser.getTitle());
    }
}
