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

import javafx.beans.property.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author xyanid on 30.03.2016.
 */
public class BidirectionalStrategy<TValue> extends TargetStrategy<Property<TValue>, Property<TValue>, Property<TValue>> {

    // region Fields

    /**
     * This is the old value that was used so far.
     */
    @Nullable
    private WeakReference<Property<TValue>> oldObservedProperty;

    // endregion

    // region Constructor

    BidirectionalStrategy(@NotNull final Supplier<Property<TValue>> observedValueSupplier, @NotNull final Property<TValue> target) {
        super(observedValueSupplier, target);

    }

    // endregion

    //region Protected

    protected void unbind(@NotNull final Property<TValue> target) {
        if (oldObservedProperty != null) {
            final Property<TValue> oldValue = oldObservedProperty.get();
            if (oldValue != null) {
                target.unbindBidirectional(oldValue);
            }
        }
    }

    protected void bind(@NotNull final Property<TValue> target) {
        final Optional<Property<TValue>> observedValue = getObservedValue();
        if (observedValue.isPresent()) {
            target.bindBidirectional(observedValue.get());
            oldObservedProperty = new WeakReference<>(observedValue.get());
        }

    }

    //endregion

    // region Override StrategyBinding

    @Override
    public final Property<TValue> computeValue() {
        final Property<TValue> targetProperty = getTarget();
        if (targetProperty != null) {
            unbind(targetProperty);
            bind(targetProperty);
        }
        return getObservedValue().orElse(null);
    }

    @Override
    public final void dispose() {
        final Property<TValue> targetProperty = getTarget();
        if (targetProperty != null) {
            unbind(getTarget());
        }
    }

    // endregion
}