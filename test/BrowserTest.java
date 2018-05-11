import org.junit.Test;
import play.api.test.Helpers;
import play.test.WithBrowser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BrowserTest extends WithBrowser {

    @Test
    public void testBrowserMain() {

        browser.goTo("http://localhost:" + Helpers.testServerPort());
        assertThat(browser.$("header h1").first().text(), equalTo("Airports Query Application"));
    }

    @Test
    public void testBrowserAirports() {

        browser.goTo("http://localhost:" + Helpers.testServerPort() + "/airports");

        assertThat(browser.$("header h1").first().text(), equalTo("Airports Query Application"));
        assertThat(browser.$("section h1").first().text(), equalTo("46.505 airports found"));

        assertThat(browser.$("#pagination li.current").first().text(), equalTo("Displaying 1 to 10 of 46505"));

        browser.$("#pagination li.next a").click();
        assertThat(browser.$("#pagination li.current").first().text(), equalTo("Displaying 11 to 20 of 46505"));

        browser.$("#searchbox1").fill().with("urke");
        browser.$("#searchsubmit1").click();
        assertThat(browser.$("section h1").first().text(), equalTo("119 airports found"));

        browser.$("#searchbox2").fill().with("DE");
        browser.$("#searchsubmit2").click();
        assertThat(browser.$("section h1").first().text(), equalTo("703 airports found"));
    }
}
