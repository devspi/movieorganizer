package spi.movieorganizer.repository;

public class MovieOrganizerConstant {

    static {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0)
            MovieOrganizerConstant.MOVIEORGANIZER_ROOT_PATH = "C:/";
        else if (os.indexOf("mac") >= 0)
            MovieOrganizerConstant.MOVIEORGANIZER_ROOT_PATH = "/Users/spi/desktop/";
        MovieOrganizerConstant.MOVIE_COLLECTION_PATH = MovieOrganizerConstant.MOVIEORGANIZER_ROOT_PATH + "moviecollection.txt";
    }

    public static String MOVIEORGANIZER_ROOT_PATH;
    public static String MOVIE_COLLECTION_PATH;

}
