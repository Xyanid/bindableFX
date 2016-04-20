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
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 * This class will act as a relay binding, meaning when the {@link #observedProperty} is changed the {@link #relayProvider} will be invoked, so that the
 * next desired {@link ObjectProperty} will be know using the new value. This also applies to the old value, so that an unbinding is also possible.
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class RelayBinding<TPropertyValue, TRelayedPropertyValue> extends BaseBinding<TPropertyValue> {

    // region Fields

    /**
     * This {@link Function} is called when the underlying {@link #observedProperty} has changed and we need a new property which we can then use.
     */
    private final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider;

    // endregion

    // region Constructor

    protected RelayBinding(final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider) {
        super();

        if (relayProvider == null) {
            throw new IllegalArgumentException("Given relayProvider must not be null");
        }

        this.relayProvider = relayProvider;
    }

    // endregion

    // region Abstract

    /**
     * Will be invoked when the value of the {@link #observedProperty} is changed and the {@link #relayProvider} is applied to the old value, so the old
     * {@link ObjectProperty} can be unbound. This will only happen if the old value is not null.
     *
     * @param relayedProperty the {@link ObjectProperty} which was previously bound.
     */
    protected abstract void unbindProperty(final ObjectProperty<TRelayedPropertyValue> relayedProperty);

    /**
     * Will be invoked when the value of the {@link #observedProperty} is changed and the {@link #relayProvider} is applied to the new value, so the new
     * {@link ObjectProperty} can be bound. This will only happen if the new value is not null.
     *
     * @param relayedProperty the {@link ObjectProperty} which was set and needs to be bound now.
     */
    protected abstract void bindProperty(final ObjectProperty<TRelayedPropertyValue> relayedProperty);

    // endregion

    // region Protected

    /**
     * Gets the desired property by applying the current value of the {@link #observedProperty} to the {@link #relayProvider}.
     *
     * @return the {@link ObjectProperty} that this {@link RelayBinding} will deliver.
     *
     * @throws UnsupportedOperationException if the value of the {@link #observedProperty} is not yet available.
     */
    protected final ObjectProperty<TRelayedPropertyValue> getRelayedProperty() {
        return relayProvider.apply(getCurrentValue().orElseThrow(UnsupportedOperationException::new));
    }

    // endregion

    // region Override BaseBinding

    /**
     * When the property was set to something valid, we will use the provided {@link #relayProvider} to get another property which we will listen to
     *
     * @param observable the observable value to use
     * @param oldValue   the old value.
     * @param newValue   the new value.
     */
    @Override
    protected final void onPropertyChanged(final ObservableValue<? extends TPropertyValue> observable,
                                           final TPropertyValue oldValue,
                                           final TPropertyValue newValue) {
        if (oldValue != null) {
            unbindProperty(relayProvider.apply(oldValue));
        }

        if (newValue != null) {
            bindProperty(relayProvider.apply(newValue));
        }
    }

    /**
     * {@inheritDoc}. This implementation will also unbind the property which was bound using the {@link #relayProvider}, if the current value of the
     * {@link #observedProperty}.
     */
    @Override
    public void dispose() {
        super.dispose();

        getCurrentValue().ifPresent(value -> unbindProperty(relayProvider.apply(value)));
    }

    // endregion
}