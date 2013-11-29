package spi.movieorganizer.display.view.loadresult;

import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.movie.UserMovieSettings;

public class SelectedItemData {

    private final UserMovieSettings settings;
    private Integer                 itemId;
    private TMDBRequestType         requestType;

    public SelectedItemData() {
        this.settings = UserMovieSettings.createUnknownSettings();
    }

    public void setItemId(final Integer itemId) {
        this.itemId = itemId;
    }

    public void setRequestType(final TMDBRequestType requestType) {
        this.requestType = requestType;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public UserMovieSettings getSettings() {
        return this.settings;
    }

    public TMDBRequestType getRequestType() {
        return this.requestType;
    }

}
