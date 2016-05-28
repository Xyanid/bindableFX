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
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * This class represents the basic binding class, it simply hold the {@link ObjectProperty} and a {@link ChangeListener}, which will be invoked each time the
 * observedProperty is changed. Every time the {@link #observedProperty} is changed the class extending from this class will be informed and can react to the
 * new state of the observedProperty according to its own needs.
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class BaseBinding<TPropertyValue> implements ChangeListener<TPropertyValue>, WeakListener {

    //region Fields

    /**
     * Determines the {@link ObservableValue} which is watched by this binding.
     */
    @Nullable
    private WeakReference<ObservableValue<TPropertyValue>> observedProperty;

    //endregion

    // region Getter

    /**
     * Returns the current value of the {@link #observedProperty}.
     *
     * @return {@link Optional#empty()} if the {@link #observedProperty} is null or an {@link Optional} of the current value of the {@link #observedProperty}.
     */
    public Optional<TPropertyValue> getCurrentObservedValue() {
        if (observedProperty == null) {
            return Optional.empty();
        }

        ObservableValue<TPropertyValue> property = observedProperty.get();
        if (property != null) {
            return Optional.ofNullable(property.getValue());
        } else {
            return Optional.empty();
        }
    }

    // endregion

    // region Package Private

    /**
     * Removes this binding as the listener from the {@link #observedProperty}, invokes a call to {@link #changed(ObservableValue, Object, Object)} with the oldValue and then
     * sets the {@link #observedProperty} to null.
     */
    void destroyObservedProperty() {
        if (observedProperty != null) {
            ObservableValue<TPropertyValue> property = observedProperty.get();
            if (property != null) {
                property.removeListener(this);
                changed(property, property.getValue(), null);
            }
            observedProperty = null;
        }
    }

    /**
     * Sets the {@link #observedProperty} and adds the this binding as the listener.
     *
     * @param observedProperty the {@link ObservableValue} which will be used as the {@link #observedProperty}
     */
    void createObservedProperty(@NotNull final ObservableValue<TPropertyValue> observedProperty) {
        // set the property that is being observe and invoke a change so that the implementation can bind the property correctly
        this.observedProperty = new WeakReference<>(observedProperty);
        observedProperty.addListener(this);
        changed(observedProperty, null, observedProperty.getValue());
    }

    // endregion

    // region Public

    /**
     * Removes this binding as the listener from the {@link #observedProperty}, invokes a call to {@link #changed(ObservableValue, Object, Object)} with the oldValue and then
     * sets the {@link #observedProperty} to null. After the call, this {@link BaseBinding} will not longer work and the {@link #observedProperty} needs to be reset using
     * {@link #createObservedProperty(ObservableValue)}.
     */
    public void dispose() {
        destroyObservedProperty();
    }

    // endregion

    // region Implement WeakListener

    /**
     * Returns true if the {@link #observedProperty} is no longer set.
     *
     * @return true if the {@link #observedProperty} is no longer set, otherwise false.
     */
    public boolean wasGarbageCollected() {
        return observedProperty != null && observedProperty.get() == null;
    }

    // endregion
}