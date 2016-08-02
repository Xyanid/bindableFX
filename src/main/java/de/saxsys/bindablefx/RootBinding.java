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

import javafx.beans.InvalidationListener;
import javafx.beans.WeakListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is the root of all binding implementations in this library. It only weakly references the {@link ObservableValue} that it is bound to and therefor does not prevent it from being
 * garbage collected. It also allows for values of the
 *
 * @param <TValue> the type of the {@link #observedValue} that is being watched.
 */
@SuppressWarnings ("OptionalUsedAsFieldOrParameterType")
class RootBinding<TValue> extends ObjectBinding<TValue> implements IFluentBinding<TValue>, ChangeListener<TValue>, WeakListener {

    // region Fields

    /**
     * Determines the {@link ObservableValue} which is watched by this binding. Since the binding implenents {@link ChangeListener}, the binding will be registered as a listener of the
     * {@link ObservableValue}.
     */
    @Nullable
    private WeakReference<ObservableValue<TValue>> observedValue;

    /**
     * The value that indicates whether the trigger value will be used or not.
     *
     * @see #replaceWith(Function)
     * @see #replaceWith(Predicate, Object)
     * @see #stopReplacement()
     */
    @Nullable
    private Function<TValue, TValue> valueReplacer;

    /**
     * The value to fallback on if the {@link #observedValue} has not yet been set.
     *
     * @see #fallbackOn(Object)
     */
    @Nullable
    private Supplier<TValue> fallbackSupplier;

    /**
     * The list of {@link ChangeListener}s added to this binding.
     */
    @NotNull
    private final List<ChangeListener<? super TValue>> changeListeners = new ArrayList<>();

    /**
     * The list of {@link InvalidationListener}s added to this binding.
     */
    @NotNull
    private final List<InvalidationListener> invalidationListeners = new ArrayList<>();

    // endregion

    // region Constructor

    RootBinding() {}

    // endregion

    // region Getter

    /**
     * Returns the current value of the {@link #observedValue}.
     *
     * @return {@link Optional#empty()} if the {@link #observedValue} is null or an {@link Optional} of the current value of the {@link #observedValue}.
     */
    public Optional<ObservableValue<TValue>> getObservedValue() {

        if (observedValue == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(observedValue.get());
    }

    // endregion

    // region Observed value

    /**
     * Removes this binding as the listener from the {@link #observedValue}, invokes a call to {@link #changed(ObservableValue, Object, Object)} with the oldValue and then
     * sets the {@link #observedValue} to null.
     */
    protected final void destroyObservedValue() {
        getObservedValue().ifPresent(observedValue -> {
            if (observedValue != null) {
                beforeDestroyObservedValue(observedValue);
                observedValue.removeListener(this);
            }
            this.observedValue = null;
        });
    }

    /**
     * Sets the {@link #observedValue} and adds the this binding as the listener.
     *
     * @param observedValue the {@link ObservableValue} which will be used as the {@link #observedValue}
     */
    protected final void setObservedValue(@NotNull final ObservableValue<TValue> observedValue) {
        // set the property that is being observe and invoke a change so that the implementation can bind the property correctly
        this.observedValue = new WeakReference<>(observedValue);
        afterSetObservedValue(observedValue);
        observedValue.addListener(this);
    }

    /**
     * Will be called when the {@link #observedValue} is about to be destroyed.
     *
     * @param observableValue the current {@link #observedValue}.
     */
    protected void beforeDestroyObservedValue(@NotNull final ObservableValue<TValue> observableValue) {}

    /**
     * Will be called when the {@link #observedValue} was set.
     *
     * @param observableValue the current {@link #observedValue}.
     */
    protected void afterSetObservedValue(@NotNull final ObservableValue<TValue> observableValue) {}

    // endregion

    // region Change Handling

    /**
     * Returns the current value of the {@link #observedValue} if any.
     *
     * @return the current value of the {@link #observedValue} if any.
     */
    @Override
    protected TValue computeValue() {
        final Optional<ObservableValue<TValue>> observedValue = getObservedValue();
        if (observedValue.isPresent()) {
            if (valueReplacer != null) {
                return valueReplacer.apply(observedValue.get().getValue());
            } else {
                return observedValue.get().getValue();
            }
            // return valueReplacer != null ? valueReplacer.apply(observedValue.get().getValue()) : observedValue.get().getValue();
        } else if (fallbackSupplier != null) {
            return fallbackSupplier.get();
        } else {
            return null;
        }
    }

    /**
     * When the observed value is propertyChanged, this binding is invalidated.
     */
    @Override
    public void changed(@Nullable final ObservableValue<? extends TValue> observable, @Nullable final TValue oldValue, @Nullable final TValue newValue) {
        invalidate();
    }

    /**
     * Returns true if the {@link #observedValue} is no longer set.
     *
     * @return true if the {@link #observedValue} is no longer set, otherwise false.
     */
    @Override
    public boolean wasGarbageCollected() {
        return observedValue != null && observedValue.get() == null;
    }

    //endregion

    // region Public

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public IFluentBinding<TValue> fallbackOn(@Nullable final TValue fallbackValue) {
        this.fallbackSupplier = () -> fallbackValue;
        invalidate();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public IFluentBinding<TValue> stopFallbackOn() {
        this.fallbackSupplier = null;
        invalidate();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public boolean hasFallbackValue() {
        return fallbackSupplier != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public IFluentBinding<TValue> replaceWith(@Nullable final Function<TValue, TValue> valueReplacer) {
        this.valueReplacer = valueReplacer;
        invalidate();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public IFluentBinding<TValue> stopReplacement() {
        if (valueReplacer != null) {
            valueReplacer = null;
        }
        invalidate();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasReplacement() {
        return valueReplacer != null;
    }

    @Override
    public void addListener(@NotNull final InvalidationListener listener) {
        super.addListener(listener);
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull final InvalidationListener listener) {
        super.removeListener(listener);
        invalidationListeners.remove(listener);
    }

    @Override
    public void addListener(@NotNull final ChangeListener<? super TValue> listener) {
        super.addListener(listener);
        changeListeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull final ChangeListener<? super TValue> listener) {
        super.removeListener(listener);
        changeListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public IFluentBinding<TValue> stopListeners() {
        while (!invalidationListeners.isEmpty()) {
            removeListener(invalidationListeners.get(0));
        }
        while (!changeListeners.isEmpty()) {
            removeListener(changeListeners.get(0));
        }
        return this;
    }

    @Override
    public boolean hasListeners() {
        return !invalidationListeners.isEmpty() || !changeListeners.isEmpty();
    }

    /**
     * Stops listening to the {@link #observedValue} and also stops the replacement, fallback value and all attached listeners.
     *
     * @see #stopReplacement()
     * @see #stopFallbackOn()
     * @see #stopListeners()
     */
    @Override
    public void dispose() {
        destroyObservedValue();
        stopReplacement();
        stopFallbackOn();
        stopListeners();
        invalidate();
    }

    // endregion
}