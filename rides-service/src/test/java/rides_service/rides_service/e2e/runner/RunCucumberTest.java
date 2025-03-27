package rides_service.rides_service.e2e.runner;

import org.junit.platform.suite.api.*;

import static rides_service.rides_service.e2e.runner.RunCucumberTest.GLUE_PROPERTY_NAME;
import static rides_service.rides_service.e2e.runner.RunCucumberTest.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "rides_service.rides_service.e2e.steps"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html")
})
public class RunCucumberTest {
    public static final String GLUE_PROPERTY_NAME = "cucumber.glue";
    public static final String PLUGIN_PROPERTY_NAME = "cucumber.plugin";
}