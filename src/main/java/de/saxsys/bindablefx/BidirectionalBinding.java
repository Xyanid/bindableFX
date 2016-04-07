package de.saxsys.bindablefx;

import javafx.beans.property.ObjectProperty;

import java.util.function.Function;

/**
 * @author x1rhents on 30.03.2016.
 */
public class BidirectionalBinding<TValue, TPropertyValue, TProperty extends ObjectProperty<TPropertyValue>>
        extends FinalBinding<TValue, TPropertyValue, TProperty> {

    // region Constructor

    public BidirectionalBinding(final Function<TValue, TProperty> propertyProvider, final TProperty finalProperty) {
        super(propertyProvider, finalProperty);
    }

    // endregion

    // region Override PropertyBinding

    @Override
    protected void unbindProperty(TProperty providedProperty) {
        providedProperty.unbindBidirectional(providedProperty);
    }

    @Override
    protected void bindProperty(TProperty providedProperty) {
        if (providedProperty != null) {
            providedProperty.bindBidirectional(providedProperty);
        }
    }

    // endregion
}