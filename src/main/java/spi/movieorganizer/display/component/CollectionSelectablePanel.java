package spi.movieorganizer.display.component;

import java.util.Locale;

import spi.movieorganizer.controller.tmdb.TMDBController;
import spi.movieorganizer.controller.tmdb.TMDBRequestResult.TMDBRequestType;
import spi.movieorganizer.data.collection.CollectionDO;

public class CollectionSelectablePanel extends AbstractSelectableItemPanel {

    private final CollectionDO collectionDO;

    public CollectionSelectablePanel(final CollectionDO collectionDO) {
        super();
        this.collectionDO = collectionDO;
        final String src = collectionDO.getPosterPath() != null ? TMDBController.BASE_URL + "w92" + collectionDO.getPosterPath() : "http://d3a8mw37cqal2z.cloudfront.net/assets/e6497422f20fa74/images/no-poster-w92.jpg";
        this.posterLabel.setText("<html><img src=\"" + src + "\"></html>");
        this.titleLabel.setText("<html>" + collectionDO.getName(Locale.FRENCH) + "</html>");
    }

    @Override
    protected TMDBRequestType getType() {
        return TMDBRequestType.Collections;
    }

    @Override
    protected Integer getItemId() {
        return this.collectionDO.getIdentifier();
    }

}
