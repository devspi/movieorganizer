package spi.movieorganizer.controller.tmdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.collection.CollectionPartDM;
import spi.movieorganizer.data.collection.CollectionPartDO;
import spi.movieorganizer.data.company.CompanyDO;
import spi.movieorganizer.data.genre.GenreDM;
import spi.movieorganizer.data.genre.GenreDO;
import spi.movieorganizer.data.movie.MovieDM;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.util.JSonUtilities;
import spi.movieorganizer.data.util.StringMultiLang;
import spi.movieorganizer.display.MovieOrganizerClient;
import spi.movieorganizer.display.MovieOrganizerClient.ActionExecutor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import exane.osgi.jexlib.core.action.Executable;

public class TMDBController implements ITMDBController {
    public static final String   BASE_URL      = "http://d3gtl9l2a4fn1j.cloudfront.net/t/p/";

    private static final String  API_KEY       = "b8066435d00be51cee4903477e68aa95";
    private static final String  API_PROXY     = "http://private-3570-themoviedb.apiary.io/3/";
    private static final String  PARAM_API_KEY = "api_key=" + TMDBController.API_KEY;

    private final GenreDM        genreDM;
    private final ActionExecutor actionExecutor;
    private final Executor       updatesExecutor;

    public TMDBController(final MovieOrganizerClient session, final ActionExecutor actionExecutor) {
        this.genreDM = session.getDataManagerRepository().getGenreDM();
        this.actionExecutor = actionExecutor;
        this.updatesExecutor = session.getUpdatesExecutor();
        initialize();
    }

    private void initialize() {
        retrieveGenres(Locale.FRENCH, Locale.US);
    }

