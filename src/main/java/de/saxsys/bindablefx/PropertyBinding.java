package de.saxsys.bindablefx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * @author x1rhents on 30.03.2016.
 */
public abstract class PropertyBinding<TValue, TPropertyValue, TProperty extends ObjectProperty<TPropertyValue>> extends BaseBinding<TValue> {

    // region Fields

    /**
     * This {@link Function} is called when the underlying {@link #property} has changed and we need a new property which we can then use.
     */
    private final Function<TValue, TProperty> propertyProvider;
    /**
     * This is the property which will available once the actual {@link #property} has changed and the {@link #propertyProvider} was called to get the new
     * property which shall be listened too. This is actually just a helper attribute since we might get ca property changed event before anything else.
     */
    private TProperty providedProperty;

    // endregion

    // region Constructor

    protected PropertyBinding(final Function<TValue, TProperty> propertyProvider) {
        super();

        if (propertyProvider == null) {
            throw new IllegalArgumentException("Given propertyProvider must not be null");
        }

        this.propertyProvider = propertyProvider;
    }

    // endregion

    // region Abstract

    protected abstract void unbindProperty(final TProperty providedProperty);

    protected abstract void bindProperty(final TProperty providedProperty);

    // endregion

    // region Override BaseBinding

    /**
     * When the property was set to something valid, we will use the provided {@link #propertyProvider} to get another property which we will listen to
     *
     * @param observable the observable value to use
     * @param oldValue   the old value.
     * @param newValue   the new value.
     */
    @Override
    protected final void onPropertyChanged(final ObservableValue<? extends TValue> observable, final TValue oldValue, final TValue newValue) {
        if (oldValue != null) {
            unbindProperty(providedProperty);
            providedProperty = null;
        }

        if (newValue != null) {
            providedProperty = propertyProvider.apply(newValue);
            bindProperty(providedProperty);
        }
    }

    // endregion
}