/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.example.remoting.chatroom.test.graphene;

import java.io.File;
import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import static org.jboss.arquillian.graphene.Graphene.*;
import org.jboss.arquillian.graphene.wait.WebDriverWait;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;

/**
 * This class tests functionality of remoting/chatroom example. The test opens
 * two browsers and tests communication between users.
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class ChatroomFunctionalTest extends SeamGrapheneTest {

    public static long timeout = 22000;
    private static String FIRST = "0";
    private static String SECOND = "1";

    @Deployment(testable = false)
    public static EnterpriseArchive createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Before
    @Override
    public void beforeTest() throws MalformedURLException {
        Assume.assumeTrue(isRealBrowser());// need window handling
        super.beforeTest();
    }
    
//    Graphene-WebDriver can't do multiple browsers ARQGRA-72
//    @Drone
//    @Secon
//    protected WebDriver browser2;
    @Test // place holder - should be replaced by better tests as soon as JBSEAM-3944 is resolved
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", getProperty("HOME_PAGE_TITLE"), browser.getTitle());
    }

    @Test
    public void connectAndChatTest() {
        openWindow(contextPath + getProperty("HOME_PAGE"), FIRST);
        openWindow(contextPath + getProperty("HOME_PAGE"), SECOND);

        /*connect user to chat*/
        connect();
        /*verify that user is connected and is seen by other users*/
        verifyConnecting();
        /*exchange several messages*/
        chat();
        /*disconnect user from chat*/
        disconnect();
        /*verify that user is disconnected and is not in a list of users anymore*/
        verifyDisconnecting();
    }

    public void connect() {
        selectWindow(FIRST, false);
        type(getBy("NAME_INPUT"), getProperty("NAME1"));
        clickAndWaitAjax(getBy("CONNECT_BUTTON"));
    }

    public void verifyConnecting() {
        selectWindow(FIRST, false);
        waitForElement("MARTIN_LISTED");

        selectWindow(SECOND, false);
        type(getBy("NAME_INPUT"), getProperty("NAME2"));
        clickAndWaitAjax(getBy("CONNECT_BUTTON"));
        waitForElement("JOZEF_LISTED");
        waitForElement("MARTIN_LISTED");

        selectWindow(FIRST, false);
        waitForText("CHAT_WINDOW", "JOZEF_CONNECTED");
        waitForElement("JOZEF_LISTED");
    }

    public void disconnect() {
        selectWindow(SECOND, false);
        clickAndWaitAjax(getBy("DISCONNECT_BUTTON"));
    }

    public void verifyDisconnecting() {
        selectWindow(SECOND, false);
        waitForElementNotPresent("JOZEF_LISTED");
        waitForElement("DISCONNECT_BUTTON_DISABLED");

        selectWindow(FIRST, false);
        waitForText("CHAT_WINDOW", "JOZEF_DISCONNECTED");
        waitForElementNotPresent("JOZEF_LISTED");
        clickAndWaitAjax(getBy("DISCONNECT_BUTTON"));
        waitForElementNotPresent("MARTIN_LISTED");
        waitForElement("DISCONNECT_BUTTON_DISABLED");
    }

    public void chat() {
        /*first user is sending a message*/
        selectWindow(FIRST, false);
        type(getBy("MESSAGE_INPUT"), getProperty("MESSAGE_FROM_MARTIN"));
        type(getBy("MESSAGE_INPUT"), Keys.RETURN, false);
        waitForText("CHAT_WINDOW", "MARTIN_GT");
        waitForText("CHAT_WINDOW", "MESSAGE_FROM_MARTIN");

        selectWindow(SECOND, false);
        waitForText("CHAT_WINDOW", "MARTIN_GT");
        waitForText("CHAT_WINDOW", "MESSAGE_FROM_MARTIN");

        /*second user is sending a message*/
        type(getBy("MESSAGE_INPUT"), getProperty("MESSAGE_FROM_JOZEF"));
        type(getBy("MESSAGE_INPUT"), Keys.RETURN, false);
        waitForText("CHAT_WINDOW", "JOZEF_GT");
        waitForText("CHAT_WINDOW", "MESSAGE_FROM_JOZEF");

        selectWindow(FIRST, false);
        waitForText("CHAT_WINDOW", "JOZEF_GT");
        waitForText("CHAT_WINDOW", "MESSAGE_FROM_JOZEF");
    }

    private WebDriverWait waitCustom() {
        return new WebDriverWait(null, browser, timeout);
    }

    private void waitForElement(String propertyName, Object... args) {
        waitCustom().until().element(getBy(propertyName, args)).is().present();
    }

    private void waitForText(String elementPropertyName, String textPropertyName, Object... args) {
        waitCustom().until().element(getBy(elementPropertyName, args)).text().contains(getProperty(textPropertyName));
    }

    private void waitForElementNotPresent(String propertyName, Object... args) {
        waitCustom().until(element(getBy(propertyName, args)).not().isPresent());
    }
}
