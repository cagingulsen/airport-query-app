import controllers.routes;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.mvc.Result;
import play.test.WithApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static play.test.Helpers.*;

// Use FixMethodOrder to run the tests sequentially
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FunctionalTest extends WithApplication {

    @Test
    public void listAirports() {
        Result result = route(app, routes.QueryController.airports(0, "name", "asc", "en", false));

        assertThat(result.status()).isEqualTo(OK);
        assertThat(contentAsString(result)).contains("46.505 airports found");
    }

    @Test
    public void filterAirportsByCountryName() {

        Result result1 = route(app, routes.QueryController.airports(0, "name", "asc", "en", false));
        assertThat(result1.status()).isEqualTo(OK);
        assertThat(contentAsString(result1)).contains("2.111 airports found");

        Result result2 = route(app, routes.QueryController.airports(0, "name", "asc", "Netherlands", false));
        assertThat(result2.status()).isEqualTo(OK);
        assertThat(contentAsString(result2)).contains("83 airports found");

        Result result3 = route(app, routes.QueryController.airports(0, "name", "asc", "netherlands", false));
        assertThat(result3.status()).isEqualTo(OK);
        assertThat(contentAsString(result3)).contains("83 airports found");
    }

    @Test
    public void filterAirportsByCountryCode() {

        Result result1 = route(app, routes.QueryController.airports(0, "name", "asc", "R", true));
        assertThat(result1.status()).isEqualTo(OK);
        assertThat(contentAsString(result1)).contains("2.111 airports found");

        Result result2 = route(app, routes.QueryController.airports(0, "name", "asc", "NL", true));
        assertThat(result2.status()).isEqualTo(OK);
        assertThat(contentAsString(result2)).contains("83 airports found");

        Result result3 = route(app, routes.QueryController.airports(0, "name", "asc", "NL", true));
        assertThat(result3.status()).isEqualTo(OK);
        assertThat(contentAsString(result3)).contains("83 airports found");
    }

    @Test
    public void topBottomCountriesTest() {

        Result result1 = route(app, routes.ReportController.topBottomCountries());
        assertThat(result1.status()).isEqualTo(OK);
        // TODO a better way to test numbers

        // 10 countries with the highest number of airports
        assertThat(contentAsString(result1)).contains("United States");
        assertThat(contentAsString(result1)).contains("21501");
        assertThat(contentAsString(result1)).contains("Brazil");
        assertThat(contentAsString(result1)).contains("3839");
        assertThat(contentAsString(result1)).contains("Canada");
        assertThat(contentAsString(result1)).contains("2454");
        assertThat(contentAsString(result1)).contains("Australia");
        assertThat(contentAsString(result1)).contains("1908");
        assertThat(contentAsString(result1)).contains("Russia");
        assertThat(contentAsString(result1)).contains("920");
        assertThat(contentAsString(result1)).contains("France");
        assertThat(contentAsString(result1)).contains("789");
        assertThat(contentAsString(result1)).contains("Argentina");
        assertThat(contentAsString(result1)).contains("713");
        assertThat(contentAsString(result1)).contains("Germany");
        assertThat(contentAsString(result1)).contains("703");
        assertThat(contentAsString(result1)).contains("Colombia");
        assertThat(contentAsString(result1)).contains("700");
        assertThat(contentAsString(result1)).contains("Venezuela");
        assertThat(contentAsString(result1)).contains("592");

        // 10 countries with the lowest number of airports
        assertThat(contentAsString(result1)).contains("Tokelau");
        assertThat(contentAsString(result1)).contains("Pitcairn");
        assertThat(contentAsString(result1)).contains("South Georgia and the South Sandwich Islands");
        assertThat(contentAsString(result1)).contains("Gambia");
        assertThat(contentAsString(result1)).contains("Gibraltar");
        assertThat(contentAsString(result1)).contains("Sint Maarten");
        assertThat(contentAsString(result1)).contains("Curaçao");
        assertThat(contentAsString(result1)).contains("Liechtenstein");
        assertThat(contentAsString(result1)).contains("Mayotte");
        assertThat(contentAsString(result1)).contains("Monaco");
    }

    @Test
    public void mostComRunwayIdentTest() {

        Result result1 = route(app, routes.ReportController.mostComRunwayIdent());
        assertThat(result1.status()).isEqualTo(OK);
        assertThat(contentAsString(result1)).contains("Belgium");
        assertThat(contentAsString(result1)).contains("GRS, PEM, CON, ASP, UNK, Grass, grass");
    }

    @Test
    public void runwayTypesPerCountryTest() {

        Result result1 = route(app, routes.ReportController.runwayTypesPerCountry());
        assertThat(result1.status()).isEqualTo(OK);
        assertThat(contentAsString(result1)).contains("H1");
        assertThat(contentAsString(result1)).contains("5566");
    }
}