    private void retrieveGenres(final Locale... locales) {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final Map<Integer, GenreDO> genreMap = new HashMap<>();
                    for (final Locale locale : locales) {
                        final URL url = new URL(TMDBController.API_PROXY + "genre/list?" + TMDBController.PARAM_API_KEY + "&language=" + locale.getLanguage());
                        url.openStream();
                        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        final String inputLine = reader.readLine();
                        connection.disconnect();

                        final JsonElement element = new JsonParser().parse(inputLine);
                        final JsonObject jobject = element.getAsJsonObject();
                        final JsonArray genresArray = jobject.get("genres").getAsJsonArray();

                        for (int i = 0; i < genresArray.size(); i++) {
                            final JsonObject jsonGenre = genresArray.get(i).getAsJsonObject();

                            final Integer id = jsonGenre.get("id").getAsInt();
                            final String name = jsonGenre.get("name").getAsString();

                            GenreDO genreDO;
                            if ((genreDO = genreMap.get(id)) == null)
                                genreMap.put(id, new GenreDO(jsonGenre.get("id").getAsInt(), new StringMultiLang(locale, name)));
                            else
                                genreDO.setName(locale, name);
                        }
                    }

                    TMDBController.this.updatesExecutor.execute(new Runnable() {

                        @Override
                        public void run() {
                            for (final GenreDO genreDO : genreMap.values())
                                TMDBController.this.genreDM.addDataObject(genreDO);
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void search(final String query, final Locale locale, final Executable<TMDBRequestResult> callback) {

        final TMDBRequestResult requestResult = new TMDBRequestResult();

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        searchMovie(query, locale, new Executable<MovieDM>() {

            @Override
            public void execute(final MovieDM movieDM) {
                requestResult.setMovieDM(movieDM);
                countDownLatch.countDown();
            }
        }, false);

        searchCollection(query, locale, new Executable<CollectionDM>() {

            @Override
            public void execute(final CollectionDM collectionDM) {
                requestResult.setCollectionDM(collectionDM);
                countDownLatch.countDown();
            }
        }, false);

        try {
            countDownLatch.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        this.updatesExecutor.execute(new Runnable() {

            @Override
            public void run() {
                callback.execute(requestResult);
            }
        });
    }

    @Override
    public void searchMovie(final String query, final Locale locale, final Executable<MovieDM> callback, final boolean executeCallbackInEDT) {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final URL url = new URL(TMDBController.API_PROXY + "search/movie?" + TMDBController.PARAM_API_KEY + "&query=" + URLEncoder.encode(query, "UTF-8")
                            + "&language=" + locale.getLanguage());
                    url.openStream();
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    final String inputLine = reader.readLine();
                    connection.disconnect();

                    final MovieDM movieDM = new MovieDM();
                    final JsonElement element = new JsonParser().parse(inputLine);
                    final JsonObject jobject = element.getAsJsonObject();
                    final JsonArray resultArray = jobject.get("results").getAsJsonArray();

                    for (int i = 0; i < resultArray.size(); i++) {
                        final JsonObject jsonMovie = resultArray.get(i).getAsJsonObject();

                        final Boolean adult = JSonUtilities.getValueAsBoolean("adult", jsonMovie);
                        final String backdropPath = JSonUtilities.getValueAsString("backdrop_path", jsonMovie);
                        final Integer id = JSonUtilities.getValueAsInteger("id", jsonMovie);
                        final String originalTitle = JSonUtilities.getValueAsString("original_title", jsonMovie);
                        final Date releaseDate = JSonUtilities.getValueAsDate("release_date", jsonMovie, "yyyy-MM-dd");
                        final String posterPath = JSonUtilities.getValueAsString("poster_path", jsonMovie);
                        final Double popularity = JSonUtilities.getValueAsDouble("popularity", jsonMovie);
                        final String title = JSonUtilities.getValueAsString("title", jsonMovie);
                        final Double voteAverage = JSonUtilities.getValueAsDouble("vote_average", jsonMovie);
                        final Integer voteCount = JSonUtilities.getValueAsInteger("vote_count", jsonMovie);

                        final MovieDO movie = new MovieDO(id, adult, backdropPath, originalTitle, releaseDate, posterPath, popularity, new StringMultiLang(locale, title),
                                voteAverage, voteCount);

                        movieDM.addDataObject(movie);
                    }

                    if (executeCallbackInEDT)
                        TMDBController.this.updatesExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                callback.execute(movieDM);
                            }
                        });
                    else
                        callback.execute(movieDM);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void searchCollection(final String query, final Locale locale, final Executable<CollectionDM> callback, final boolean executeCallbackInEDT) {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final URL url = new URL(TMDBController.API_PROXY + "search/collection?" + TMDBController.PARAM_API_KEY + "&query=" + URLEncoder.encode(query, "UTF-8")
                            + "&language=" + locale.getLanguage());
                    url.openStream();
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    final String inputLine = reader.readLine();
                    connection.disconnect();

                    final CollectionDM collectionDM = new CollectionDM();
                    final JsonElement element = new JsonParser().parse(inputLine);
                    final JsonObject jobject = element.getAsJsonObject();
                    final JsonArray resultArray = jobject.get("results").getAsJsonArray();

                    for (int i = 0; i < resultArray.size(); i++) {
                        final JsonObject jsonMovie = resultArray.get(i).getAsJsonObject();

                        final Integer id = JSonUtilities.getValueAsInteger("id", jsonMovie);
                        final String backdropPath = JSonUtilities.getValueAsString("backdrop_path", jsonMovie);
                        final String name = JSonUtilities.getValueAsString("name", jsonMovie);
                        final String posterPath = JSonUtilities.getValueAsString("poster_path", jsonMovie);

                        final CollectionDO collectionDO = new CollectionDO(id, backdropPath, new StringMultiLang(locale, name), posterPath);

                        collectionDM.addDataObject(collectionDO);
                    }

                    if (executeCallbackInEDT)
                        TMDBController.this.updatesExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                callback.execute(collectionDM);
                            }
                        });
                    else
                        callback.execute(collectionDM);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void requestMovie(final String movieId, final Locale locale, final Executable<MovieDO> callback, final boolean executeCallbakcInEDT) {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {

                    final URL url = new URL(TMDBController.API_PROXY + "movie/" + movieId + "?" + TMDBController.PARAM_API_KEY + "&language=" + locale.getLanguage());
                    url.openStream();
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    final String inputLine = reader.readLine();
                    connection.disconnect();

                    final JsonElement element = new JsonParser().parse(inputLine);
                    final JsonObject jsonMovie = element.getAsJsonObject();

                    final Boolean adult = JSonUtilities.getValueAsBoolean("adult", jsonMovie);
                    final String backdropPath = JSonUtilities.getValueAsString("backdrop_path", jsonMovie);
                    final Integer id = JSonUtilities.getValueAsInteger("id", jsonMovie);
                    final String originalTitle = JSonUtilities.getValueAsString("original_title", jsonMovie);
                    final Date releaseDate = JSonUtilities.getValueAsDate("release_date", jsonMovie, "yyyy-MM-dd");
                    final String posterPath = JSonUtilities.getValueAsString("poster_path", jsonMovie);
                    final Double popularity = JSonUtilities.getValueAsDouble("popularity", jsonMovie);
                    final String title = JSonUtilities.getValueAsString("title", jsonMovie);
                    final Double voteAverage = JSonUtilities.getValueAsDouble("vote_average", jsonMovie);
                    final Integer voteCount = JSonUtilities.getValueAsInteger("vote_count", jsonMovie);

                    final Double budget = JSonUtilities.getValueAsDouble("budget", jsonMovie);
                    final String homepage = JSonUtilities.getValueAsString("homepage", jsonMovie);
                    final String imdb_id = JSonUtilities.getValueAsString("imdb_id", jsonMovie);
                    final String overview = JSonUtilities.getValueAsString("overview", jsonMovie);
                    final Double revenue = JSonUtilities.getValueAsDouble("revenue", jsonMovie);
                    final Double runtime = JSonUtilities.getValueAsDouble("runtime", jsonMovie);
                    final String tagline = JSonUtilities.getValueAsString("tagline", jsonMovie);
                    final String status = JSonUtilities.getValueAsString("status", jsonMovie);

                    final JsonArray genreArray = jsonMovie.get("genres").getAsJsonArray();
                    final List<Integer> genreIds = new ArrayList<>();
                    for (int i = 0; i < genreArray.size(); i++) {
                        final JsonObject jsonGenre = genreArray.get(i).getAsJsonObject();
                        genreIds.add(JSonUtilities.getValueAsInteger("id", jsonGenre));
                    }

                    final JsonArray companyArray = jsonMovie.get("production_companies").getAsJsonArray();
                    final List<CompanyDO> companies = new ArrayList<>();
                    for (int i = 0; i < companyArray.size(); i++) {
                        final JsonObject jsonCompany = companyArray.get(i).getAsJsonObject();
                        companies.add(new CompanyDO(jsonCompany.get("id").getAsInt(), jsonCompany.get("name").getAsString()));
                    }
                    final MovieDO movieDO = new MovieDO(id, adult, backdropPath, originalTitle, releaseDate, posterPath, popularity, new StringMultiLang(locale, title),
                            voteAverage, voteCount, budget, genreIds, homepage, imdb_id, new StringMultiLang(locale, overview), companies, revenue, runtime, status, tagline);

                    if (executeCallbakcInEDT)
                        TMDBController.this.updatesExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                callback.execute(movieDO);
                            }
                        });
                    else
                        callback.execute(movieDO);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void requestCollection(final String collectionId, final Locale locale, final Executable<CollectionDO> callback, final boolean executeCallbackInEDT) {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {

                    final URL url = new URL(TMDBController.API_PROXY + "collection/" + collectionId + "?" + TMDBController.PARAM_API_KEY + "&language=" + locale.getLanguage());
                    url.openStream();
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    final String inputLine = reader.readLine();
                    connection.disconnect();

                    final JsonElement element = new JsonParser().parse(inputLine);
                    final JsonObject jsonCollection = element.getAsJsonObject();

                    final String backdropPath = JSonUtilities.getValueAsString("backdrop_path", jsonCollection);
                    final Integer id = JSonUtilities.getValueAsInteger("id", jsonCollection);
                    final String posterPath = JSonUtilities.getValueAsString("poster_path", jsonCollection);
                    final String name = JSonUtilities.getValueAsString("name", jsonCollection);

                    final JsonArray collectionPartsArray = jsonCollection.get("parts").getAsJsonArray();
                    final CollectionPartDM collectionPartDM = new CollectionPartDM();
                    for (int i = 0; i < collectionPartsArray.size(); i++) {
                        final JsonObject jsonCollectionPart = collectionPartsArray.get(i).getAsJsonObject();
                        final Integer partId = JSonUtilities.getValueAsInteger("id", jsonCollectionPart);
                        final String partBackdropPath = JSonUtilities.getValueAsString("backdrop_path", jsonCollectionPart);
                        final String partPosterPath = JSonUtilities.getValueAsString("poster_path", jsonCollectionPart);
                        final Date partReleaseDate = JSonUtilities.getValueAsDate("release_date", jsonCollectionPart, "yyyy-MM-dd");
                        final String partTitle = JSonUtilities.getValueAsString("title", jsonCollectionPart);

                        collectionPartDM.addDataObject(new CollectionPartDO(partId, partBackdropPath, partPosterPath, partReleaseDate, new StringMultiLang(locale, partTitle)));
                    }

                    final CollectionDO collectionDO = new CollectionDO(id, backdropPath, new StringMultiLang(locale, name), posterPath);
                    collectionDO.setCollectionPartDM(collectionPartDM);

                    if (executeCallbackInEDT)
                        TMDBController.this.updatesExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                callback.execute(collectionDO);

                            }
                        });
                    else
                        callback.execute(collectionDO);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
