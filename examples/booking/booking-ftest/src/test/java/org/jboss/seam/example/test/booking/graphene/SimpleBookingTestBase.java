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
package org.jboss.seam.example.test.booking.graphene;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class tests booking functionality of the example.
 *
 * @author jbalunas
 * @author jharting
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public abstract class SimpleBookingTestBase extends BookingFunctionalTestBase {
    
    @ClassRule
    public static TestRule repeatRule = new TestRule() {
        @Override
        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {

                    Repeat repeat = description.getAnnotation(Repeat.class);

                    if (repeat != null) {
                        for (int i = 0; i < repeat.value(); i++) {
                            try {
                                base.evaluate();
                            } catch (Throwable ex) {
                                Logger.getLogger(SimpleBookingTestBase.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        base.evaluate();
                    }
                }
            };
        }
    };

    protected final String EXPECTED_NAME = "Demo User";

    protected final String CREDIT_CARD = "0123456789012345";

    protected final String CREDIT_CARD_NAME = "visa";
    
    /**
     * Tries searching for non existing hotel.
     */
    
    @Test
    public void invalidSearchStringTest() {
        enterSearchQuery("NonExistingHotel");
        assertTrue("Search failed.", isElementPresent(getBy("NO_HOTELS_FOUND")));
    }

    /**
     * Simply books hotel.
     */
    @Test
    public void simpleBookingTest() {
        String hotelName = "W Hotel";
        int confirmationNumber;
        confirmationNumber = bookHotel(hotelName);
        assertTrue("Booking with confirmation number " + confirmationNumber + " not found.",
                isElementPresent(getBy("BOOKING_TABLE_ITEM", String.valueOf(confirmationNumber), hotelName)));
        cancelBooking(hotelName, confirmationNumber);
    }

    /**
     * Tries booking hotel with incorrect dates.
     */
    @Test
    public void invalidDatesTest() {
        String hotelName = "W Hotel";
        enterSearchQuery(hotelName);
        browser.findElement(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK")).click();

        // hotel page
        browser.findElement(getBy("BOOKING_BOOK")).click();

        // booking page
        String checkOut = browser.findElement(getBy("HOTEL_CHECKOUT_DATE_FIELD")).getAttribute("value");
        populateBookingFields();

        // switch check in and check out date
        setTextInputValue(getBy("HOTEL_CHECKIN_DATE_FIELD"), checkOut);
        browser.findElement(getBy("HOTEL_PROCEED")).click();
        assertTrue("Date verification #1 failed.", isTextInSource(getProperty("BOOKING_INVALID_DATE_MESSAGE1")));
        assertTrue("Check-out date error message expected.", isElementPresent(getBy("HOTEL_CHECKOUT_DATE_MESSAGE")));
        // set check in to past
        setTextInputValue(getBy("HOTEL_CHECKIN_DATE_FIELD"), "01/01/1970");
        browser.findElement(getBy("HOTEL_PROCEED")).click();
        assertTrue("Date verification #2 failed.", isTextInSource(getProperty("BOOKING_INVALID_DATE_MESSAGE2")));
        assertTrue("Checkin-date error message expected.", isElementPresent(getBy("HOTEL_CHECKIN_DATE_MESSAGE")));
    }

    /**
     * This test verifies that user gets right confirmation number when
     * canceling order. https://jira.jboss.org/jira/browse/JBSEAM-3288
     */
    @Test
    public void testJBSEAM3288() {
        String[] hotelNames = new String[]{"Doubletree", "Hotel Rouge", "Conrad Miami"};
        int[] confirmationNumbers = new int[3];
        // make 3 bookings
        for (int i = 0; i < 3; i++) {
            int confirmationNumber = bookHotel(hotelNames[i]);
            confirmationNumbers[i] = confirmationNumber;
        }
        // assert that there bookings are listed in hotel booking list
        for (int i = 0; i < 3; i++) {
            assertTrue("Expected booking #" + i + " not present", isElementPresent(
                    getBy("BOOKING_TABLE_ITEM", String.valueOf(confirmationNumbers[i]), hotelNames[i])));
        }
        // cancel all the reservations
        for (int i = 2; i >= 0; i--) {
            browser.findElement(getBy("BOOKING_TABLE_ITEM_LINK", String.valueOf(confirmationNumbers[i]), hotelNames[i])).click();
            assertTrue("Booking canceling failed", isTextInSource(
                    getProperty("BOOKING_CANCELLED_MESSAGE", String.valueOf(confirmationNumbers[i]))));
        }

    }

    protected int bookHotel(String hotelName, int bed, int smoking, String creditCard, String creditCardName) {
        if (!isLoggedIn()) {
            fail();
        }
        if (!isElementPresent(getBy("SEARCH_SUBMIT"))) {
            open(contextPath + getProperty("MAIN_PAGE"));
        }
        enterSearchQuery(hotelName);
        browser.findElement(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK")).click();

        // booking page
        browser.findElement(getBy("BOOKING_BOOK")).click();

        // hotel page
        populateBookingFields(bed, smoking, creditCard, creditCardName);
        browser.findElement(getBy("HOTEL_PROCEED")).click();

        // confirm page
        browser.findElement(getBy("HOTEL_CONFIRM")).click();
        new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.presenceOfElementLocated(getBy("HOTEL_MESSAGE")));

        // main page
        String message = getText(getBy("HOTEL_MESSAGE"));
        assertTrue("Booking failed. Confirmation message does not match.", message.matches(
                getProperty("BOOKING_CONFIRMATION_MESSAGE", EXPECTED_NAME, hotelName)));
        String[] messageParts = message.split(" ");
        int confirmationNumber = Integer.parseInt(messageParts[messageParts.length - 1]);
        return confirmationNumber;
    }

    protected int bookHotel(String hotelName) {
        return bookHotel(hotelName, 2, 0, CREDIT_CARD, CREDIT_CARD_NAME);
    }

    protected void populateBookingFields(int bed, int smoking, String creditCard, String creditCardName) {
        selectByValue(getBy("HOTEL_BED_FIELD"), bed);
        if (smoking == 1) {
            browser.findElement(getBy("HOTEL_SMOKING_1")).click();
        } else {
            browser.findElement(getBy("HOTEL_SMOKING_2")).click();
        }
        type(getBy("HOTEL_CREDIT_CARD"), creditCard);
        type(getBy("HOTEL_CREDIT_CARD_NAME"), creditCardName);
    }

    protected void populateBookingFields() {
        populateBookingFields(2, 0, CREDIT_CARD, CREDIT_CARD_NAME);
    }
    
    protected void cancelBooking(String hotelName, int confirmationNumber) {
        browser.findElement(getBy("BOOKING_TABLE_ITEM_LINK", String.valueOf(confirmationNumber), hotelName)).click();
    }
}
