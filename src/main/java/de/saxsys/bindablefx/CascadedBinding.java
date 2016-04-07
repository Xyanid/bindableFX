package de.saxsys.bindablefx;

import javafx.beans.property.ObjectProperty;

import java.util.function.Function;

/**
 * @author x1rhents on 30.03.2016.
 */
public class CascadedBinding<TValue, TPropertyValue, TProperty extends ObjectProperty<TPropertyValue>>
        extends PropertyBinding<TValue, TPropertyValue, TProperty> {

    // region Fields

    /**
     * This is the binding to invoke when a property change is needed. This means if the {@link #property} was changed to a valid value we invoke the
     * {@link #propertyProvider} to get the new {@link ObjectProperty} that is needed. If a call to {@link #attach(Function)} was already made, then this
     * property is passed to this binding so that the cascaded change can be build. Alternatively if a call to {@link #bind(ObjectProperty)} was made then
     * this binding will be a {@link UnidirectionalBinding}.
     */
    private BaseBinding binding;

    // endregion

    // region Constructor

    private CascadedBinding(final Function<TValue, TProperty> propertyProvider) {
        super(propertyProvider);
    }

    public CascadedBinding(final ObjectProperty<TValue> property, final Function<TValue, TProperty> propertyProvider) {
        this(propertyProvider);

        setProperty(property);
    }

    // endregion

    // region Public

    public CascadedBinding attach(final Function<TPropertyValue, ObjectProperty> otherPropertyProvider) {
        binding = new CascadedBinding<>(otherPropertyProvider);

        // the property was already set because we had a call before a call to this method was made
        if (providedProperty != null) {
            binding.setProperty(providedProperty);
            providedProperty = null;
        }

        return (CascadedBinding) binding;
    }

    public UnidirectionalBinding bind(ObjectProperty property) {

        // TODO need to create a UnidirectionalBinding here and set the property
        return null;
    }

    public BidirectionalBinding bindBidirectional(ObjectProperty property) {

        // TODO need to create a BidirectionalBinding here and set the property
        return null;
    }

    // endregion

    // region Override PropertyBinding

    @Override
    protected void unbindProperty(TProperty providedProperty) {
        if (binding == null) {
            return;
        }
        binding.dispose();
    }

    @Override
    protected void bindProperty(TProperty providedProperty) {
        if (providedProperty != null) {
            if (binding instanceof FinalBinding) {
                binding.setProperty(providedProperty);
            }
        }
    }

    // endregion
}