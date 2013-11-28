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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult;
import spi.movieorganizer.data.collection.CollectionDM;
import spi.movieorganizer.data.collection.CollectionDO;
import spi.movieorganizer.data.collection.CollectionPartDO;
import spi.movieorganizer.data.movie.LoadMovie;
import spi.movieorganizer.data.movie.MovieDM;
import spi.movieorganizer.data.movie.MovieDO;
import spi.movieorganizer.data.movie.UserMovieDM;
import spi.movieorganizer.data.movie.UserMovieDO;
import spi.movieorganizer.data.movie.UserMovieDO.MovieFormat;
import spi.movieorganizer.data.movie.UserMovieDO.MovieResolution;
import spi.movieorganizer.data.util.JSonUtilities;
import spi.movieorganizer.data.util.StringMultiLang;
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
import com.google.gson.JsonPrimitive;
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
        this.movieCollectionFile.setWritable(true);
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
    public void removeFromUserMovie(final Integer movieId) {
        if (this.userMovieDM.hasDataObjectKey(movieId)) {
            this.userMovieDM.removeDataObjectKey(movieId);

            try {
                final File tempFile = new File("temp.txt");
                String line = null;
                final PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
                final BufferedReader reader = new BufferedReader(new FileReader(UserMovieController.this.movieCollectionFile));
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("{\"id\":" + String.valueOf(movieId)))
                        continue;
                    printWriter.println(line);
                    printWriter.flush();
                }
                printWriter.close();
                reader.close();
                if (UserMovieController.this.movieCollectionFile.delete()) {
                    System.out.println("deleted!");
                    if (tempFile.renameTo(UserMovieController.this.movieCollectionFile))
                        System.out.println("update succeed");
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            this.actionExecutor.execute(new Runnable() {

                @Override
                public void run() {

                }
            });

        }
    }

    @Override
    public void addToUserMovie(final MovieDO movieDO) {
        if (this.userMovieDM.hasDataObjectKey(movieDO.getIdentifier()))
            return;
        this.actionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final UserMovieDO userMovieDO = new UserMovieDO(movieDO.getIdentifier(), movieDO);
                    UserMovieController.this.writer.append(UserMovieController.this.gson.toJson(userMovieDO));
                    UserMovieController.this.writer.newLine();
                    UserMovieController.this.writer.flush();
                    UserMovieController.this.updatesExecutor.execute(new Runnable() {

                        @Override
                        public void run() {
                            UserMovieController.this.userMovieDM.addDataObject(userMovieDO);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addToUserMovie(final CollectionDO collectionDO) {
        for (final CollectionPartDO collectionPartDO : collectionDO.getCollectionPartDM())
            this.loadMovieExecutor.executeQueue(collectionPartDO.getIdentifier().toString(), new Runnable() {

                @Override
                public void run() {
                    UserMovieController.this.session.getControllerRepository().getTmdbController()
                            .requestMovie(collectionPartDO.getIdentifier().toString(), Locale.FRENCH, new Executable<MovieDO>() {

                                @Override
                                public void execute(final MovieDO arg0) {
                                    addToUserMovie(arg0);
                                }
                            });
                }
            });
    }

    @Override
    public void loadMovieList(final List<String> fileNames, final Executable<DoubleTuple<LoadMovie, TMDBRequestResult>> callback) {
        for (final String fileName : fileNames) {
            final LoadMovie loadMovie = MovieLoader.getLoadMovie(fileName);
            this.loadMovieExecutor.executeQueue(fileName, new Runnable() {

                @Override
                public void run() {
                    if (loadMovie.isCollection() == false)
                        UserMovieController.this.session.getControllerRepository().getTmdbController().searchMovie(loadMovie.getName(), Locale.FRENCH, new Executable<MovieDM>() {

                            @Override
                            public void execute(final MovieDM movieDM) {
                                final SimpleDateFormat format = new SimpleDateFormat("yyyy");
                                final MovieDM exactMovies = new MovieDM();
                                if (loadMovie.getYear() != null && loadMovie.isCollection() == false)
                                    for (final MovieDO movieDO : movieDM) {
                                        final String year = format.format(movieDO.getReleaseDate());
                                        if (year.equals(loadMovie.getYear()))
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
                                        callback.execute(new DoubleTuple<LoadMovie, TMDBRequestResult>(loadMovie, requestResult));
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
                                                callback.execute(new DoubleTuple<LoadMovie, TMDBRequestResult>(loadMovie, requestResult));
                                            }
                                        });
                                    }
                                }, false);
                    // UserMovieController.this.session.getControllerRepository().getTmdbController().search(loadMovie.getName(), Locale.FRENCH, new Executable<TMDBRequestResult>()
                    // {
                    //
                    // @Override
                    // public void execute(final TMDBRequestResult result) {
                    // final SimpleDateFormat format = new SimpleDateFormat("yyyy");
                    // final MovieDM exactMovies = new MovieDM();
                    // if (loadMovie.getYear() != null && loadMovie.isCollection() == false)
                    // for (final MovieDO movieDO : result.getMovieDM()) {
                    // final String year = format.format(movieDO.getReleaseDate());
                    // if (year.equals(loadMovie.getYear()))
                    // exactMovies.addDataObject(movieDO);
                    // }
                    //
                    // final TMDBRequestResult requestResult = new TMDBRequestResult();
                    // requestResult.setCollectionDM(result.getCollectionDM());
                    // if (exactMovies.getDataObjectCount() > 0)
                    // requestResult.setMovieDM(exactMovies);
                    // else
                    // requestResult.setMovieDM(result.getMovieDM());
                    // callback.execute(new DoubleTuple<LoadMovie, TMDBRequestResult>(loadMovie, requestResult));
                    // }
                    // });
                }
            });
        }
    }

    private class MovieCustomSerializer implements JsonSerializer<UserMovieDO> {

        @Override
        public JsonElement serialize(final UserMovieDO movie, final Type arg1, final JsonSerializationContext arg2) {
            final JsonObject jsonMovie = new JsonObject();

            jsonMovie.add("id", new JsonPrimitive(movie.getIdentifier()));
            jsonMovie.add("adult", new JsonPrimitive(movie.getMovie().isAdult()));
            jsonMovie.add("backdrop_path", new JsonPrimitive(movie.getMovie().getBackdropPath()));
            jsonMovie.add("original_title", new JsonPrimitive(movie.getMovie().getOriginalTitle()));
            jsonMovie.add("release_date", new JsonPrimitive(movie.getMovie().getReleaseDate().getTime()));
            jsonMovie.add("poster_path", new JsonPrimitive(movie.getMovie().getPosterPath()));
            jsonMovie.add("popularity", new JsonPrimitive(movie.getMovie().getPopularity()));
            jsonMovie.add("title", UserMovieController.this.gson.toJsonTree(movie.getMovie().getTitleMultiLang()));
            jsonMovie.add("vote_average", new JsonPrimitive(movie.getMovie().getVoteAverage()));
            jsonMovie.add("vote_count", new JsonPrimitive(movie.getMovie().getVoteCount()));

            jsonMovie.add("genres", UserMovieController.this.gson.toJsonTree(movie.getMovie().getGenres()));
            jsonMovie.add("overview", UserMovieController.this.gson.toJsonTree(movie.getMovie().getOverviewMultiLang()));

            jsonMovie.add("seen", new JsonPrimitive(movie.isSeen()));
            jsonMovie.add("adding_date", new JsonPrimitive(movie.getAddingDate().getTime()));
            jsonMovie.add("resolution", new JsonPrimitive(movie.getMovieResolution().name()));
            jsonMovie.add("format", new JsonPrimitive(movie.getMovieFormat().name()));
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

            final UserMovieDO userMovieDO = new UserMovieDO(movieDO.getIdentifier(), movieDO, new Date(addingDate), seen, movieFormat, movieResolution);

            return userMovieDO;
        }
    }
}
