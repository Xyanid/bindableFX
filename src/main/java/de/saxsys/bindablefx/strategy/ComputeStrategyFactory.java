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
 * @author Xyanid on 18.06.2016.
 */
public final class ComputeStrategyFactory {

    // region Constructor

    private ComputeStrategyFactory() {}

    // endregion

    //region Methods

    public static <TValue, TProperty extends Property<TValue>> IComputeStrategy<TProperty, TProperty> createUnidirectionalStrategy(@NotNull final ObservableValue<TValue> target) {
        return new UnidirectionalStrategy<>(target);
    }

    public static <TValue> IComputeStrategy<TValue, Void> createConsumerStrategy(@NotNull final Consumer<TValue> previousConsumer,
                                                                                 @NotNull final Consumer<TValue> previousObjectConsumer) {
        return new ConsumerStrategy<>(previousConsumer, previousObjectConsumer);
    }

    public static <TValue, TObservedValue extends ObservableValue<TValue>> IComputeStrategy<TObservedValue, TValue> createFallbackStrategy(
            @NotNull final Function<TValue, TValue> resolver) {
        return new FallbackStrategy<>(resolver);
    }

    public static <TValue, TProperty extends Property<TValue>> IComputeStrategy<TProperty, TProperty> createBidirectionalStrategy(@NotNull final TProperty target) {
        return new BidirectionalStrategy<>(target);
    }

    public static <TValue, TProperty extends Property<TValue>> IComputeStrategy<TProperty, TProperty> createFallbackBidirectionalStrategy(@NotNull final TProperty targetProperty,
                                                                                                                                          @Nullable final TValue fallbackValue) {
        return new FallbackBidirectionalStrategy<>(targetProperty, fallbackValue);
    }


    //endregion
}