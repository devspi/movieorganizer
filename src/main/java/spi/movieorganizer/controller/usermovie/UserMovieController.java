package spi.movieorganizer.controller.usermovie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.collection.CollectionPartDO;
import spi.movieorganizer.data.movie.LoadedMovieData;
import spi.movieorganizer.data.movie.MovieDM;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.movie.UserMovieDM;
import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.data.movie.UserMovieSettings;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieFormat;
import spi.movieorganizer.data.movie.UserMovieSettings.MovieResolution;
import spi.movieorganizer.data.util.JSonUtilities;
import spi.movieorganizer.data.util.StringMultiLang;
import spi.movieorganizer.data.util.TimeTools;
import spi.movieorganizer.display.MovieOrganizerClient;
import spi.movieorganizer.display.MovieOrganizerClient.ActionExecutor;
import spi.movieorganizer.repository.MovieOrganizerConstant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import exane.osgi.jexlib.core.action.Executable;
import exane.osgi.jexlib.core.concurrency.ThrottleQueueExecutor;
import exane.osgi.jexlib.core.type.tuple.DoubleTuple;

public class UserMovieController implements IUserMovieController {

    private final MovieOrganizerClient          session;
    private final ActionExecutor                actionExecutor;
    private final Executor                      updatesExecutor;

    private BufferedWriter                      writer;

    private final UserMovieDM                   userMovieDM;

    private final File                          movieCollectionFile;

    private Gson                                gson;

    private final ThrottleQueueExecutor<String> loadMovieExecutor;

