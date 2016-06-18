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
import java.util.function.Supplier;

/**
 * @author Xyanid on 18.06.2016.
 */
public abstract class SupplierStrategy<TObservedValue extends ObservableValue, TComputedValue> implements IComputeStrategy<TComputedValue> {

    // region Fields

    @NotNull
    private final Supplier<TObservedValue> observedValueSupplier;

    // endregion

    //region Constructor

    protected SupplierStrategy(@NotNull final Supplier<TObservedValue> observedValueSupplier) {
        this.observedValueSupplier = observedValueSupplier;
    }

    //endregion

    //region Getter

    /**
     * Returns the value of the {@link #observedValueSupplier}.
     *
     * @return the value of the {@link #observedValueSupplier}.
     */
    @NotNull
    protected final Optional<TObservedValue> getObservedValue() {
        return Optional.ofNullable(observedValueSupplier.get());
    }

    //endregion
}
