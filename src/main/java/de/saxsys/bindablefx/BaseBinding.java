package de.saxsys.bindablefx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author x1rhents on 30.03.2016.
 */
//TODO maybe caching of events that happend during creation of the instance in a sub class need to be cached so they are not lost and
//TODO invoked when the sub class activates the listener ?
public abstract class BaseBinding<TValue> {

    //region Fields

    /**
     * Determines the {@link ObjectProperty} which is watched by this binding.
     */
    private ObjectProperty<TValue> property;
    /**
     * The listener which will be attached to the {@link #property}.
     */
    private final ChangeListener<TValue> listener;

    //endregion

    //region Constructor

    /**
     * Creates a new instance and adds the {@link #listener} to the {@link #property}.
     */
    protected BaseBinding() {
        listener = this::onPropertyChanged;
    }

    //endregion

    // region Package Private

    /**
     * Sets the {@link #property} and adds the {@link #listener} to it.
     *
     * @param property the {@link ObjectProperty} which will be used as the {@link #property}
     */
    void setProperty(final ObjectProperty<TValue> property) {
        if (property == null) {
            throw new IllegalArgumentException("Given property can not be null");
        }

        dispose();

        this.property = property;
        this.property.addListener(listener);
    }

    // endregion

    // region Public

    /**
     * Removes the {@link #listener} from the {@link #property}. Once a call has been made, this {@link BaseBinding} will not longer
     * work.
     */
    public void dispose() {
        if (property != null) {
            property.removeListener(listener);
        }
    }

    // endregion

    // region Abstract

    /**
     * Will be called each time the {@link #property} has changed.
     *
     * @param observable the {@link #property} that was changed.
     * @param oldValue   the old value.
     * @param newValue   the new value.
     */
    protected abstract void onPropertyChanged(final ObservableValue<? extends TValue> observable, final TValue oldValue, final TValue newValue);

    // endregion
}