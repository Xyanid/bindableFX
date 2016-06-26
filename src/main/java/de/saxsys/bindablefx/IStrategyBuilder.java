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

import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface is used for the FluentAPI so that an observation chain can be created. A call to
 *
 * @author Xyanid on 25.06.2016.
 */
public interface IStrategyBuilder<TValue, TObservedValue extends ObservableValue<TValue>> {

    IStrategyBuilder<TValue, TObservedValue> consume(@NotNull final Consumer<TObservedValue> previousValueConsumer, @NotNull final Consumer<TObservedValue> currentValueConsumer);

    IStrategyBuilder<TValue, TObservedValue> fallbackOn(@NotNull final Function<TValue, TValue> resolver);

    IStrategyBuilder<TValue, TObservedValue> bind(@NotNull final ObservableValue<TValue> target);

    IStrategyBuilder<TValue, TObservedValue> bindBidirectional(@NotNull final TObservedValue target);

    IStrategyBuilder<TValue, TObservedValue> bindBidirectionalOrFallbackOn(@NotNull final TObservedValue target, @Nullable final TValue fallbackValue);
}