    public UserMovieController(final MovieOrganizerClient session, final ActionExecutor actionExecutor) {
        this.session = session;
        this.updatesExecutor = session.getUpdatesExecutor();
        this.actionExecutor = actionExecutor;

        this.userMovieDM = session.getDataManagerRepository().getUserMovieDM();

        this.movieCollectionFile = new File(MovieOrganizerConstant.MOVIE_COLLECTION_PATH);
        try {
            if (!this.movieCollectionFile.exists())
                this.movieCollectionFile.createNewFile();
            final GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(UserMovieDO.class, new MovieCustomSerializer());
            builder.registerTypeAdapter(UserMovieDO.class, new MovieCustomDesarializer());
            this.gson = builder.create();

            this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.movieCollectionFile, true), "UTF-8"));
            loadUserMovie();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        this.loadMovieExecutor = session.getClientThreadManager().createThrottleQueueExecutor(350);

    }

    private void loadUserMovie() {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                String inputLine;
                try {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(UserMovieController.this.movieCollectionFile), "UTF-8"));
                    while ((inputLine = reader.readLine()) != null) {
                        final UserMovieDO userMovieDO = UserMovieController.this.gson.fromJson(inputLine, UserMovieDO.class);
                        UserMovieController.this.updatesExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                UserMovieController.this.userMovieDM.addDataObject(userMovieDO);
                            }
                        });
                    }
                    reader.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void removeFromUserMovie(final List<Integer> movieIds) {
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final File tempFile = new File("temp.txt");
                    final PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));

                    String line = null;
                    final BufferedReader reader = new BufferedReader(new FileReader(UserMovieController.this.movieCollectionFile));
                    while ((line = reader.readLine()) != null) {
                        final JsonElement element = new JsonParser().parse(line);
                        final JsonObject jsonObject = element.getAsJsonObject();
                        final Integer id = JSonUtilities.getValueAsInteger("id", jsonObject);
                        if (movieIds.contains(id) == false) {
                            printWriter.println(line);
                            printWriter.flush();
                        }
                    }
                    printWriter.close();
                    reader.close();
                    UserMovieController.this.writer.close();

                    if (UserMovieController.this.movieCollectionFile.delete())
                        if (tempFile.renameTo(UserMovieController.this.movieCollectionFile)) {
                            UserMovieController.this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(UserMovieController.this.movieCollectionFile, true),
                                    "UTF-8"));
                            UserMovieController.this.updatesExecutor.execute(new Runnable() {

                                @Override
                                public void run() {
                                    for (final Integer movieId : movieIds)
                                        UserMovieController.this.userMovieDM.removeDataObjectKey(movieId);
                                }
                            });
                        }
                } catch (final Exception e) {

                }
            }
        });
    }

    private void writeToUserMovie(final UserMovieDO userMovieDO, final Runnable callback) {
        try {
            UserMovieController.this.writer.append(UserMovieController.this.gson.toJson(userMovieDO));
            UserMovieController.this.writer.newLine();
            UserMovieController.this.writer.flush();
            UserMovieController.this.updatesExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    System.out.println(userMovieDO.getMovie().getTitle(Locale.FRENCH) + " written");
                    UserMovieController.this.userMovieDM.addDataObject(userMovieDO);
                    callback.run();
                }
            });
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addToUserMovie(final TMDBRequestType type, final Integer itemId, final UserMovieSettings settings, final Runnable onRequestDone) {
        System.out.println("addToUserMovie type=" + type.name());
        switch (type) {
            case Collections:
                this.session.getControllerRepository().getTmdbController().requestCollection(itemId.toString(), Locale.FRENCH, new Executable<CollectionDO>() {

                    @Override
                    public void execute(final CollectionDO collectionDO) {
                        System.out.println(collectionDO.getName(Locale.FRENCH) + " have " + collectionDO.getCollectionPartDM().getDataObjectCount() + " to add");
                        final CountDownLatch countDownLatch = new CountDownLatch(collectionDO.getCollectionPartDM().getDataObjectCount());
                        for (final CollectionPartDO collectionPartDO : collectionDO.getCollectionPartDM())
                            if (UserMovieController.this.userMovieDM.hasDataObjectKey(collectionPartDO.getIdentifier()) == false)
                                UserMovieController.this.loadMovieExecutor.executeQueue(collectionPartDO.getIdentifier().toString(), new Runnable() {

                                    @Override
                                    public void run() {
                                        UserMovieController.this.session.getControllerRepository().getTmdbController()
                                                .requestMovie(collectionPartDO.getIdentifier().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                                                    @Override
                                                    public void execute(final MovieDO movieDO) {
                                                        final UserMovieDO userMovieDO = new UserMovieDO(movieDO.getIdentifier(), movieDO);
                                                        userMovieDO.setSettings(settings);
                                                        writeToUserMovie(userMovieDO, new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                countDownLatch.countDown();
                                                            }
                                                        });
                                                    }
                                                }, false);
                                    }
                                });
                        try {
                            countDownLatch.await();
                            UserMovieController.this.updatesExecutor.execute(onRequestDone);
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, false);
                break;
            case Movies:
                if (this.userMovieDM.hasDataObjectKey(itemId))
                    return;
                this.session.getControllerRepository().getTmdbController().requestMovie(itemId.toString(), Locale.FRENCH, new Executable<MovieDO>() {

                    @Override
                    public void execute(final MovieDO movieDO) {
                        final UserMovieDO userMovieDO = new UserMovieDO(movieDO.getIdentifier(), movieDO);
                        userMovieDO.setSettings(settings);
                        writeToUserMovie(userMovieDO, onRequestDone);
                    }
                }, false);
                break;
        }
    }

    @Override
    public void loadMovieList(final List<String> fileNames, final Executable<DoubleTuple<LoadedMovieData, TMDBRequestResult>> callback) {
        for (final String fileName : fileNames) {
            final LoadedMovieData loadMovie = MovieLoader.getLoadMovie(fileName);
            this.loadMovieExecutor.executeQueue(fileName, new Runnable() {

                @Override
                public void run() {
                    if (loadMovie.isCollection() == false)
                        UserMovieController.this.session.getControllerRepository().getTmdbController().searchMovie(loadMovie.getName(), Locale.FRENCH, new Executable<MovieDM>() {

                            @Override
                            public void execute(final MovieDM movieDM) {
                                final MovieDM exactMovies = new MovieDM();
                                if (loadMovie.getYear() != null && loadMovie.isCollection() == false)
                                    for (final MovieDO movieDO : movieDM) {
                                        final String year = TimeTools.format(TimeTools.yyyy_PATTERN, movieDO.getReleaseDate());
                                        if (year != null && year.equals(loadMovie.getYear()))
                                            exactMovies.addDataObject(movieDO);
                                    }
                                final TMDBRequestResult requestResult = new TMDBRequestResult();
                                if (exactMovies.getDataObjectCount() > 0)
                                    requestResult.setMovieDM(exactMovies);
                                else
                                    requestResult.setMovieDM(movieDM);
                                UserMovieController.this.updatesExecutor.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        callback.execute(new DoubleTuple<LoadedMovieData, TMDBRequestResult>(loadMovie, requestResult));
                                    }
                                });
                            }
                        }, false);
                    else if (loadMovie.isCollection())
                        UserMovieController.this.session.getControllerRepository().getTmdbController()
                                .searchCollection(loadMovie.getName(), Locale.FRENCH, new Executable<CollectionDM>() {

                                    @Override
                                    public void execute(final CollectionDM collectionDM) {
                                        final TMDBRequestResult requestResult = new TMDBRequestResult();
                                        requestResult.setCollectionDM(collectionDM);
                                        UserMovieController.this.updatesExecutor.execute(new Runnable() {

                                            @Override
                                            public void run() {
                                                callback.execute(new DoubleTuple<LoadedMovieData, TMDBRequestResult>(loadMovie, requestResult));
                                            }
                                        });
                                    }
                                }, false);
                }
            });
        }
    }

    private class MovieCustomSerializer implements JsonSerializer<UserMovieDO> {

        @Override
        public JsonElement serialize(final UserMovieDO movie, final Type arg1, final JsonSerializationContext arg2) {
            final JsonObject jsonMovie = new JsonObject();

            jsonMovie.add("id", JSonUtilities.numberToJsonPrimitive(movie.getIdentifier()));
            jsonMovie.add("adult", JSonUtilities.booleanToJsonPrimitive(movie.getMovie().isAdult()));
            jsonMovie.add("backdrop_path", JSonUtilities.stringToJsonPrimitive(movie.getMovie().getBackdropPath()));
            jsonMovie.add("original_title", JSonUtilities.stringToJsonPrimitive(movie.getMovie().getOriginalTitle()));
            jsonMovie.add("release_date", JSonUtilities.numberToJsonPrimitive(movie.getMovie().getReleaseDate().getTime()));
            jsonMovie.add("poster_path", JSonUtilities.stringToJsonPrimitive(movie.getMovie().getPosterPath()));
            jsonMovie.add("popularity", JSonUtilities.numberToJsonPrimitive(movie.getMovie().getPopularity()));
            jsonMovie.add("title", UserMovieController.this.gson.toJsonTree(movie.getMovie().getTitleMultiLang()));
            jsonMovie.add("vote_average", JSonUtilities.numberToJsonPrimitive(movie.getMovie().getVoteAverage()));
            jsonMovie.add("vote_count", JSonUtilities.numberToJsonPrimitive(movie.getMovie().getVoteCount()));

            jsonMovie.add("genres", UserMovieController.this.gson.toJsonTree(movie.getMovie().getGenres()));
            jsonMovie.add("overview", UserMovieController.this.gson.toJsonTree(movie.getMovie().getOverviewMultiLang()));

            jsonMovie.add("seen", JSonUtilities.booleanToJsonPrimitive(movie.getSettings().isSeen()));
            jsonMovie.add("adding_date", JSonUtilities.numberToJsonPrimitive(movie.getAddingDate().getTime()));
            jsonMovie.add("resolution", JSonUtilities.stringToJsonPrimitive(movie.getSettings().getMovieResolution().name()));
            jsonMovie.add("format", JSonUtilities.stringToJsonPrimitive(movie.getSettings().getMovieFormat().name()));
            return jsonMovie;
        }
    }

    private class MovieCustomDesarializer implements JsonDeserializer<UserMovieDO> {

        @Override
        public UserMovieDO deserialize(final JsonElement jsonElement, final Type arg1, final JsonDeserializationContext arg2) throws JsonParseException {
            final JsonObject jsonMovie = jsonElement.getAsJsonObject();
            final Integer id = JSonUtilities.getValueAsInteger("id", jsonMovie);
            final Boolean adult = JSonUtilities.getValueAsBoolean("adult", jsonMovie);
            final String backdropPath = JSonUtilities.getValueAsString("backdrop_path", jsonMovie);
            final String originalTitle = JSonUtilities.getValueAsString("original_title", jsonMovie);
            final Long releaseDate = JSonUtilities.getValuseAsLong("release_date", jsonMovie);
            final String posterPath = JSonUtilities.getValueAsString("poster_path", jsonMovie);
            final Double popularity = JSonUtilities.getValueAsDouble("popularity", jsonMovie);
            final StringMultiLang title = UserMovieController.this.gson.fromJson(jsonMovie.get("title"), StringMultiLang.class);
            final Double voteAverage = JSonUtilities.getValueAsDouble("vote_average", jsonMovie);
            final Integer voteCount = JSonUtilities.getValueAsInteger("vote_count", jsonMovie);
            final StringMultiLang overview = UserMovieController.this.gson.fromJson(jsonMovie.get("overview"), StringMultiLang.class);
            final List<Integer> genres = UserMovieController.this.gson.fromJson(jsonMovie.get("genres"), new TypeToken<List<Integer>>() {
            }.getType());

            final Long addingDate = JSonUtilities.getValuseAsLong("adding_date", jsonMovie);
            final Boolean seen = JSonUtilities.getValueAsBoolean("seen", jsonMovie);
            final String format = JSonUtilities.getValueAsString("format", jsonMovie);
            final String resolution = JSonUtilities.getValueAsString("resolution", jsonMovie);

            final MovieFormat movieFormat = format != null ? MovieFormat.valueOf(format) : MovieFormat.UNKNOWN;
            final MovieResolution movieResolution = resolution != null ? MovieResolution.valueOf(resolution) : MovieResolution.UNKNOWN;

            final MovieDO movieDO = new MovieDO(id, adult, backdropPath, originalTitle, new Date(releaseDate), posterPath, popularity, title, voteAverage, voteCount);
            movieDO.setOverviewMutliLang(overview);
            movieDO.setGenres(genres);

            final UserMovieDO userMovieDO = new UserMovieDO(movieDO.getIdentifier(), movieDO, new Date(addingDate), new UserMovieSettings(seen, movieFormat, movieResolution));

            return userMovieDO;
        }
    }
}
