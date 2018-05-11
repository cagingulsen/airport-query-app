package repository;

import io.ebean.*;
import models.Country;
import play.db.ebean.EbeanConfig;
import util.Pair;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;
/**
 * A repository that executes country database operations.
 */
public class CountryRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public CountryRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Gets 10 countries with the highest & lowest number of airports with airport counts.
     *
     * @return
     */
    public CompletionStage<Pair<Map<String, Integer>,Map<String, Integer>>> topBottomCountries() {

        // 10 countries with the highest number of airports
        // union
        // 10 countries with the lowest number of airports
        String sql =
                        "SELECT cname, airport_count FROM (" +

                            "SELECT cname, airport_count FROM ( " +
                                "SELECT c.cname AS cname, count(a.id) AS airport_count \n" +
                                "FROM airport a \n" +
                                "JOIN country c ON c.code = a.country_code \n" +
                                "GROUP BY c.cname \n" +
                                "ORDER BY airport_count DESC \n" +
                                "LIMIT 10 ) m \n" +

                            "UNION \n" +

                            "SELECT cname, airport_count FROM ( " +
                                "SELECT c.cname AS cname, count(a.id) AS airport_count \n" +
                                "FROM country c \n" +
                                "LEFT JOIN airport a ON c.code = a.country_code \n" +
                                "GROUP BY c.cname \n" +
                                "ORDER BY airport_count ASC \n" +
                                "LIMIT 10) n \n" +
                            ") K";

        RawSql rawSql =
                RawSqlBuilder
                        .parse(sql)
                        .columnMapping("cname",  "name")
                        .columnMapping("airport_count", "airportCount")
                        .create();

        Query<Country> query = ebeanServer.find(Country.class);
        query.setRawSql(rawSql);
        return supplyAsync(() -> query.findList(), executionContext).thenApply(list -> {

                    // sort countries by their airport count

                    // using Java 8 gives error:
                    // list.sort(Comparator.comparing(a -> a.airportCount));

                    // Java 7 line
                    Collections.sort(list, new CountryComparator());

                    if( list.size() != 20 ) {
                        return null;
                    }

                    HashMap<String, Integer> topCountryPairs     = new LinkedHashMap<>();
                    HashMap<String, Integer> bottomCountryPairs  = new LinkedHashMap<>();

                    // 10 countries with the highest number of airports sorted
                    for ( int i = 0 ; i < 10 ; i++ ) {
                        topCountryPairs.put(list.get(i).name, list.get(i).airportCount);
                    }

                    // 10 countries with the lowest number of airports sorted
                    // start from the bottom so that countries with no airports appear first
                    for ( int i = list.size() - 1 ; i > 9 ; i-- ) {
                        bottomCountryPairs.put(list.get(i).name, list.get(i).airportCount);
                    }

                    Pair<Map<String, Integer>, Map<String, Integer>> resultPair =
                            new Pair<>(topCountryPairs, bottomCountryPairs);
                    return resultPair;
                });
    }

    /**
     * Returns a map of country names - country runway types per country.
     * @return
     */
    public CompletionStage<Map<String, String>> runwayTypesPerCountry() {

        String sql =
                        "SELECT DISTINCT c.cname AS cname, r.surface AS surface \n" +
                        "FROM country c, airport a, runway r \n" +
                        "WHERE r.airport_id = a.id AND a.country_code = c.code \n" +
                        "ORDER BY c.cname";

        RawSql rawSql =
                RawSqlBuilder
                        .parse(sql)
                        .columnMapping("cname",  "name")
                        .columnMapping("surface", "surface")
                        .create();

        Query<Country> query = ebeanServer.find(Country.class);
        query.setRawSql(rawSql);
        return supplyAsync(() -> query.findList(), executionContext).thenApply(list -> {

            HashMap<String, List<String>> tempCountrySurfaces = new LinkedHashMap<>();

            // collect runway types of each country int a list
            for( Country c : list ) {
                if( !tempCountrySurfaces.containsKey(c.name) ) {
                    List<String> surfaceList = new ArrayList<>();
                    surfaceList.add(c.surface);
                    tempCountrySurfaces.put(c.name, surfaceList);
                }
                else {
                    List<String> surfaceList = tempCountrySurfaces.get(c.name);
                    surfaceList.add(c.surface);
                }
            }

            HashMap<String, String> countrySurfaces = new LinkedHashMap<>();

            // from the list of runway types, combine them into 1 string, runway types separated by comma
            for (Map.Entry<String, List<String>> entry : tempCountrySurfaces.entrySet()) {

                String countryName      = entry.getKey();
                List<String> surfaces   = entry.getValue();
                StringBuilder sb = new StringBuilder();

                String prefix = "";
                for( String surface : surfaces ) {

                    if( surface != null ) {
                        sb.append(prefix);
                        prefix = ", ";
                        sb.append(surface);
                    }
                }

                countrySurfaces.put(countryName, sb.toString());
            }

            return countrySurfaces;
        });
    }

    /**
     * Return country by the given country code.
     *
     * @param code  country code
     */
    public CompletionStage<Optional<Country>> lookup(String code) {
        return supplyAsync(() -> {
            return Optional.ofNullable(ebeanServer.find(Country.class).setParameter("code", code).findUnique());
        }, executionContext);
    }

    /**
     * Custom country comparator. Uses airportCount field for comparison.
     */
    public class CountryComparator implements Comparator<Country> {
        @Override
        public int compare(Country c1, Country c2) {
            return c2.airportCount.compareTo(c1.airportCount);
        }
    }
}
