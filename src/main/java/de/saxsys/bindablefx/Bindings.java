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

import java.util.function.Function;

/**
 * Utility class to allow for the binding mechanisms in the lib to be used and is also the only point of entrance.
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

    // region Methods

    /**
     * Creates a new {@link NestedBinding} that listens to changes made to the given {@link ObservableValue} and then invokes its own binding mechanism.
     *
     * @param observedValue    the {@link ObservableValue} to listen to.
     * @param <TValue>         the type of the value of the {@link ObservableValue}
     * @param <TComputedValue> the type of the value of the {@link Property} that is computed.
     *
     * @return a new {@link NestedBinding}.
     */
    public static <TValue, TComputedValue> NestedBinding<TValue, TComputedValue> observe(@NotNull final ObservableValue<TValue> observedValue,
                                                                                         @NotNull final Function<TValue, ObservableValue<TComputedValue>> relayProvider) {
        final NestedBinding<TValue, TComputedValue> result = new NestedBinding<>(relayProvider);
        result.setObservedValue(observedValue);
        return result;
    }

    // endregion
}