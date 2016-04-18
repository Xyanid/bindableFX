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

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Optional;

/**
 * This class represents the basic binding class, it simply hold the {@link ObjectProperty} and a {@link ChangeListener}, which will be invoked each time the
 * observedProperty is changed. Every time the {@link #observedProperty} is changed the class extending from this class will be informed and can react to the
 * new state of the observedProperty according to its own needs.
 *
 * @author xyanid on 30.03.2016.
 */
//TODO maybe caching of events that happened during creation of the instance in a sub class need to be cached so they are not lost and
//TODO invoked when the sub class activates the listener ?
public abstract class BaseBinding<TPropertyValue> {

    //region Fields

    /**
     * Determines the {@link ObjectProperty} which is watched by this binding.
     */
    private ObjectProperty<TPropertyValue> observedProperty;
    /**
     * The listener which will be attached to the {@link #observedProperty}.
     */
    private final ChangeListener<TPropertyValue> listener;

    //endregion

    //region Constructor

    /**
     * Creates a new instance and adds the {@link #listener} to the {@link #observedProperty}.
     */
    protected BaseBinding() {
        listener = this::onPropertyChanged;
    }

    //endregion

    // region Getter

    /**
     * Returns the current value of the {@link #observedProperty}.
     *
     * @return {@link Optional#empty()} if the {@link #observedProperty} is null or an {@link Optional} of the current value of the {@link #observedProperty}.
     */
    public Optional<TPropertyValue> getCurrentValue() {
        return observedProperty != null ? Optional.ofNullable(observedProperty.get()) : Optional.empty();
    }

    // endregion

    // region Package Private

    /**
     * Sets the {@link #observedProperty} and adds the {@link #listener} to it.
     *
     * @param observedProperty the {@link ObjectProperty} which will be used as the {@link #observedProperty}
     */
    void setObservedProperty(final ObjectProperty<TPropertyValue> observedProperty) {
        if (observedProperty == null) {
            throw new IllegalArgumentException("Given observedProperty can not be null");
        }

        dispose();

        // set the property that is being observe and invoke a change so that the implementation can bind the property correctly
        this.observedProperty = observedProperty;
        this.observedProperty.addListener(listener);
        onPropertyChanged(observedProperty, null, observedProperty.get());
    }

    // endregion

    // region Public

    /**
     * Removes the {@link #listener} from the {@link #observedProperty} and set it to null. Once a call has been made to this method, this {@link BaseBinding}
     * will not longer work and the {@link #observedProperty} needs to be reset using {@link #setObservedProperty(ObjectProperty)}.
     */
    public void dispose() {
        if (observedProperty != null) {
            observedProperty.removeListener(listener);
            observedProperty = null;
        }
    }

    // endregion

    // region Abstract

    /**
     * Will be called each time the {@link #observedProperty} has changed.
     *
     * @param observable the {@link #observedProperty} that was changed.
     * @param oldValue   the old value.
     * @param newValue   the new value.
     */
    protected abstract void onPropertyChanged(final ObservableValue<? extends TPropertyValue> observable,
                                              final TPropertyValue oldValue,
                                              final TPropertyValue newValue);

    // endregion
}