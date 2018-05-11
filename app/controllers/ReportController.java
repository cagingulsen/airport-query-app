package controllers;

import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repository.CountryRepository;
import repository.RunwayRepository;
import views.html.reports.reportsMain;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Shows reports from the database.
 */
public class ReportController extends Controller {

    private final CountryRepository countryRepository;
    private final RunwayRepository runwayRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public ReportController(CountryRepository countryRepository,
                            RunwayRepository runwayRepository,
                            HttpExecutionContext httpExecutionContext) {
        this.countryRepository      = countryRepository;
        this.runwayRepository       = runwayRepository;
        this.httpExecutionContext   = httpExecutionContext;
    }

    public Result reportsMain() {
        return ok(reportsMain.render("Reports:"));
    }

    /**
     * Display 10 countries with highest & lowest number of airports report.
     *
     * @return 2 maps, 10 country name-airport count pairs with the highest airport count
     *               , 10 country name-airport count pairs with the lowest airport count
     */
    public CompletionStage<Result> topBottomCountries() {
        // Run a db operation in another thread (using DatabaseExecutionContext)
        return countryRepository.topBottomCountries().thenApplyAsync(pair -> {
            // This is the HTTP rendering thread context
            return ok(views.html.reports.topBottomCountries.render(pair.getLeft(), pair.getRight()));
        }, httpExecutionContext.current());
    }

    /**
     * Display the report showing all countries and their runway types.
     * @return
     */
    public CompletionStage<Result> runwayTypesPerCountry() {
        // Run a db operation in another thread (using DatabaseExecutionContext)
        return countryRepository.runwayTypesPerCountry().thenApplyAsync(map -> {
            // This is the HTTP rendering thread context
            return ok(views.html.reports.runwayTypesPerCountry.render(map));
        }, httpExecutionContext.current());
    }

    /**
     * Display 10 most common runway identifications.
     * @return
     */
    public CompletionStage<Result> mostComRunwayIdent() {
        // Run a db operation in another thread (using DatabaseExecutionContext)
        return runwayRepository.mostComRunwayIdent().thenApplyAsync(list -> {
            // This is the HTTP rendering thread context
            return ok(views.html.reports.mostComRunwayIdent.render(list));
        }, httpExecutionContext.current());
    }
}
