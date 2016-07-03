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

import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This strategy can be used to provide a fallback value, if the current value shall for some reason not be used or to be altered.
 *
 * @author xyanid on 30.03.2016.
 */
public class FallbackStrategy<TValue> implements IStrategy<ObservableValue<TValue>, TValue> {

    // region Fields

    /**
     * This {@link Function} is to be used when the new value is know shall be returned.
     */
    @NotNull
    private final Function<ObservableValue<TValue>, TValue> resolver;

    // endregion

    // region Constructor

    public FallbackStrategy(@NotNull final Function<ObservableValue<TValue>, TValue> resolver) {
        this.resolver = resolver;
    }

    // endregion

    // region Override RelayBinding

    /**
     * Calls the {@link #resolver} to determine the actual value to use.
     *
     * @param observableValue the current {@link ObservableValue}.
     *
     * @return null or the value provided by the {@link #resolver}.
     */
    @Override
    public final TValue computeValue(@Nullable final ObservableValue<TValue> observableValue) {
        return resolver.apply(observableValue);
    }

    /**
     * Disposes this strategy but does actually nothing.
     */
    @Override
    public void dispose() {}

    // endregion
}