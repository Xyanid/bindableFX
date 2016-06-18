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

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Xyanid on 18.06.2016.
 */
public final class ComputeStrategyFactory {

    // region Constructor

    private ComputeStrategyFactory() {}

    // endregion

    //region Methods

    <TObservable extends ObservableValue> IComputeStrategy<TObservable> createConsumerStrategy(@NotNull final Supplier<TObservable> observableSupplier,
                                                                                               @NotNull final Consumer<TObservable> previousConsumer,
                                                                                               @NotNull final Consumer<TObservable> previousObjectConsumer) {
        return new ConsumerStrategy<>(observableSupplier, previousConsumer, previousObjectConsumer);
    }

    <TObservedValue> IComputeStrategy<Property<TObservedValue>> createBidirectionalStrategy(@NotNull final Supplier<Property<TObservedValue>> observableSupplier,
                                                                                            @NotNull final Property<TObservedValue> targetProperty) {
        return new BidirectionalStrategy<>(observableSupplier, targetProperty);
    }

    <TObservedValue> IComputeStrategy<Property<TObservedValue>> createResettableBidirectionalStrategy(@NotNull final Supplier<Property<TObservedValue>> observableSupplier,
                                                                                                      @NotNull final Property<TObservedValue> targetProperty,
                                                                                                      @NotNull final TObservedValue resetValue) {
        return new ResettableBidirectionalStrategy<>(observableSupplier, targetProperty, resetValue);
    }

    <TObservedValue> IComputeStrategy<ObservableValue<TObservedValue>> createUnidirectionalStrategy(@NotNull final Supplier<ObservableValue<TObservedValue>> observableSupplier,
                                                                                                    @NotNull final Property<TObservedValue> targetProperty) {
        return new UnidirectionalStrategy<>(observableSupplier, targetProperty);
    }

    <TObservedValue> IComputeStrategy<ObservableValue<TObservedValue>> createResettableUnidirectionalStrategy(
            @NotNull final Supplier<ObservableValue<TObservedValue>> observableSupplier,
            @NotNull final Property<TObservedValue> targetProperty,
            @NotNull final TObservedValue resetValue) {
        return new ResettableUnidirectionalStrategy<>(observableSupplier, targetProperty, resetValue);
    }

    //endregion
}