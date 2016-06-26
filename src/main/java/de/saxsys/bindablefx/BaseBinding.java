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

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * This is the base class for all bindings contains a {@link WeakReference} to the {@link ObservableValue} is is bound against.
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class BaseBinding<TObservedValue extends ObservableValue, TComputedValue> extends ObjectBinding<TComputedValue> {

    // region Fields

    /**
     * Determines the {@link ObservableValue} which is watched by this binding.
     */
    @Nullable
    private WeakReference<TObservedValue> observedValue;

    // endregion

    // region Constructor

    protected BaseBinding() {}

    // endregion

    // region ObservedValue

    /**
     * Returns the current value of the {@link #observedValue}.
     *
     * @return {@link Optional#empty()} if the {@link #observedValue} is null or an {@link Optional} of the current value of the {@link #observedValue}.
     */
    public Optional<TObservedValue> getObservableValue() {
        if (observedValue == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(observedValue.get());
    }

    /**
     * Removes this binding as the listener from the {@link #observedValue}, invokes a call to {@link #computeValue()} and then
     * sets the {@link #observedValue} to null.
     */
    protected void destroyObservedValue() {
        if (observedValue != null) {
            final TObservedValue value = observedValue.get();
            if (value != null) {
                unbind(value);
                computeValue();
            }
            observedValue.clear();
            observedValue = null;
        }
    }

    /**
     * Creates a new {@link WeakReference} to the given {@link ObservableValue} and uses its own {@link #observedValue}.
     *
     * @param observedValue the {@link ObservableValue} to use.
     */
    protected void setObservedValue(@NotNull final TObservedValue observedValue) {
        // set the property that is being observe and invoke a change so that the implementation can bind the property correctly
        this.observedValue = new WeakReference<>(observedValue);
        bind(observedValue);
        computeValue();
    }

    // endregion

    // region Override ObjectBinding

    /**
     * Removes this binding as the listener from the {@link #observedValue}, invokes a call to {@link #computeValue()} with the oldValue and then
     * sets the {@link #observedValue} to null. After the call, this {@link BaseBinding} will not longer work and the {@link #observedValue} needs to be reset using
     * {@link #setObservedValue(ObservableValue)}.
     */
    @Override
    public void dispose() {
        super.dispose();
        destroyObservedValue();
    }

    // endregion
}