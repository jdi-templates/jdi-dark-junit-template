package com.epam.jdi.httptests.example;


import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;


@RunWith(JUnitPlatform.class)

@SelectClasses({ TrelloTests.class, ServiceTests.class})
@SuiteDisplayName("suite junit")
public class Suite {
}
