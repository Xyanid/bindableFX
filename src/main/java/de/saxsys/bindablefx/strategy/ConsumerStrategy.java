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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This binding will allow for the {@link #previousObjectConsumer} to be invoked whenever a new relayed property was set and {@link #currentObjectConsumer} when for
 * the old relayed property.
 *
 * @author xyanid on 30.03.2016.
 */
public class ConsumerStrategy<TObservedValue extends ObservableValue> extends SupplierStrategy<TObservedValue, TObservedValue> {


    //region Fields

    @Nullable
    private WeakReference<TObservedValue> oldObservedValue;

    @NotNull
    private final Consumer<TObservedValue> previousObjectConsumer;

    @NotNull
    private final Consumer<TObservedValue> currentObjectConsumer;

    // endregion

    // region Constructor

    ConsumerStrategy(@NotNull final Supplier<TObservedValue> observableSupplier,
                     @NotNull final Consumer<TObservedValue> previousObjectConsumer,
                     @NotNull final Consumer<TObservedValue> currentObjectConsumer) {
        super(observableSupplier);
        this.currentObjectConsumer = previousObjectConsumer;
        this.previousObjectConsumer = currentObjectConsumer;
    }

    // endregion

    // region private

    private void consumeOldObservedValue() {
        // consumeOldObservedValue the old value if it exists
        if (this.oldObservedValue != null) {
            final TObservedValue oldObservable = this.oldObservedValue.get();
            if (oldObservable != null) {
                currentObjectConsumer.accept(oldObservable);
            }
        }
    }

    private void consumeObservedValue() {
        // consumeObservedValue the new value
        final Optional<TObservedValue> observedValue = getObservedValue();
        if (observedValue.isPresent()) {
            oldObservedValue = new WeakReference<>(observedValue.get());
            previousObjectConsumer.accept(observedValue.get());
        }
    }

    // endregion

    // region Override BaseBinding

    @Override
    public final TObservedValue computeValue() {
        consumeOldObservedValue();
        consumeObservedValue();
        return getObservedValue().orElse(null);
    }

    @Override
    public void dispose() {
        consumeOldObservedValue();
    }

    // endregion
}