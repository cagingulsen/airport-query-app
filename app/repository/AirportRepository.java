package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.PagedList;
import models.Airport;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes airport database operations.
 */
public class AirportRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public AirportRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Return a paged list of airports.
     *
     * @param page     Page to display
     * @param pageSize Number of computers per page
     * @param sortBy   Computer property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public CompletionStage<PagedList<Airport>> pageAirports(int page, int pageSize, String sortBy, String order,
                                                    String filter, Boolean useCountryCode) {

        // TODO find a solution for final String problem
        if(useCountryCode)
            return supplyAsync(() -> {
                return ebeanServer.find(Airport.class).select("id, name, atype, country.code, country.cname")
                        .where().disjunction()
                        .ilike("country.code", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .fetch("country")
                        .setFirstRow(page * pageSize)
                        .setMaxRows(pageSize)
                        .findPagedList();
            } , executionContext);
        else
            return supplyAsync(() -> {
                return ebeanServer.find(Airport.class).select("id, name, atype, country.code, country.cname")
                        .where().disjunction()
                        .ilike( "country.name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .fetch("country")
                        .setFirstRow(page * pageSize)
                        .setMaxRows(pageSize)
                        .findPagedList();
            } , executionContext);
    }

    /**
     * Return airport by the given airport id.
     *
     * @param id    airport id
     */
    public CompletionStage<Optional<Airport>> lookup(Integer id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Airport.class).setId(id).findUnique());
        }, executionContext);
    }
}
