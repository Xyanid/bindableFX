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
 * @author xyanid on 30.03.2016.
 */
public class FallbackStrategy<TValue, TObservedValue extends ObservableValue<TValue>> implements IComputeStrategy<TObservedValue, TValue> {

    // region Fields

    @NotNull
    private final Function<TValue, TValue> resolver;

    // endregion

    // region Constructor

    public FallbackStrategy(@NotNull final Function<TValue, TValue> resolver) {
        this.resolver = resolver;
    }

    // endregion

    // region Override RelayBinding

    @Override
    public final TValue computeValue(@Nullable final TObservedValue observableValue) {
        if (observableValue != null) {
            return resolver.apply(observableValue.getValue());
        }

        return null;
    }

    @Override
    public void dispose() {}

    // endregion
}