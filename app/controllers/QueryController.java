package controllers;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repository.AirportRepository;
import repository.RunwayRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Handles airport database queries.
 */
public class QueryController extends Controller {

    private final AirportRepository airportRepository;
    private final RunwayRepository runwayRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public QueryController(FormFactory formFactory,
                          AirportRepository airportRepository,
                          RunwayRepository runwayRepository,
                          HttpExecutionContext httpExecutionContext) {
        this.airportRepository = airportRepository;
        this.runwayRepository = runwayRepository;
        this.httpExecutionContext = httpExecutionContext;
    }

    /**
     * Display the paginated list of airports.
     *
     * @param page   Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order  Sort order (either asc or desc)
     * @param filter Filter applied on computer names
     */
    public CompletionStage<Result> airports(int page, String sortBy, String order, String filter, Boolean useCountryCode) {
        // Run a db operation in another thread (using DatabaseExecutionContext)
        return airportRepository.pageAirports(page, 10, sortBy, order, filter, useCountryCode).thenApplyAsync(list -> {
            // This is the HTTP rendering thread context
            return ok(views.html.queries.airports.render(list, sortBy, order, filter, useCountryCode));
        }, httpExecutionContext.current());
    }

    /**
     * Display the runways of an airport.
     *
     * @param airportId Id of the airport
     * @param sortBy Column to be sorted
     * @param order  Sort order (either asc or desc)
     */
    public CompletionStage<Result> runwaysOfAirport(Long airportId, String sortBy, String order) {
        // Run a db operation in another thread (using DatabaseExecutionContext)
        return runwayRepository.getRunwaysOfAirport(airportId, sortBy, order).thenApplyAsync(list -> {
            // This is the HTTP rendering thread context
            return ok(views.html.queries.runwaysOfAirport.render(list, sortBy, order));
        }, httpExecutionContext.current());
    }
}
