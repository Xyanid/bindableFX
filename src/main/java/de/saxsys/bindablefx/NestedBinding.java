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

import de.saxsys.bindablefx.strategy.IStrategy;
import de.saxsys.bindablefx.strategy.StrategyFactory;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
 * {@link NestedBinding} comes into play and handles this by attaching listeners cascadingly so that we can bind to D with no concern that B or C might be
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
public class NestedBinding<TValue, TComputedValue> extends RelayBinding<TValue, ObservableValue<TComputedValue>> {

    // region Fields

    /**
     * The child binding held by this cascaded binding.
     */
    @Nullable
    private BaseBinding child;

    // endregion

    // region Constructor

    NestedBinding() {}

    // endregion

    // region Child Binding

    /**
     */
    private void disposeChild() {
        if (child != null) {
            child.dispose();
        }
    }

    /**
     * Disposes the current {@link #child} and recreates a new {@link TBaseBinding} using the given {@link Supplier}.
     *
     * @param <TBaseBinding> the type of the desired new child.
     *
     * @return a new {@link TBaseBinding} which as been set as the new {@link #child}.
     */
    @NotNull <TBaseBinding extends BaseBinding> TBaseBinding createChild(@NotNull final TBaseBinding binding) {
        disposeChild();

        child = binding;

        computeValue();

        return binding;
    }

    // endregion

    // region Override BaseBinding

    /**
     * Computes the nested {@link Property} which is provided by the current {@link #observedValue}. If the {@link #child} has been set, then the {@link Property} will also be
     * relayed to the child.
     *
     * @return the {@link Property} which is relayed by the {@link #relayResolver} or null if either the {@link #relayResolver} or {@link #observedValue} is null.
     */
    @SuppressWarnings ("unchecked")
    @Override
    public final ObservableValue<TComputedValue> computeValue() {

        final ObservableValue<TComputedValue> computedValue = super.computeValue();

        if (child != null) {
            child.destroyObservedValue();
            if (computedValue != null) {
                child.setObservedValue(computedValue);
            }
        }

        return computedValue;
    }

    /**
     * {@inheritDoc}. This implementation also disposes the child if it is available.
     */
    @Override
    public void dispose() {
        super.dispose();
        disposeChild();
        child = null;
    }

    // endregion

    // region Public

    /**
     * Starts observing the given {@link ObservableValue} and computed the desired {@link ObservableValue} via the provided relay provider.
     *
     * @param observedValue the {@link ObservableValue} to observe.
     * @param relayProvider the {@link Function} to be used when the observed value is set and next {@link ObservableValue} is needed.
     *
     * @return this binding.
     */
    NestedBinding<TValue, TComputedValue> observe(@NotNull final ObservableValue<TValue> observedValue, @NotNull final Function<TValue, ObservableValue<TComputedValue>> relayProvider) {
        setObservedValue(observedValue);
        setRelayResolver(relayProvider);
        return this;
    }

    /**
     * Starts observing the {@link Property} that is provided by the {@link Function} as soon as this bindings {@link #observedValue} has been set. Note that a call to this
     * method will dispose the previous {@link #child}.
     *
     * @param relayProvider          the {@link Function} used to get the next {@link Property} to observe for the {@link #child}.
     * @param <TNestedComputedValue> the type of the nested computed value of the {@link #child}.
     *
     * @return this bindings {@link #child}, which is a new {@link NestedBinding}.
     *
     * @see NestedBinding
     */
    public <TNestedComputedValue> NestedBinding<TComputedValue, TNestedComputedValue> thenObserve(@NotNull final Function<TComputedValue, ObservableValue<TNestedComputedValue>> relayProvider) {
        final NestedBinding<TComputedValue, TNestedComputedValue> result = createChild(new NestedBinding<TComputedValue, TNestedComputedValue>());
        result.setRelayResolver(relayProvider);
        return result;
    }

    /**
     * Consumes the computed {@link Property} of this binding each time it is changed, note that this does not mean that the value of the {@link Property} is changed but the
     * computed {@link Property} itself. So this will only happen if this bindings {@link #observedValue} is changed, which will then provide a new computed
     * {@link Property}. The
     * previous {@link Property} will be saved, so that when a new {@link Property} is computed the old {@link Property} can be consumed prior to the new one. Note that a
     * call
     * to this method will dispose the previous {@link #child}.
     *
     * @param previousValueConsumer the {@link Consumer} to be called when the previous {@link Property} is consumed.
     * @param currentValueConsumer  the {@link Consumer} to be called when the new {@link Property} is consumed.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyBinding} using a {@link de.saxsys.bindablefx.strategy.ConsumerStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.ConsumerStrategy
     */
    public StrategyBinding<ObservableValue<TComputedValue>, Void> thenConsume(final @NotNull Consumer<TComputedValue> previousValueConsumer,
                                                                              final @NotNull Consumer<TComputedValue> currentValueConsumer) {
        final StrategyBinding<ObservableValue<TComputedValue>, Void> result = createChild(new StrategyBinding<ObservableValue<TComputedValue>, Void>());
        final IStrategy<ObservableValue<TComputedValue>, Void> strategy = StrategyFactory.createConsumerStrategy(previousValueConsumer, currentValueConsumer);
        result.setStrategy(strategy);
        return result;
    }

