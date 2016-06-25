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

/**
 * This is a utility class much like the original javaFX {@link javafx.beans.binding.Bindings} to allow easier usage of the binding mechanisms.
 *
 * @author Xyanid on 06.05.2016.
 */
public final class Bindings {

    // region Constructor

    /**
     * Prevents others from creating an instance of this class.
     */
    private Bindings() {}

    // endregion

    // region 2.0 Methods

    public final <TValue, TObservedValue extends ObservableValue<TValue>, TComputedValue, TComputedObservedValue extends ObservableValue<TComputedValue>> CascadedBinding<TValue,
            TObservedValue, TComputedValue, TComputedObservedValue> observe(
            @NotNull final TObservedValue observedValue) {
        return new CascadedBinding<TValue, TObservedValue, TComputedValue, TComputedObservedValue>().observe(observedValue);
    }

    // endregion
}