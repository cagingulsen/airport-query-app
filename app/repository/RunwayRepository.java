package repository;

import io.ebean.*;
import models.Runway;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes runway database operations.
 */
public class RunwayRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public RunwayRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Gets the runways of an airport.
     *
     * @param airportId
     * @param sortBy
     * @param order
     * @return
     */
    public CompletionStage<PagedList<Runway>> getRunwaysOfAirport(Long airportId, String sortBy, String order) {


        String sql = "SELECT r.id AS rid, r.surface AS rsurface \n" +
                "FROM runway r \n" +
                "WHERE airport_id = " + airportId;

        RawSql rawSql =
                RawSqlBuilder
                        .parse(sql)
                        .columnMapping("rid", "id")
                        .columnMapping("rsurface", "surface")
                        .create();

        Query<Runway> query = ebeanServer.find(Runway.class);
        query.setRawSql(rawSql);
        return supplyAsync(() -> query.setMaxRows(100).orderBy(sortBy + " " + order)
                .findPagedList(), executionContext);
    }

    /**
     * Return 10 most common runway identifications.
     * @return
     */
    public CompletionStage<Map<String, Integer>> mostComRunwayIdent() {

        String sql = "SELECT le_ident, count(le_ident) AS ident_count \n" +
                "FROM runway r \n" +
                "GROUP BY le_ident \n" +
                "ORDER BY ident_count DESC \n" +
                "LIMIT 10";

        SqlQuery sqlQuery = ebeanServer.createSqlQuery(sql);

        return supplyAsync(() -> sqlQuery.findList(), executionContext).thenApply(rows -> {

            HashMap<String, Integer> mostCommonRunwayIdents = new LinkedHashMap<>();

            for( SqlRow row : rows ) {
                String identificationName   = row.getString("LE_IDENT");
                Integer identificationCount = row.getInteger("IDENT_COUNT");
                mostCommonRunwayIdents.put( identificationName, identificationCount);
            }

            return mostCommonRunwayIdents;
        });
    }

    /**
     * Return runway by the given runway id.
     *
     * @param id    runway id
     */
    public CompletionStage<Optional<Runway>> lookup(Integer id) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Runway.class).setId(id).findUnique());
        }, executionContext);
    }
}
