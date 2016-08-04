/*
 * Copyright 2015 - 2016 Xyanid
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package de.saxsys.bindablefx;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This is a binding that acts as an intermediate binding so that multiple cascaded properties can be observed and the desired property can be bound at the
 * very end. So if you want to bind a property which itself is contained cascadingly in other properties.
 * <p>
 * e.G. if we have a class A which hold a property of B, which hold property of C, which holds a property of D and D is the property we want to bind to, we
 * would like build something like this
 * <pre>
 * {@code
 * A a = new A();
 * ObjectProperty<Long> property = new SimpleObjectProperty<>();
 * a.getB().getC().dProperty().bindBidirectional(property);
 * }
 * </pre>
 * However since B and C might be null we would need to listen to the values to become available at some point in time. This is where the
 * {@link PropertyBinding} comes into play and handles this by attaching listeners cascadingly so that we can bind to D with no concern that B or C might be
 * null, change or become invalid.
 * <p>
 * e.g. using the above example, the code to safely bind to D would look like this.
 * <pre>
 * {@code
 * A a = new A();
 * ObjectProperty<Long> property = new SimpleObjectProperty<>();
 *
 * new NestedBinding(a.bProperty(), B::cProperty).bindBidirectional(C::dProperty, property, false);
 * }
 * </pre>
 *
 * @author xyanid on 30.03.2016.
 */
class PropertyBinding<TParentValue, TValue, TProperty extends Property<TValue>> extends RelayBinding<TParentValue, TValue, TProperty> implements IPropertyBinding<TValue> {

    // region Fields

    /**
     * The currently bound value of this binding.
     */
    @Nullable
    private WeakReference<ObservableValue<? extends TValue>> boundValue;

    /**
     * The currently bound value of this binding.
     */
    @NotNull
    private final List<WeakReference<Property>> bidirectionalBoundProperties = new ArrayList<>();

    /**
     * The last value that has been set for this property binding, will be used once the property gets available.
     */
    @Nullable
    private TValue memorizedValue;

    // endregion

    // region Constructor

    PropertyBinding(@NotNull final ObservableValue<TParentValue> parent, @NotNull final Function<TParentValue, TProperty> nestedResolver) {
        super(parent, nestedResolver);
    }

    // endregion

    // region Override RootBinding

    @Override
    protected void beforeDestroyObservedValue(@NotNull final ObservableValue<TValue> observableValue) {
        getObservedValue().ifPresent(observedValue -> {
            if (isBound()) {
                ((Property) observedValue).unbind();
            }
        });
    }

    @SuppressWarnings ("unchecked")
    @Override
    protected void afterSetObservedValue(@NotNull final ObservableValue<TValue> observableValue) {
        if (boundValue != null) {
            final ObservableValue<? extends TValue> boundTo = boundValue.get();
            ((Property) observableValue).bind(boundTo);
        } else if (memorizedValue != null) {
            setValue(memorizedValue);
        }
    }

    /**
     * {@inheritDoc} Also bidirectionally unbinds all bound properties available in {@link #bidirectionalBoundProperties}.
     */
    @Override
    public void dispose() {
        super.dispose();
        unbind();
        unbindBidirectional();
    }

    // endregion

    // region Implement Property

    /**
     * Sets the value of the {@link #observedValue} if it is available. If the {@link #observedValue} is not yet available, then a call to this method will remember the value that needs to be set.
     * Once the {@link #observedValue} then get available, the value will be set.
     *
     * @param value the value to use.
     *
     * @throws IllegalStateException if the {@link #observedValue} is available, but is already bound.
     */
    @SuppressWarnings ("unchecked")
    @Override
    public void setValue(final TValue value) {
        if (isBound()) {
            throw new IllegalStateException((getBean() != null && getName() != null ? getBean().getClass().getSimpleName() + "." + getName() + " : " : "") + "A bound value cannot be set.");
        }

        final Optional<ObservableValue<TValue>> observedValue = getObservedValue();
        if (observedValue.isPresent()) {
            ((Property) observedValue.get()).setValue(value);
            memorizedValue = null;
        } else {
            memorizedValue = value;
        }
    }

    @Override
    public Object getBean() {
        final Optional<ObservableValue<TValue>> currentProperty = getObservedValue();
        return currentProperty.isPresent() ? ((Property) currentProperty.get()).getBean() : null;
    }

    @Override
    public String getName() {
        final Optional<ObservableValue<TValue>> currentProperty = getObservedValue();
        return currentProperty.isPresent() ? ((Property) currentProperty.get()).getName() : null;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public void bind(@NotNull final ObservableValue<? extends TValue> observable) {
        memorizedValue = null;
        this.boundValue = new WeakReference<>(observable);
        getObservedValue().ifPresent(observedValue -> {
            if (((Property) observedValue).isBound()) {
                ((Property) observedValue).unbind();
            }
            ((Property) observedValue).bind(observable);
        });
    }

    @Override
    public void unbind() {
        if (isBound()) {
            getObservedValue().ifPresent(property -> ((Property) property).unbind());
            boundValue = null;
        }
    }

    @Override
    public boolean isBound() {
        return boundValue != null && boundValue.get() != null;
    }

    @Override
    public void bindBidirectional(@NotNull final Property<TValue> other) {
        memorizedValue = null;
        javafx.beans.binding.Bindings.bindBidirectional(this, other);
        bidirectionalBoundProperties.add(new WeakReference<>(other));
    }

    @Override
    public void unbindBidirectional(@NotNull final Property<TValue> other) {
        unbindProperty(other);
    }

    @Override
    public <TOtherValue> void bindBidirectional(@NotNull final Property<TOtherValue> other, @NotNull final IConverter<TValue, TOtherValue> converter) {
        memorizedValue = null;
        Bindings.bindBidirectional(this, other, converter);
        bidirectionalBoundProperties.add(new WeakReference<>(other));
    }

    @Override
    public <IOtherValue> void unbindBidirectionalConverted(@NotNull final Property<IOtherValue> other) {
        unbindProperty(other);
    }

    @SuppressWarnings ("unchecked")
    @Override
    public void unbindBidirectional() {
        while (!bidirectionalBoundProperties.isEmpty()) {
            final Property prop = bidirectionalBoundProperties.get(0).get();
            if (prop != null) {
                javafx.beans.binding.Bindings.unbindBidirectional(this, prop);
                Bindings.unbindBidirectional(this, prop);
            }
            bidirectionalBoundProperties.remove(0);
        }
    }

    @Override
    public boolean isBidirectionalBound() {
        return !bidirectionalBoundProperties.isEmpty();
    }

    // endregion

    // region Private

    @SuppressWarnings ("unchecked")
    private void unbindProperty(@NotNull final Property other) {
        for (int i = 0; i < bidirectionalBoundProperties.size(); ++i) {
            final Property prop = bidirectionalBoundProperties.get(i).get();
            if (prop == null || prop == other) {
                javafx.beans.binding.Bindings.unbindBidirectional(this, other);
                Bindings.unbindBidirectional(this, other);
                bidirectionalBoundProperties.remove(i--);
            }
        }
    }

    // endregion
}