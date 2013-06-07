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
package org.jboss.seam.example.seambay.test.graphene;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;

/**
 *
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BidTest extends SeamBayFunctionalTestBase {

    private static final String JSESSIONID = "JSESSIONID";

    @Test
    @InSequence(1)
    public void simpleBidTest() {
        String title = "Lost Tales Volume 1 by J.R.R. Tolkien";
        String price = "10";
        int bidCount;

        login();
        search(title);
        clickAndWaitHttp(getBy("SEARCH_RESULTS_FIRST_ROW_LINK"));
        clickAndWaitHttp(getBy("ITEM_BID_HISTORY"));
        if (isElementPresent(getBy("BID_HISTORY_COUNT_EMPTY"))) {
            bidCount = 0;
        } else {
            bidCount = getXpathCount(getBy("BID_HISTORY_COUNT"));
        }
        browser.navigate().back();
        placeBid(price);
        assertTrue("Auction page expected.", browser.getCurrentUrl().contains(getProperty("AUCTION_URL")));
        clickAndWaitHttp(getBy("ITEM_BID_HISTORY"));
        assertEquals("Unexpected count of bids.", ++bidCount, getXpathCount(getBy("BID_HISTORY_COUNT")));
    }

    @Test
    @InSequence(2)
    public void complexBidTest() {
        String firstBidderName = "honestjoe";
        String secondBidderName = "bidTester";
        String title = "Nikon D80 Digital Camera";

        //SeamSelenium firstBrowser = browser;
        //SeamSelenium secondBrowser = startBrowser();

        Cookie firstSession = null;
        Cookie secondSession = null;

        if (isLoggedIn()) {
            logout();
        }

        // register new user in first browser
        firstSession = browser.manage().getCookieNamed(JSESSIONID);
        clickAndWaitHttp(getBy("REGISTRATION"));
        submitRegistrationForm(secondBidderName, "password", "password", "Slovakia");
        assertTrue("Creating new user failed.", isLoggedIn());

        // place a bid for a camera
        search(title);
        clickAndWaitHttp(getBy("SEARCH_RESULTS_FIRST_ROW_LINK"));
        placeBid("2000");

        // switch to second browser and place several bids
        browser.manage().deleteCookieNamed(JSESSIONID);
        open(contextPath + getProperty("HOME_PAGE"));
        secondSession = browser.manage().getCookieNamed(JSESSIONID);

        login();
        search(title);
        clickAndWaitHttp(getBy("SEARCH_RESULTS_FIRST_ROW_LINK"));
        for (int i = 1100; i < 2000; i += 200) {
            placeBid(String.valueOf(i));
            assertTrue("'You have been outbid' page expected.", isElementPresent(getBy("BID_OUTBID")));
        }
        placeBid("2200");
        assertFalse("Outbid unexpectedly", isElementPresent(getBy("BID_OUTBID")));
        assertEquals("High bidder not recognized.", firstBidderName, getText(getBy("BID_HIGH_BIDDER")));
        
        // switch to first browser again and place the highest bid again
        switchBrowser(firstSession);
        placeBid("2100");
        assertTrue("'You have been outbid' page expected.", isElementPresent(getBy("BID_OUTBID")));
        placeBid("2500");
        assertEquals("High bidder not recognized.", secondBidderName, getText(getBy("BID_HIGH_BIDDER")));
    }

    public void placeBid(String price) {
        if (isElementPresent(getBy("ITEM_NEW_BID_FIELD"))) {
            type(getBy("ITEM_NEW_BID_FIELD"), price);
            clickAndWaitHttp(getBy("ITEM_NEW_BID_SUBMIT"));
        } else if (isElementPresent(getBy("BID_INCREASE_FIELD"))) {
            type(getBy("BID_INCREASE_FIELD"), price);
            clickAndWaitHttp(getBy("BID_INCREASE_SUBMIT"));
        } else {
            fail("Unable to place a bid.");
        }
        clickAndWaitHttp(getBy("BID_CONFIRM"));
    }

    private void switchBrowser(Cookie toSession) {
        browser.manage().deleteCookieNamed(JSESSIONID);
        browser.manage().addCookie(toSession);
    }
}