    /**
     * Calls the given {@link Function} in order to either use the value of the computed {@link Property} or provide a different one. Note that a call to this method will
     * dispose the previous {@link #child}.
     *
     * @param resolver the {@link Function} to be called when this bindings {@link Property} has been computed and shall be used by the child.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyBinding} using a {@link de.saxsys.bindablefx.strategy.FallbackStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.FallbackStrategy
     */
    public StrategyBinding<ObservableValue<TComputedValue>, TComputedValue> thenFallbackOn(@NotNull final Function<ObservableValue<TComputedValue>, TComputedValue> resolver) {
        final StrategyBinding<ObservableValue<TComputedValue>, TComputedValue> result = createChild(new StrategyBinding<ObservableValue<TComputedValue>, TComputedValue>());
        final IStrategy<ObservableValue<TComputedValue>, TComputedValue> strategy = StrategyFactory.createFallbackStrategy(resolver);
        result.setStrategy(strategy);
        return result;
    }

    /**
     * Binds the computed {@link Property} of this binding against the provides {@link ObservableValue}. Note that a call to this method will dispose the previous
     * {@link #child}.
     *
     * @param target the {@link ObservableValue} against which the computed {@link Property} of this binding will be bound.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyBinding} using a {@link de.saxsys.bindablefx.strategy.UnidirectionalStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.UnidirectionalStrategy
     */
    public StrategyBinding<Property<TComputedValue>, Void> thenBind(@NotNull final ObservableValue<TComputedValue> target) {
        final StrategyBinding<Property<TComputedValue>, Void> result = createChild(new StrategyBinding<Property<TComputedValue>, Void>());
        final IStrategy<Property<TComputedValue>, Void> strategy = StrategyFactory.createUnidirectionalStrategy(target);
        result.setStrategy(strategy);
        return result;
    }

    /**
     * Binds the computed {@link Property} of this binding bidirectional against the provides {@link Property}. Note that a call to this method will dispose the previous
     * {@link #child}.
     *
     * @param target the {@link Property} against which the computed {@link Property} of this binding will be bound.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyBinding} using a {@link de.saxsys.bindablefx.strategy.BidirectionalStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.BidirectionalStrategy
     */
    public StrategyBinding<Property<TComputedValue>, Void> thenBindBidirectional(@NotNull final Property<TComputedValue> target) {
        final StrategyBinding<Property<TComputedValue>, Void> result = createChild(new StrategyBinding<Property<TComputedValue>, Void>());
        final IStrategy<Property<TComputedValue>, Void> strategy = StrategyFactory.createBidirectionalStrategy(target);
        result.setStrategy(strategy);
        return result;
    }

    /**
     * Binds the computed {@link Property} of this binding bidirectional against the provides {@link Property}. If the value of current computed {@link Property} is null, then
     * the fallbackValue is used instead. Note that a call to this method will dispose the previous {@link #child}.
     *
     * @param target the {@link Property} against which the computed {@link Property} of this binding will be bound.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyBinding} using a {@link de.saxsys.bindablefx.strategy.FallbackBidirectionalStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.FallbackBidirectionalStrategy
     */
    public StrategyBinding<Property<TComputedValue>, Void> thenBindBidirectionalOrFallbackOn(@NotNull final Property<TComputedValue> target, @Nullable final TComputedValue fallbackValue) {


        final StrategyBinding<Property<TComputedValue>, Void> result = createChild(new StrategyBinding<Property<TComputedValue>, Void>());
        final IStrategy<Property<TComputedValue>, Void> strategy = StrategyFactory.createFallbackBidirectionalStrategy(target, fallbackValue);
        result.setStrategy(strategy);
        return result;
    }

    /**
     * Uses the provided {@link IStrategy}, which will be invoked each time the computed {@link ObservableValue} of this binding is changed. Note that a call to this method will
     * dispose the previous {@link #child}.
     *
     * @param strategy the {@link IStrategy} to be used when the computed {@link Property} changes.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyBinding}.
     *
     * @see IStrategy
     */
    public <TComputedStrategyValue> StrategyBinding<ObservableValue<TComputedValue>, TComputedStrategyValue> thenUseStrategy(final @NotNull IStrategy<ObservableValue<TComputedValue>,
            TComputedStrategyValue> strategy) {
        final StrategyBinding<ObservableValue<TComputedValue>, TComputedStrategyValue> result = createChild(new StrategyBinding<ObservableValue<TComputedValue>, TComputedStrategyValue>());
        result.setStrategy(strategy);
        return result;
    }

    // endregion
}