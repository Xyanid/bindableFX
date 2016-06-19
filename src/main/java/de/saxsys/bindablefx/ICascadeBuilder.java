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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Xyanid on 18.06.2016.
 */
public interface ICascadeBuilder<TObservedValue extends ObservableValue, TComputedValue extends ObservableValue> {

    default ICascadeBuilder<TObservedValue, TComputedValue> observe(@NotNull final TObservedValue observedValue) {
        CascadedBinding<TObservedValue, TComputedValue> result = new CascadedBinding<>();
        result.setObservedValue(observedValue);
        return result;
    }

    ICascadeBuilder<TObservedValue, TComputedValue> wait(Function<TObservedValue, TComputedValue> relayProvider);

    <TCascadedComputedValue extends ObservableValue> ICascadeBuilder<TComputedValue, TCascadedComputedValue> waitFor(
            @NotNull final Function<TComputedValue, TCascadedComputedValue> relayProvider);

    <TCascadedComputedValue> StrategyBinding<TComputedValue, TCascadedComputedValue> consume(@NotNull final Consumer<TComputedValue> previousValueConsumer,
                                                                                             @NotNull final Consumer<TComputedValue> currentValueConsumer);
}
