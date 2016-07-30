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

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This interface allows for fluent api calls when binding {@link ObservableValue} or {@link Property}.
 *
 * @author Xyanid on 29.07.2016.
 */
public interface IFluentBinding<TValue> extends ObservableValue<TValue> {

    /**
     * Sets a fallback value that will be returned if the underlying {@link ObservableValue} is not yet set and therefor null.
     *
     * @param value the value to fallback on.
     *
     * @return this {@link IFluentBinding}.
     */
    IFluentBinding<TValue> fallbackOn(final TValue value);

    /**
     * Attaches the given {@link ChangeListener} as a {@link javafx.beans.value.WeakChangeListener} to this binding, so it does not block elements referenced in the {@link ChangeListener} to be
     * cleaned up.
     *
     * @param changeListener the {@link ChangeListener} to use.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #stopWaitingForChange().
     */
    IFluentBinding<TValue> waitForChange(final ChangeListener<TValue> changeListener);

    /**
     * Removes the {@link ChangeListener} that has been attached via the {@link #waitForChange(ChangeListener)} from this binding.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #waitForChange(ChangeListener)
     */
    IFluentBinding<TValue> stopWaitingForChange();

    /**
     * Creates a new {@link IFluentBinding} that converts the value of this {@link IFluentBinding} into another type.
     *
     * @param converter         the {@link Function} to use when converting form the type of this {@link IFluentBinding} into the desired type.
     * @param <TConvertedValue> the desired type to convert into.
     *
     * @return a new {@link IFluentBinding} which listens to changes of this {@link IFluentBinding}.
     *
     * @see ConverterBinding
     */
    default <TConvertedValue> IFluentBinding<TConvertedValue> convertTo(@NotNull final Function<TValue, TConvertedValue> converter) {
        return new ConverterBinding<>(this, converter);
    }

    /**
     * Creates a new {@link IFluentBinding} that listens to changes of this {@link IFluentBinding} and then relays to another {@link ObservableValue} that is being watched.
     *
     * @param relayResolver           the {@link Function} that is used to determine the new {@link ObservableValue} that is to be watched by the newly created {@link IFluentBinding}.
     * @param <TRelayedValue>         the type of value of the relayed {@link ObservableValue}.
     * @param <TRelayedObservedValue> the type of the {@link ObservableValue}.
     *
     * @return a new {@link IFluentBinding}.
     *
     * @see RelayBinding
     */
    default <TRelayedValue, TRelayedObservedValue extends ObservableValue<TRelayedValue>> IFluentBinding<TRelayedValue> thenObserve(
            @NotNull final Function<TValue, TRelayedObservedValue> relayResolver) {
        return new RelayBinding<>(this, relayResolver);
    }

    /**
     * Creates a new {@link IFluentBinding} that listens to changes of this {@link IFluentBinding} and then relays to another {@link Property} that is being watched.
     *
     * @param relayResolver      the {@link Function} that is used to determine the new {@link Property} that is to be watched by the newly created {@link IFluentBinding}.
     * @param <TRelayedValue>    the type of value of the relayed {@link Property}.
     * @param <TRelayedProperty> the type of the {@link Property}.
     *
     * @return a new {@link IFluentBinding}.
     *
     * @see PropertyBinding
     */
    default <TRelayedValue, TRelayedProperty extends Property<TRelayedValue>> IPropertyBinding<TRelayedValue> thenObserveProperty(@NotNull final Function<TValue, TRelayedProperty> relayResolver) {
        return new PropertyBinding<>(this, relayResolver);
    }
}
