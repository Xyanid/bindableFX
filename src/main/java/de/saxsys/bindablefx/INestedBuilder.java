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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface is used for the FluentAPI so that an observation chain can be created.
 *
 * @author Xyanid on 25.06.2016.
 */
public interface INestedBuilder<TValue, TObservedValue extends ObservableValue<TValue>, TComputedValue, TComputedObservedValue extends Property<TComputedValue>> {

    /**
     * Stats observing the given {@link ObservableValue}.
     *
     * @param observedValue the {@link ObservableValue} to observe.
     *
     * @return a new {@link INestedBuilder} instance.
     */
    default INestedBuilder<TValue, TObservedValue, TComputedValue, TComputedObservedValue> observe(@NotNull final TObservedValue observedValue) {
        final NestedBinding<TValue, TObservedValue, TComputedValue, TComputedObservedValue> result = new NestedBinding<>();
        result.setObservedValue(observedValue);
        return this;
    }

    /**
     * Starts observing the {@link Property} that is provided by the {@link InitialFunction}. Prior to call to this method, a call to {@link #observe(ObservableValue)} was
     * already made, to the {@link InitialFunction} uses the already known {@link ObservableValue}.
     *
     * @param relayProvider the {@link InitialFunction} used to get the next {@link Property} to observe.
     *
     * @return the current {@link INestedBuilder}, whose {@link NestedBinding#nestedResolver} has been set.
     */
    INestedBuilder<TValue, TObservedValue, TComputedValue, TComputedObservedValue> thenObserve(
            @NotNull final InitialFunction<TObservedValue, TComputedObservedValue> relayProvider);

    <TCascadedComputedValue, TCascadedComputedObservedValue extends Property<TCascadedComputedValue>> INestedBuilder<TComputedValue, TComputedObservedValue,
            TCascadedComputedValue, TCascadedComputedObservedValue> thenObserve(
            final @NotNull Function<TComputedObservedValue, TCascadedComputedObservedValue> relayProvider);

    StrategyBinding<TComputedObservedValue, Void> thenConsume(@NotNull final Consumer<TComputedObservedValue> previousValueConsumer,
                                                              @NotNull final Consumer<TComputedObservedValue> currentValueConsumer);

    StrategyBinding<TComputedObservedValue, TComputedValue> thenFallbackOn(@NotNull final Function<TComputedValue, TComputedValue> resolver);

    StrategyBinding<TComputedObservedValue, TComputedObservedValue> thenBind(@NotNull final ObservableValue<TComputedValue> target);

    StrategyBinding<TComputedObservedValue, TComputedObservedValue> thenBindBidirectional(@NotNull final TComputedObservedValue target);

    StrategyBinding<TComputedObservedValue, TComputedObservedValue> thenBindBidirectionalOrFallbackOn(@NotNull final TComputedObservedValue target,
                                                                                                      @Nullable final TComputedValue fallbackValue);
}
