package de.saxsys.bindablefx;

import javafx.beans.property.ObjectProperty;

import java.util.function.Function;

/**
 * @author x1rhents on 30.03.2016.
 */
public abstract class FinalBinding<TValue, TPropertyValue, TProperty extends ObjectProperty<TPropertyValue>>
        extends PropertyBinding<TValue, TPropertyValue, TProperty> {

    // region Fields

    /**
     * This is the final property that will be bound to the {@link #property} once it gets set to a valid value.
     */
    private final TProperty finalProperty;

    // endregion

    // region Constructor

    public FinalBinding(final Function<TValue, TProperty> propertyProvider, final TProperty finalProperty) {
        super(propertyProvider);

        if (finalProperty == null) {
            throw new IllegalArgumentException("Given finalProperty must not be null");
        }

        this.finalProperty = finalProperty;
    }

    // endregion
}