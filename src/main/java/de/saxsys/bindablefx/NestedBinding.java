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
public class NestedBinding<TValue, TRelayedValue> extends BaseBinding<TValue> {

    // region Fields

    /**
     * The child binding held by this cascaded binding.
     */
    @Nullable
    private BaseBinding<TValue> child;

    @NotNull
    private final Function<TValue, ObservableValue<TRelayedValue>> relayResolver;

    // endregion

    // region Constructor

    NestedBinding(@NotNull final Function<TValue, ObservableValue<TRelayedValue>> relayResolver) {
        this.relayResolver = relayResolver;
    }

    // endregion

    // region Strategy or Child

    /**
     */
    private void disposeChild() {
        if (child != null) {
            child.dispose();
            child = null;
        }
    }

    /**
     * Disposes the current {@link #child} or {@link #strategy} and recreates a new {@link NestedBinding} using the given {@link Supplier}.
     *
     * @param <TBaseBinding> the type of the desired new {@link #child}.
     *
     * @return a new {@link NestedBinding} which as been set as the new {@link #child}.
     */
    @NotNull
    private <TBaseBinding extends BaseBinding<TValue>> TBaseBinding createChild(@NotNull final TBaseBinding child) {
        disposeChild();

        this.child = child;

        changed(getObservedValue().orElse(null), null, getValue());

        return child;
    }

    // endregion

    // region Change Handling

    /**
     * When the property was set to something valid, we will use the provided {@link #relayResolver} to get another property which we will listen to
     *
     * @param observable the observable value to use
     * @param oldValue   the old value.
     * @param newValue   the new value.
     */
    @Override
    public final void changed(@Nullable final ObservableValue<? extends TValue> observable, @Nullable final TValue oldValue, @Nullable final TValue newValue) {

    }

    //endregion

    // region Public

    public BaseBinding<TValue> observe(@NotNull final ObservableValue<TValue> observableValue) {
        setObservedValue(observableValue);
        return this;
    }

    /**
     * Starts observing the {@link Property} that is provided by the {@link Function} as soon as this bindings {@link #observedValue} has been set. Note that a call to this
     * method will dispose the previous {@link #child}.
     *
     * @param relayProvider         the {@link Function} used to get the next {@link Property} to observe for the {@link #child}.
     * @param <TNestedRelayedValue> the type of the nested computed value of the {@link #child}.
     *
     * @return this bindings {@link #child}, which is a new {@link NestedBinding}.
     *
     * @see NestedBinding
     */
    public <TNestedRelayedValue> NestedBinding<TRelayedValue, TNestedRelayedValue> thenObserve(@NotNull final Function<TRelayedValue, ObservableValue<TNestedRelayedValue>> relayProvider) {
        return createChild(new NestedBinding<>(relayProvider));
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
     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.ConsumerStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.ConsumerStrategy
     */
    public NestedBinding<TValue, TRelayedValue> thenConsumeValue(final @NotNull Consumer<TRelayedValue> previousValueConsumer, final @NotNull Consumer<TRelayedValue> currentValueConsumer) {

        return this;
    }

    /**
     * Calls the given {@link Function} in order to either use the value of the computed {@link Property} or provide a different one. Note that a call to this method will
     * dispose the previous {@link #child}.
     *
     * @param resolver the {@link Function} to be called when this bindings {@link Property} has been computed and shall be used by the child.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.FallbackStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.FallbackStrategy
     */
    public NestedBinding<TValue, TRelayedValue> thenFallbackOn(@NotNull final Function<ObservableValue<TRelayedValue>, TRelayedValue> resolver) {
        return this;
    }

    /**
     * Binds the computed {@link Property} of this binding against the provides {@link ObservableValue}. Note that a call to this method will dispose the previous
     * {@link #child}.
     *
     * @param target the {@link ObservableValue} against which the computed {@link Property} of this binding will be bound.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.UnidirectionalStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.UnidirectionalStrategy
     */
    public NestedBinding<TValue, TRelayedValue> thenBind(@NotNull final ObservableValue<TRelayedValue> target) {
        return this;
    }

    /**
     * Binds the computed {@link Property} of this binding bidirectional against the provides {@link Property}. Note that a call to this method will dispose the previous
     * {@link #child}.
     *
     * @param target the {@link Property} against which the computed {@link Property} of this binding will be bound.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.BidirectionalStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.BidirectionalStrategy
     */
    public NestedBinding<TValue, TRelayedValue> thenBindBidirectional(@NotNull final Property<TRelayedValue> target) {
        return this;
    }

    /**
     * Binds the computed {@link Property} of this binding bidirectional against the provides {@link Property}. If the value of current computed {@link Property} is null, then
     * the fallbackValue is used instead. Note that a call to this method will dispose the previous {@link #child}.
     *
     * @param target the {@link Property} against which the computed {@link Property} of this binding will be bound.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyListener} using a {@link de.saxsys.bindablefx.strategy.FallbackBidirectionalStrategy}.
     *
     * @see de.saxsys.bindablefx.strategy.FallbackBidirectionalStrategy
     */
    public NestedBinding<TValue, TRelayedValue> thenBindBidirectionalOrFallbackOn(@NotNull final Property<TRelayedValue> target, @Nullable final TRelayedValue fallbackValue) {
        return this;
    }

    /**
     * Uses the provided {@link IStrategy}, which will be invoked each time the computed {@link ObservableValue} of this binding is changed. Note that a call to this method will
     * dispose the previous {@link #child}.
     *
     * @param strategy the {@link IStrategy} to be used when the computed {@link Property} changes.
     *
     * @return this bindings {@link #child}, which also a new {@link StrategyListener}.
     *
     * @see IStrategy
     */
    public NestedBinding<TValue, TRelayedValue> thenUseStrategy(final @NotNull IStrategy<ObservableValue<TRelayedValue>> strategy) {
        return this;
    }

    /**
     * {@inheritDoc}. This implementation also disposes the child if it is available.
     */
    public void dispose() {
        destroyObservedValue();
        disposeChild();
        disposeStrategy();
    }


    // endregion
}