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

import javafx.beans.WeakListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * This class is the root of all binding implementations in this library. It consists of a {@link WeakReference} to the {@link ObservableValue} that is being watched and a {@link #fallbackValue}.
 * It also contains a convenient {@link WeakChangeListener}, so its possible to subscribe to changes of this binding.
 *
 * @param <TValue> the type of the {@link #observedValue} that is being watched.
 */
class RootBinding<TValue> extends ObjectBinding<TValue> implements IFluentBinding<TValue>, ChangeListener<TValue>, WeakListener {

    // region Fields

    /**
     * Determines the {@link ObservableValue} which is watched by this binding. Since the binding implenents {@link ChangeListener}, the binding will be registered as a listener of the
     * {@link ObservableValue}.
     */
    @Nullable
    private WeakReference<ObservableValue<TValue>> observedValue;

    /**
     * The value to use when the {@link #observedValue} has not yet been set and ist still null.
     *
     * @see #fallbackOn(Object)
     */
    @Nullable
    private TValue fallbackValue;

    /**
     * The current {@link WeakChangeListener} that was added.
     *
     * @see #waitForChange(ChangeListener)
     * @see #stopWaitingForChange()
     */
    @Nullable
    private WeakChangeListener<TValue> changeListener;

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

    /**
     * Returns the current value of the {@link #observedValue}.
     *
     * @return {@link Optional#empty()} if the {@link #observedValue} is null or an {@link Optional} of the current value of the {@link #observedValue}.
     */
    @Override
    public TValue getValue() {

        final Optional<ObservableValue<TValue>> observedValue = getObservedValue();

        if (observedValue.isPresent()) {
            return observedValue.get().getValue();
        } else {
            return this.fallbackValue;
        }
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
                invalidate();
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
        invalidate();
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
        return getValue();
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
     * Sets the {@link #fallbackValue}, which is used whenever this bindings {@link #observedValue} or its value is null.
     *
     * @param value the value to fallback on, when the {@link #observedValue} or its value is null.
     *
     * @return this {@link RootBinding}.
     */
    public RootBinding<TValue> fallbackOn(final TValue value) {
        fallbackValue = value;
        invalidate();
        return this;
    }

    /**
     * Adds a the given {@link ChangeListener} as a {@link WeakChangeListener} to this binding, so it can automatically be removed later one. Note if there is already a {@link #changeListener} set,
     * it will first be removed, before the new {@link ChangeListener} is used.
     *
     * @param changeListener the {@link ChangeListener} to use.
     *
     * @return this {@link RootBinding}.
     *
     * @see #stopWaitingForChange()
     */
    public RootBinding<TValue> waitForChange(final ChangeListener<TValue> changeListener) {
        stopWaitingForChange();

        this.changeListener = new WeakChangeListener<>(changeListener);
        addListener(this.changeListener);
        return this;
    }

    /**
     * Removes the current {@link #changeListener} from this binding. If no {@link ChangeListener} has been set yet, then a call to this method will have no effect.
     *
     * @return this {@link RootBinding}.
     *
     * @see #waitForChange(ChangeListener)
     */
    public RootBinding<TValue> stopWaitingForChange() {
        if (changeListener != null) {
            removeListener(changeListener);
        }
        return this;
    }

    /**
     * Stops listening to the {@link #observedValue} and also sets the {@link #fallbackValue} to null.
     *
     * @see #stopWaitingForChange()
     */
    @Override
    public void dispose() {
        destroyObservedValue();
        stopWaitingForChange();
        fallbackValue = null;
    }

    // endregion
}