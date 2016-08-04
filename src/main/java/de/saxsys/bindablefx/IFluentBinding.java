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
import javafx.beans.binding.Binding;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This interface allows for fluent api calls when binding {@link ObservableValue} or {@link Property}.
 *
 * @author Xyanid on 29.07.2016.
 */
public interface IFluentBinding<TValue> extends Binding<TValue>, WeakListener {

    /**
     * Sets a fallback value that will be returned if the underlying {@link ObservableValue} is not yet set.
     *
     * @param fallbackValue the value to fallback on.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #stopFallbackOn()
     * @see #hasFallbackValue()
     */
    @NotNull IFluentBinding<TValue> fallbackOn(@Nullable final TValue fallbackValue);

    /**
     * Removes the fallback value, so that null will be returned instead of the fallback value.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #fallbackOn(Object)
     * @see #hasFallbackValue()
     */
    @NotNull IFluentBinding<TValue> stopFallbackOn();

    /**
     * Determines if the binding has as fallback value.
     *
     * @return true if the binding has a fallback value, otherwise false.
     *
     * @see #fallbackOn(Object)
     * @see #stopFallbackOn()
     */
    boolean hasFallbackValue();

    /**
     * Fallback on the given fallback value if the value of the underlying {@link ObservableValue} computes to true using the given {@link Predicate}.
     *
     * @param fallbackTriggerValue determine when the value of the underlying {@link ObservableValue} will be replaced with the given fallback value.
     * @param fallbackValue        the value to fallback on.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #replaceWith(Function)
     * @see #stopReplacement()
     * @see #hasReplacement()
     */
    @NotNull
    default IFluentBinding<TValue> replaceWith(@NotNull final Predicate<TValue> fallbackTriggerValue, @Nullable final TValue fallbackValue) {
        return replaceWith(value -> fallbackTriggerValue.test(value) ? fallbackValue : value);
    }

    /**
     * Applies the given {@link Function} whenever the underlying {@link ObservableValue} is changed, allowing for certain values to be replaced or ignored. In order to stop this behaviour a call
     * to {@link #stopReplacement()} is required. A call to this method should overwrite previous call, so only the last applied behaviour is used.
     *
     * @param valueReplacer the {@link Function} which decided if the value of the underlying {@link ObservableValue} will be replaced with the fallback value.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #replaceWith(Predicate, Object)
     * @see #stopReplacement()
     * @see #stopReplacement()
     * @see #hasReplacement()
     */
    @NotNull IFluentBinding<TValue> replaceWith(@Nullable final Function<TValue, TValue> valueReplacer);

    /**
     * Stops replacing the value of the underlying {@link ObservableValue}.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #replaceWith(Function)
     * @see #replaceWith(Predicate, Object)
     * @see #hasReplacement()
     */
    @NotNull IFluentBinding<TValue> stopReplacement();

    /**
     * Determines if the binding has a replacement.
     *
     * @return true if the binding has a replacement, otherwise false.
     *
     * @see #replaceWith(Predicate, Object)
     * @see #replaceWith(Function)
     * @see #stopReplacement()
     */
    boolean hasReplacement();

    /**
     * Removes all the {@link ChangeListener} and {@link InvalidationListener} that have been attached to this binding.
     *
     * @return this {@link IFluentBinding}.
     *
     * @see #addListener(ChangeListener)
     * @see #addListener(InvalidationListener)
     * @see #hasListeners()
     */
    @NotNull IFluentBinding<TValue> stopListeners();

    /**
     * Determines if this binding has any {@link ChangeListener} or {@link InvalidationListener} attached to it.
     *
     * @return true if the binding has any {@link ChangeListener} or {@link InvalidationListener} attached to it, otherwise false.
     *
     * @see #stopListeners()
     * @see #addListener(ChangeListener)
     * @see #addListener(InvalidationListener)
     */
    boolean hasListeners();

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
    @NotNull
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
    @NotNull
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
    @NotNull
    default <TRelayedValue, TRelayedProperty extends Property<TRelayedValue>> IPropertyBinding<TRelayedValue> thenObserveProperty(@NotNull final Function<TValue, TRelayedProperty> relayResolver) {
        return new PropertyBinding<>(this, relayResolver);
    }
}
