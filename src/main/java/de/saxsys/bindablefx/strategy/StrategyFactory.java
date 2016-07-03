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

package de.saxsys.bindablefx.strategy;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class creates the desired {@link IStrategy}s.
 *
 * @author Xyanid on 18.06.2016.
 */
public final class StrategyFactory {

    // region Constructor

    private StrategyFactory() {}

    // endregion

    //region Methods

    /**
     * Creates a new {@link ConsumerStrategy}.
     *
     * @param previousConsumer       the {@link Consumer} to use for the previous value.
     * @param previousObjectConsumer the {@link Consumer} to use for the computed value.
     * @param <TValue>               the type of the computed value.
     *
     * @return a new {@link ConsumerStrategy}.
     */
    public static <TValue> IStrategy<ObservableValue<TValue>, Void> createConsumerStrategy(@NotNull final Consumer<TValue> previousConsumer, @NotNull final Consumer<TValue> previousObjectConsumer) {
        return new ConsumerStrategy<>(previousConsumer, previousObjectConsumer);
    }

    /**
     * Creates a new {@link FallbackStrategy}.
     *
     * @param resolver the {@link Function} to use when the {@link ObservableValue} is computed.
     * @param <TValue> the type of the value of the computed {@link ObservableValue}.
     *
     * @return a new {@link FallbackStrategy}.
     */
    public static <TValue> IStrategy<ObservableValue<TValue>, TValue> createFallbackStrategy(@NotNull final Function<ObservableValue<TValue>, TValue> resolver) {
        return new FallbackStrategy<>(resolver);
    }

    /**
     * Creates a new {@link UnidirectionalStrategy}.
     *
     * @param target   the {@link ObservableValue} to bind the computed {@link Property} against.
     * @param <TValue> the type of the value of the computed {@link Property}.
     *
     * @return a new {@link UnidirectionalStrategy}.
     */
    public static <TValue, TProperty extends Property<TValue>> IStrategy<TProperty, Void> createUnidirectionalStrategy(@NotNull final ObservableValue<TValue> target) {
        return new UnidirectionalStrategy<>(target);
    }

    /**
     * Creates a new {@link BidirectionalStrategy}.
     *
     * @param target   the {@link Property} on which to bidirectional bind the computed {@link Property}.
     * @param <TValue> the type of the value of the computed {@link Property}.
     *
     * @return a new {@link BidirectionalStrategy}.
     */
    public static <TValue> IStrategy<Property<TValue>, Void> createBidirectionalStrategy(@NotNull final Property<TValue> target) {
        return new BidirectionalStrategy<>(target);
    }

    /**
     * Creates a new {@link FallbackBidirectionalStrategy}.
     *
     * @param targetProperty the {@link Property}  on which to bidirectional bind the computed {@link Property}.
     * @param fallbackValue  the value to use when the computed {@link Property} is still null.
     * @param <TValue>       the type of the value of the computed {@link Property}.
     *
     * @return a new {@link FallbackBidirectionalStrategy}.
     */
    public static <TValue> IStrategy<Property<TValue>, Void> createFallbackBidirectionalStrategy(@NotNull final Property<TValue> targetProperty, @Nullable final TValue fallbackValue) {
        return new FallbackBidirectionalStrategy<>(targetProperty, fallbackValue);
    }


    //endregion
}