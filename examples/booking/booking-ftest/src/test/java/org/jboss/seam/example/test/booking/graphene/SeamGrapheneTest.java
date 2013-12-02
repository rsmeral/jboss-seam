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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Base class for all Graphene tests of Seam.
 *
 * @author jbalunas
 * @author jharting
 *
 */
public abstract class SeamGrapheneTest {

    @Rule
    public TestRule watchman = new TestWatcher() {

        private static final int MAX_FILES = 40;

        @Override
        public void failed(Throwable e, Description d) {
            BufferedWriter bw = null;
            PrintWriter pw = null;
            File testOutput = new File("target/test-output");
            if (!testOutput.exists()) {
                testOutput.mkdirs();
            }

            // delete the oldest file
            String[] fileNames = testOutput.list();
            if (fileNames.length == MAX_FILES) {
                Arrays.sort(fileNames);
                new File(testOutput, fileNames[0]).delete();
                new File(testOutput, fileNames[1]).delete();
            }

            try {
                long currentTime = System.currentTimeMillis();
                
                File fileToWrite = new File(testOutput, currentTime + "_" + d.getMethodName() + "_source.html");
                bw = new BufferedWriter(new FileWriter(fileToWrite));
                bw.write(browser.getPageSource());
                bw.close();
                
                File txtToWrite = new File(testOutput, currentTime + "_" + d.getMethodName() + "_report.txt");
                bw = new BufferedWriter(new FileWriter(txtToWrite));
                pw = new PrintWriter(bw);
                pw.println(e.getMessage());
                pw.println();
                e.printStackTrace(pw);
                bw.close();
                pw.close();
            } catch (Exception ex) {
                System.err.println("Can't save HTML/report");
            } finally {
                try {
                    bw.close();
                    pw.close();
                } catch (Exception ex) {
                }
            }
        }
    };

    private static String PROPERTY_FILE = "/ftest.properties";

    private static boolean propertiesLoaded = false;

    private static boolean propertiesExist = false;

    private static Properties properties = new Properties();

    protected WebDriver browser = new HtmlUnitDriver(true);

    public static String getProperty(String key, Object... args) {
        if (!propertiesLoaded) {
            try {
                InputStream is = SeamGrapheneTest.class.getResourceAsStream(PROPERTY_FILE);
                if (is == null) {
                    propertiesLoaded = true;
                    propertiesExist = false;
                } else {
                    properties.load(is);
                    propertiesLoaded = true;
                    propertiesExist = true;
                }
            } catch (IOException e) {
                // ?
            }
        }
        String propValue = propertiesExist ? properties.getProperty(key) : null;
        if (propValue != null && args.length != 0) {
            return MessageFormat.format(propValue, args);
        }
        return propValue;
    }

    public By getBy(String seleniumLocatorProperty, Object... args) {
        String seleniumLocator = getProperty(seleniumLocatorProperty);
        if (seleniumLocator == null) {
            seleniumLocator = seleniumLocatorProperty;
        }
        String locator = seleniumLocator.substring(seleniumLocator.indexOf("=") + 1);
        if (args.length != 0) {
            locator = MessageFormat.format(locator, args);
        }
        if (seleniumLocator.startsWith("id")) {
            return By.id(locator);
        } else if (seleniumLocator.startsWith("xpath")) {
            return By.xpath(locator);
        } else if (seleniumLocator.startsWith("css")) {
            return By.cssSelector(locator);
        } else if (seleniumLocator.startsWith("link")) {
            return By.linkText(locator);
        } else if (seleniumLocator.startsWith("name")) {
            return By.name(locator);
        } else {
            return null;
        }
    }

    public boolean isElementPresent(By by) {
        try {
            return browser.findElement(by).isDisplayed();
        } catch (NotFoundException nfe) {
            return false;
        }
    }

    public String getText(By by) {
        return browser.findElement(by).getText();
    }

    public void check(By by) {
        WebElement checkbox = browser.findElement(by);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public void click(By by) {
        browser.findElement(by).click();
    }

    public void clickAndWaitHttp(By by) {
        browser.findElement(by).click();
    }

    public void clickAndWaitAjax(By by) {
        browser.findElement(by).click();
    }

    public void type(By by, CharSequence text, boolean clear) {
        WebElement elem = browser.findElement(by);
        if (clear) {
            elem.clear();
        }
        elem.sendKeys(text);
    }

    public void type(By by, CharSequence text) {
        type(by, text, true);
    }

    public void setTextInputValue(By by, String value) {
        ((JavascriptExecutor) browser).executeScript("document.getElementById(arguments[0]).value = arguments[1]", browser.findElement(by).getAttribute("id"), value);
    }

    public boolean isTextInSource(String text) {
        //return (Boolean) Graphene.element(By.tagName("body")).text().contains(text).apply(browser);
        return browser.getPageSource().contains(text);
    }

    public boolean isTextOnPage(String text) {
        return (Boolean) browser.findElement(By.tagName("body")).getText().contains(text);
    }

    public void selectByValue(By by, Object value) {
        new Select(browser.findElement(by)).selectByValue(String.valueOf(value));
    }

    public void selectByText(By by, Object value) {
        new Select(browser.findElement(by)).selectByVisibleText(String.valueOf(value));
    }

    public void selectByIndex(By by, int index) {
        new Select(browser.findElement(by)).selectByIndex(index);
    }

    public void openWindow(String url, String name) {
        ((JavascriptExecutor) browser).executeScript(MessageFormat.format("window.open(\"{0}\", \"{1}\")", url, name));
    }

    public void selectWindow(String windowNameOrHandle) {
        selectWindow(windowNameOrHandle, true);
    }

    public void selectWindow(String windowNameOrHandle, boolean refresh) {
        browser.switchTo().window(windowNameOrHandle);
        if (refresh) {
            browser.navigate().refresh();
        }
    }

    public void open(String url) {
        browser.navigate().to(url);
        sleep(1000);// the Navigation.to doesn't seem to block as well as it should
    }

    public int getXpathCount(By by) {
        return browser.findElements(by).size();
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }

    public boolean isRealBrowser() {
        String webDriverClass = browser.getClass().toString().toLowerCase();
        return !webDriverClass.contains("phantom") && !webDriverClass.contains("htmlunit");
    }
}
