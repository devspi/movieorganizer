package spi.movieorganizer.data.movie;

public class LoadMovie {

    private final String  fileName;
    private final String  name;
    private final String  quality;
    private final String  year;
    private final boolean collection;

    public LoadMovie(final String fileName, final String name, final String quality, final String year, final boolean collection) {
        super();
        this.fileName = fileName;
        this.name = name;
        this.quality = quality;
        this.year = year;
        this.collection = collection;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isCollection() {
        return this.collection;
    }

    public String getName() {
        return this.name;
    }

    public String getQuality() {
        return this.quality;
    }

    public String getYear() {
        return this.year;
    }

}
