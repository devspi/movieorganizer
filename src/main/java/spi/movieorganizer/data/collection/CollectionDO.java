package spi.movieorganizer.data.collection;

import java.util.Locale;

import spi.movieorganizer.data.MovieOrganizerType;
import spi.movieorganizer.data.util.StringMultiLang;
import exane.osgi.jexlib.data.object.AbstractDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

public class CollectionDO extends AbstractDataObject<Integer> {

    private final String          backdropPath;
    private final StringMultiLang name;
    private final String          posterPath;

    private CollectionPartDM      collectionPartDM;

    public CollectionDO(final Integer key, final String backdropPath, final StringMultiLang name, final String posterPath) {
        super(key);
        this.backdropPath = backdropPath;
        this.name = name;
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return this.backdropPath;
    }

    public String getName(final Locale locale) {
        return this.name.getValue(locale);
    }

    public StringMultiLang getNameMultiLang() {
        return this.name;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public CollectionPartDM getCollectionPartDM() {
        return this.collectionPartDM;
    }

    public void setCollectionPartDM(final CollectionPartDM collectionPartDM) {
        this.collectionPartDM = collectionPartDM;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("id: " + getIdentifier() + "\n");
        sb.append("title: " + this.name);
        return sb.toString();
    }

    @Override
    public ExaneDataType getDataType() {
        return MovieOrganizerType.COLLECTION;
    }
}
