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

import de.saxsys.bindablefx.strategy.ComputeStrategyFactory;
import de.saxsys.bindablefx.strategy.IComputeStrategy;
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
class NestedBinding<TValue, TObservedValue extends ObservableValue<TValue>, TComputedValue, TComputedObservedValue extends Property<TComputedValue>>
        extends BaseBinding<TObservedValue, TComputedObservedValue> implements INestedBuilder<TValue, TObservedValue, TComputedValue, TComputedObservedValue> {

    // region Fields

    /**
     * This function will be called when the {@link #observedValue} has changed.
     */
    @Nullable
    private Function<TObservedValue, TComputedObservedValue> nestedResolver;

    /**
     * The child binding held by this cascaded binding.
     */
    @Nullable
    private BaseBinding<TComputedObservedValue, ?> child;

    // endregion

    // region Constructor

    NestedBinding() {}

    // endregion

    // region Setter

    private void setNestedResolver(@NotNull final Function<TObservedValue, TComputedObservedValue> nestedResolver) {
        this.nestedResolver = nestedResolver;
        computeValue();
    }

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
    @NotNull
    private <TBaseBinding extends BaseBinding<TComputedObservedValue, ?>> TBaseBinding createChild(@NotNull final TBaseBinding binding) {
        disposeChild();

        child = binding;

        computeValue();

        return binding;
    }

    // endregion

    // region Override BaseBinding

    @Override
    public final TComputedObservedValue computeValue() {

        final TObservedValue observedValue = getObservableValue().orElse(null);

        final TComputedObservedValue
                computedValue =
                nestedResolver != null && observedValue != null && observedValue.getValue() != null ? nestedResolver.apply(observedValue) : null;

        if (child != null) {
            // if the computed value we give to the child
            if (computedValue != null) {
                child.setObservedValue(computedValue);
            } else {
                child.destroyObservedValue();
            }
            return null;
        }

        return computedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        disposeChild();
        child = null;
    }

    // endregion

    // region Public

    @Override
    public INestedBuilder<TValue, TObservedValue, TComputedValue, TComputedObservedValue> thenObserve(
            @NotNull final InitialFunction<TObservedValue, TComputedObservedValue> relayProvider) {
        setNestedResolver(relayProvider);
        return this;
    }

    @Override
    public <TCascadedComputedValue, TCascadedComputedObservedValue extends Property<TCascadedComputedValue>> INestedBuilder<TComputedValue, TComputedObservedValue,
            TCascadedComputedValue, TCascadedComputedObservedValue> thenObserve(
            @NotNull final Function<TComputedObservedValue, TCascadedComputedObservedValue> relayProvider) {
        final NestedBinding<TComputedValue, TComputedObservedValue, TCascadedComputedValue, TCascadedComputedObservedValue>
                result = createChild(new NestedBinding<TComputedValue, TComputedObservedValue, TCascadedComputedValue, TCascadedComputedObservedValue>());
        result.setNestedResolver(relayProvider);
        return result;
    }

    @Override
    public StrategyBinding<TComputedObservedValue, Void> thenConsume(@NotNull final Consumer<TComputedObservedValue> previousValueConsumer,
                                                                     @NotNull final Consumer<TComputedObservedValue> currentValueConsumer) {
        final StrategyBinding<TComputedObservedValue, Void> result = createChild(new StrategyBinding<TComputedObservedValue, Void>());
        final IComputeStrategy<TComputedObservedValue, Void> strategy = ComputeStrategyFactory.createConsumerStrategy(previousValueConsumer, currentValueConsumer);
        result.setStrategy(strategy);
        return result;
    }

    @Override
    public StrategyBinding<TComputedObservedValue, TComputedValue> thenFallbackOn(@NotNull final Function<TComputedValue, TComputedValue> resolver) {
        final StrategyBinding<TComputedObservedValue, TComputedValue> result = createChild(new StrategyBinding<TComputedObservedValue, TComputedValue>());
        final IComputeStrategy<TComputedObservedValue, TComputedValue> strategy = ComputeStrategyFactory.createFallbackStrategy(resolver);
        result.setStrategy(strategy);
        return result;
    }


    @Override
    public StrategyBinding<TComputedObservedValue, TComputedObservedValue> thenBind(@NotNull final ObservableValue<TComputedValue> target) {
        final StrategyBinding<TComputedObservedValue, TComputedObservedValue> result = createChild(new StrategyBinding<TComputedObservedValue, TComputedObservedValue>());
        final IComputeStrategy<TComputedObservedValue, TComputedObservedValue> strategy = ComputeStrategyFactory.createUnidirectionalStrategy(target);
        result.setStrategy(strategy);
        return result;
    }

    @Override
    public StrategyBinding<TComputedObservedValue, TComputedObservedValue> thenBindBidirectional(@NotNull final TComputedObservedValue target) {
        final StrategyBinding<TComputedObservedValue, TComputedObservedValue> result = createChild(new StrategyBinding<TComputedObservedValue, TComputedObservedValue>());
        final IComputeStrategy<TComputedObservedValue, TComputedObservedValue> strategy = ComputeStrategyFactory.createBidirectionalStrategy(target);
        result.setStrategy(strategy);
        return result;
    }

    @Override
    public StrategyBinding<TComputedObservedValue, TComputedObservedValue> thenBindBidirectionalOrFallbackOn(@NotNull final TComputedObservedValue target,
                                                                                                             @Nullable final TComputedValue fallbackValue) {
        final StrategyBinding<TComputedObservedValue, TComputedObservedValue> result = createChild(new StrategyBinding<TComputedObservedValue, TComputedObservedValue>());
        final IComputeStrategy<TComputedObservedValue, TComputedObservedValue> strategy = ComputeStrategyFactory.createFallbackBidirectionalStrategy(target, fallbackValue);
        result.setStrategy(strategy);
        return result;
    }

    // endregion
}