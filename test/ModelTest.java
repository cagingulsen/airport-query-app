import io.ebean.PagedList;
import models.Airport;
import models.Country;
import models.Runway;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import repository.AirportRepository;
import repository.CountryRepository;
import repository.RunwayRepository;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ModelTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void findAirportById() {
        final AirportRepository airportRepository = app.injector().instanceOf(AirportRepository.class);
        final CompletionStage<Optional<Airport>> stage = airportRepository.lookup(4533);

        await().atMost(1, SECONDS).until(() ->
                assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(airportOptional -> {
                    final Airport amAirport = airportOptional.get();
                    return (amAirport.name.equals("Adnan Menderes International Airport") && amAirport.atype.equals("large_airport")
                            && amAirport.countryCode.equals("TR"));
                })
        );
    }

    @Test
    public void findCountryByCode() {
        final CountryRepository countryRepository = app.injector().instanceOf(CountryRepository.class);
        final CompletionStage<Optional<Country>> stage = countryRepository.lookup("TR");

        await().atMost(1, SECONDS).until(() ->
                assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(countryOptional -> {
                    final Country turkey = countryOptional.get();
                    return (turkey.name.equals("Turkey") && turkey.code.equals("TR"));
                })
        );
    }

    @Test
    public void findRunwayById() {
        final RunwayRepository runwayRepository = app.injector().instanceOf(RunwayRepository.class);
        final CompletionStage<Optional<Runway>> stage = runwayRepository.lookup(239308);

        await().atMost(1, SECONDS).until(() ->
                assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(runwayOptional -> {
                    final Runway r = runwayOptional.get();
                    return (r.id == 239308 && r.airportId == 4533 && r.surface.equals("ASP"));
                })
        );
    }
    
    @Test
    public void pagination() {
        final AirportRepository airportRepository = app.injector().instanceOf(AirportRepository.class);
        CompletionStage<PagedList<Airport>> stage = airportRepository.pageAirports(1, 20, "name", "ASC", "", false);

        // Test the completed result
        await().atMost(1, SECONDS).until(() ->
            assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(airports ->
                    airports.getTotalCount() == 574 &&
                    airports.getTotalPageCount() == 4650 &&
                    airports.getList().size() == 10
            )
        );
    }
}
