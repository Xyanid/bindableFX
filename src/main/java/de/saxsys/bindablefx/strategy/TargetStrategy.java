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

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

/**
 * @author Xyanid on 18.06.2016.
 */
public abstract class TargetStrategy<TObservedValue extends ObservableValue, TComputedValue, TTarget> extends SupplierStrategy<TObservedValue, TComputedValue> {

    // region Fields

    @NotNull
    private final WeakReference<TTarget> target;

    // endregion

    //region Constructor

    protected TargetStrategy(@NotNull final Supplier<TObservedValue> observableSupplier, @NotNull final TTarget target) {
        super(observableSupplier);
        this.target = new WeakReference<>(target);
    }

    //endregion

    //region Getter

    /**
     * Returns the current value of the {@link #target}.
     *
     * @return the current value of the {@link #target}.
     */
    @Nullable
    protected final TTarget getTarget() {
        return target.get();
    }

    //endregion
}
