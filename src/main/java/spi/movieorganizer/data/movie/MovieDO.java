package spi.movieorganizer.data.movie;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import spi.movieorganizer.data.MovieOrganizerType;
import spi.movieorganizer.data.company.CompanyDO;
import spi.movieorganizer.data.util.StringMultiLang;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class MovieDO extends AbstractDataObject<Integer> {

    // summary
    private final Boolean         adult;
    private final String          backdropPath;
    private final String          originalTitle;
    private final Date            releaseDate;
    private final String          posterPath;
    private final Double          popularity;
    private final StringMultiLang title;
    private final Double          voteAverage;
    private final Integer         voteCount;

    // details
    private final Double          budget;
    private List<Integer>         genres;
    private final String          homepage;
    private final String          imdbId;
    private StringMultiLang       overview;
    private final List<CompanyDO> productionCompanies;
    private final Double          revenue;
    private final Double          runtime;
    private final String          status;
    private final String          tagline;

    public MovieDO(final Integer id, final Boolean adult, final String backdropPath, final String originalTitle, final Date releaseDate, final String posterPath,
            final Double popularity, final StringMultiLang title, final Double voteAverage, final Integer voteCount) {
        super(id);
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.title = title;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;

        this.budget = null;
        this.genres = null;
        this.homepage = null;
        this.imdbId = null;
        this.overview = null;
        this.productionCompanies = null;
        this.revenue = null;
        this.runtime = null;
        this.status = null;
        this.tagline = null;
    }

    public MovieDO(final Integer id, final Boolean adult, final String backdropPath, final String originalTitle, final Date releaseDate, final String posterPath,
            final Double popularity, final StringMultiLang title, final Double voteAverage, final Integer voteCount, final Double budget, final List<Integer> genres,
            final String homepage, final String imdbId, final StringMultiLang overview, final List<CompanyDO> productionCompanies, final Double revenue, final Double runtime,
            final String status, final String tagline) {
        super(id);
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.title = title;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.budget = budget;
        this.genres = genres;
        this.homepage = homepage;
        this.imdbId = imdbId;
        this.overview = overview;
        this.productionCompanies = productionCompanies;
        this.revenue = revenue;
        this.runtime = runtime;
        this.status = status;
        this.tagline = tagline;
    }

    public boolean isAdult() {
        return this.adult;
    }

    public String getBackdropPath() {
        return this.backdropPath;
    }

    public double getBudget() {
        return this.budget;
    }

    public List<Integer> getGenres() {
        return this.genres;
    }

    public void setGenres(final List<Integer> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public String getImdbId() {
        return this.imdbId;
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public String getOverview(final Locale locale) {
        return this.overview.getValue(locale);
    }

    public StringMultiLang getOverviewMultiLang() {
        return this.overview;
    }

    public void setOverviewMutliLang(final StringMultiLang overview) {
        this.overview = overview;
    }

    public void setOverview(final Locale locale, final String value) {
        this.overview.addValue(locale, value);
    }

    public Double getPopularity() {
        return this.popularity;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public List<CompanyDO> getProductionCompanies() {
        return this.productionCompanies;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public Double getRevenue() {
        return this.revenue;
    }

    public double getRuntime() {
        return this.runtime;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTagline() {
        return this.tagline;
    }

    public String getTitle(final Locale locale) {
        return this.title.getValue(locale);
    }

    public StringMultiLang getTitleMultiLang() {
        return this.title;
    }

    public Double getVoteAverage() {
        return this.voteAverage;
    }

    public Integer getVoteCount() {
        return this.voteCount;
    }

    public Boolean getAdult() {
        return this.adult;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("id: " + getIdentifier() + "\n");
        sb.append("title: " + this.title + "\n");
        sb.append("release date:" + this.releaseDate + "\n");
        sb.append("original title: " + this.originalTitle + "\n");
        if (this.overview != null)
            sb.append("overview: " + this.overview + "\n");
        sb.append("popularity: " + this.popularity + "\n");
        sb.append("vote average: " + this.voteAverage + " vote count=" + this.voteCount + "\n");

        if (this.genres != null)
            for (final Integer item : this.genres)
                sb.append("Genre: " + item + "\n");
        return sb.toString();
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.MOVIE;
    }

}
