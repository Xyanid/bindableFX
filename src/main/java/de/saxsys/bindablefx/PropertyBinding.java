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

import java.lang.ref.WeakReference;
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

    private WeakReference<ObservableValue<? extends TValue>> boundValue;

    private TValue memorizedValue;

    // endregion

    // region Constructor

    PropertyBinding(@NotNull final ObservableValue<TParentValue> parent, @NotNull final Function<TParentValue, TProperty> nestedResolver) {
        super(parent, nestedResolver);
    }

    // endregion

    // region Public

    //    /**
    //     * Consumes the computed {@link Property} of this binding each time it is propertyChanged, note that this does not mean that the value of the {@link Property} is propertyChanged but the
    //     * computed {@link Property} itself. So this will only happen if this bindings {@link #observedValue} is propertyChanged, which will then provide a new computed
    //     * {@link Property}. The
    //     * previous {@link Property} will be saved, so that when a new {@link Property} is computed the old {@link Property} can be consumed prior to the new one. Note that a
    //     * call
    //     * to this method will dispose the previous {@link #child}.
    //     *
    //     * @param previousValueConsumer the {@link Consumer} to be called when the previous {@link Property} is consumed.
    //     * @param currentValueConsumer  the {@link Consumer} to be called when the new {@link Property} is consumed.
    //     *
    //     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.ConsumerStrategy}.
    //     *
    //     * @see de.saxsys.bindablefx.strategy.ConsumerStrategy
    //     */
    //    public PropertyBinding<TParentValue, TValue, TProperty> thenConsumeValue(final @NotNull Consumer<TValue> previousValueConsumer, final @NotNull Consumer<TValue>
    // currentValueConsumer) {
    //
    //        return this;
    //    }
    //
    //    /**
    //     * Calls the given {@link Function} in order to either use the value of the computed {@link Property} or provide a different one. Note that a call to this method will
    //     * dispose the previous {@link #child}.
    //     *
    //     * @param resolver the {@link Function} to be called when this bindings {@link Property} has been computed and shall be used by the child.
    //     *
    //     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.FallbackStrategy}.
    //     *
    //     * @see de.saxsys.bindablefx.strategy.FallbackStrategy
    //     */
    //    public PropertyBinding<TParentValue, TValue, TProperty> thenFallbackOn(@NotNull final Function<ObservableValue<TValue>, TValue> resolver) {
    //        return this;
    //    }
    //
    //    /**
    //     * Binds the computed {@link Property} of this binding against the provides {@link ObservableValue}. Note that a call to this method will dispose the previous
    //     * {@link #child}.
    //     *
    //     * @param target the {@link ObservableValue} against which the computed {@link Property} of this binding will be bound.
    //     *
    //     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.UnidirectionalStrategy}.
    //     *
    //     * @see de.saxsys.bindablefx.strategy.UnidirectionalStrategy
    //     */
    //    public PropertyBinding<TParentValue, TValue, TProperty> thenBind(@NotNull final ObservableValue<TValue> target) {
    //        return this;
    //    }
    //
    //    /**
    //     * Binds the computed {@link Property} of this binding bidirectional against the provides {@link Property}. Note that a call to this method will dispose the previous
    //     * {@link #child}.
    //     *
    //     * @param target the {@link Property} against which the computed {@link Property} of this binding will be bound.
    //     *
    //     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.BidirectionalStrategy}.
    //     *
    //     * @see de.saxsys.bindablefx.strategy.BidirectionalStrategy
    //     */
    //    public PropertyBinding<TParentValue, TValue, TProperty> thenBindBidirectional(@NotNull final Property<TValue> target) {
    //        return this;
    //    }
    //
    //    /**
    //     * Binds the computed {@link Property} of this binding bidirectional against the provides {@link Property}. If the value of current computed {@link Property} is null, then
    //     * the fallbackValue is used instead. Note that a call to this method will dispose the previous {@link #child}.
    //     *
    //     * @param target the {@link Property} against which the computed {@link Property} of this binding will be bound.
    //     *
    //     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.FallbackBidirectionalStrategy}.
    //     *
    //     * @see de.saxsys.bindablefx.strategy.FallbackBidirectionalStrategy
    //     */
    //    public PropertyBinding<TParentValue, TValue, TProperty> thenBindBidirectionalOrFallbackOn(@NotNull final Property<TValue> target, final TValue @Nullable fallbackValue) {
    //        return this;
    //    }
    //
    //    /**
    //     * Uses the provided {@link IStrategy}, which will be invoked each time the computed {@link ObservableValue} of this binding is propertyChanged. Note that a call to this method will
    //     * dispose the previous {@link #child}.
    //     *
    //     * @param strategy the {@link IStrategy} to be used when the computed {@link Property} changes.
    //     *
    //     * @return this bindings {@link #child}, which also a new {@link StrategyListener}.
    //     *
    //     * @see IStrategy
    //     */
    //    public PropertyBinding<TParentValue, TValue, TProperty> thenUseStrategy(final @NotNull IStrategy<ObservableValue<TValue>> strategy) {
    //        return this;
    //    }

    // endregion

    // region Override RootBinding

    @Override
    protected void beforeDestroyObservedValue(@NotNull final ObservableValue<TValue> observableValue) {
        if (isBound()) {
            unbind();
        }
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
        unbind();
        memorizedValue = null;
        this.boundValue = new WeakReference<>(observable);
        getObservedValue().ifPresent(observedValue -> ((Property) observedValue).bind(observable));
    }

    @Override
    public void unbind() {
        getObservedValue().ifPresent(property -> ((Property) property).unbind());
        boundValue = null;
    }

    @Override
    public boolean isBound() {
        if (boundValue != null && boundValue.get() != null) {
            return true;
        }

        final Optional<ObservableValue<TValue>> currentProperty = getObservedValue();
        return currentProperty.isPresent() && ((Property) currentProperty.get()).isBound();
    }

    @Override
    public void bindBidirectional(@NotNull final Property<TValue> other) {
        javafx.beans.binding.Bindings.bindBidirectional(this, other);
    }

    @Override
    public <TOtherValue> void bindBidirectional(@NotNull final Property<TOtherValue> other, @NotNull final IConverter<TValue, TOtherValue> converter) {
        Bindings.bindBidirectional(this, other, converter);
    }

    @Override
    public void unbindBidirectional(@NotNull final Property<TValue> other) {
        javafx.beans.binding.Bindings.unbindBidirectional(this, other);
        Bindings.unbindBidirectional(this, other);
    }

    // endregion
}