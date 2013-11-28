/*
 * 27 août 2008
 *
 * Copyright 2008 Exane All rights reserved.
 */
package spi.movieorganizer.display.table.column;

import java.util.MissingResourceException;

import javax.swing.Action;
import javax.swing.Icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spi.movieorganizer.display.resources.MovieOrganizerStaticResources;
import exane.osgi.jexlib.common.annotation.injector.ResourceInjector;
import exane.osgi.jexlib.common.swing.table.column.AbstractExaneTableColumn;
import exane.osgi.jexlib.common.swing.tools.resources.ImageResourceManager;
import exane.osgi.jexlib.data.object.ExaneDataObject;
import exane.osgi.jexlib.data.object.ExaneDataType;

/**
 * @author Tribondeau Brian
 * @version 27 août 2008
 * @param <K>
 *            Column type
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMovieOrganizerTableColumn<T, V extends ExaneDataObject> extends AbstractExaneTableColumn<T, V> {
    private static final long   serialVersionUID       = -8913608996797130927L;
    private static final Logger LOGGER                 = LoggerFactory.getLogger(AbstractMovieOrganizerTableColumn.class);

    public static final String  ID_COLUMN              = "TableColumn.";

    public static final String  COLUMN_NAME            = "." + Action.NAME;
    public static final String  COLUMN_DESCRIPTION     = "." + "Description";
    public static final String  COLUMN_ICON            = "." + Action.SMALL_ICON;
    public static final String  COLUMN_GROUP           = "." + "Group";
    public static final String  GROUP_SEPARATOR_REGEXP = "\\|";

    public static final boolean IS_ALWAYS_UPDATE       = true;

    public AbstractMovieOrganizerTableColumn(final ExaneDataType... changeItems) {
        super(changeItems);
    }

    public String getResourcePath() {
        return MovieOrganizerStaticResources.PROPERTIES_TABLES;
    }

    public abstract ColumnCategory getCategory();

    @Override
    public String computeId() {
        return AbstractMovieOrganizerTableColumn.ID_COLUMN + getCategory().toString() + "." + this.getClass().getSimpleName();
    }

    @Override
    public String[] computeGroup() {
        final String searchString = getId() + AbstractMovieOrganizerTableColumn.COLUMN_GROUP;

        try {
            ResourceInjector.useSilentForNextGet();
            final String groupStr = ResourceInjector.getString(getClass(), getResourcePath(), searchString, (String) null);
            if (groupStr != null)
                return groupStr.split(AbstractMovieOrganizerTableColumn.GROUP_SEPARATOR_REGEXP);
        } catch (final MissingResourceException mre) {
            AbstractMovieOrganizerTableColumn.LOGGER.error("Unable to load column group from common bundle", mre);
        }

        return super.getGroup();
    }

    @Override
    public String computeDescription() {
        final String searchString = getId() + AbstractMovieOrganizerTableColumn.COLUMN_DESCRIPTION;

        try {
            final String descStr = ResourceInjector.getString(getClass(), getResourcePath(), searchString, (String) null);
            if (descStr != null)
                return descStr;
        } catch (final MissingResourceException mre) {
            AbstractMovieOrganizerTableColumn.LOGGER.error("Unable to load column description from common bundle", mre);
        }
        return "No description";
    }

    public Icon getIcon() {
        return ImageResourceManager.getEmptyIcon(16, 16);
    }

    @Override
    public boolean isRealTime() {
        return true;
    }

    @Override
    public String computeName() {
        final String searchString = getId() + AbstractMovieOrganizerTableColumn.COLUMN_NAME;
        String nameStr = null;

        try {
            nameStr = ResourceInjector.getString(getClass(), getResourcePath(), searchString, (String) null);
            if (nameStr != null)
                return nameStr;
        } catch (final MissingResourceException mre) {
            AbstractMovieOrganizerTableColumn.LOGGER.error("Unable to load column name from common bundle", mre);
        }

        return "#Undef";
    }
}
