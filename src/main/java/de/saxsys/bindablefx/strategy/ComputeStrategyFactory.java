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

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Xyanid on 18.06.2016.
 */
public final class ComputeStrategyFactory {

    // region Constructor

    private ComputeStrategyFactory() {}

    // endregion

    //region Methods

    public static <TValue> IComputeStrategy<TValue, Void> createConsumerStrategy(@NotNull final Consumer<TValue> previousConsumer,
                                                                                 @NotNull final Consumer<TValue> previousObjectConsumer) {
        return new ConsumerStrategy<>(previousConsumer, previousObjectConsumer);
    }

    //    public static <TObservedValue> IComputeStrategy<Property<TObservedValue>> createBidirectionalStrategy(@NotNull final Supplier<Property<TObservedValue>>
    // observableSupplier,
    //                                                                                                          @NotNull final Property<TObservedValue> targetProperty) {
    //        return new BidirectionalStrategy<>(observableSupplier, targetProperty);
    //    }
    //
    //    public static <TObservedValue> IComputeStrategy<Property<TObservedValue>> createResettableBidirectionalStrategy(
    //            @NotNull final Supplier<Property<TObservedValue>> observableSupplier,
    //            @NotNull final Property<TObservedValue> targetProperty,
    //            @NotNull final TObservedValue resetValue) {
    //        return new ResettableBidirectionalStrategy<>(observableSupplier, targetProperty, resetValue);
    //    }

    //    static <TValue> IComputeStrategy<ObservableValue<TValue>> createUnidirectionalStrategy(@NotNull final Supplier<Property<TValue>> observableSupplier,
    //                                                                                    @NotNull final ObservableValue<TValue> targetProperty) {
    //        return new UnidirectionalStrategy<>(observableSupplier, targetProperty);
    //    }


    //endregion
}