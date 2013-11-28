package spi.movieorganizer.data;

import java.util.Set;

import exane.osgi.jexlib.data.object.ExaneDataType;

public enum MovieOrganizerType implements ExaneDataType {

    MOVIE,
    GENRE,
    COMPANY,
    PEOPLE,
    USER_MOVIE,
    COLLECTION_PART,
    COLLECTION;

    @Override
    public Set<ExaneDataType> getAncestors() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ExaneDataType> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ExaneDataType> getDescendants() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ExaneDataType> getParents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAncestorOf(final ExaneDataType arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildOf(final ExaneDataType arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDescendantOf(final ExaneDataType arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isParentOf(final ExaneDataType arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
