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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author xyanid on 30.03.2016.
 */
public class FallbackValueStrategy<TValue> extends SupplierStrategy<ObservableValue<TValue>, TValue> {

    // region Fields

    @NotNull
    private final Function<TValue, TValue> resolver;

    // endregion

    // region Constructor

    FallbackValueStrategy(@NotNull final Supplier<ObservableValue<TValue>> observableSupplier, @NotNull final Function<TValue, TValue> resolver) {
        super(observableSupplier);
        this.resolver = resolver;
    }

    // endregion

    // region Override RelayBinding

    @Override
    public final TValue computeValue() {
        final Optional<ObservableValue<TValue>> observableValue = getObservedValue();
        if (observableValue.isPresent()) {
            return resolver.apply(observableValue.get().getValue());
        }

        return null;
    }

    @Override
    public void dispose() {}

    // endregion
}