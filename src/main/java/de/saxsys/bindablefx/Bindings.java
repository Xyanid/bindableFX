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
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This is a utility class much like the original javaFX {@link javafx.beans.binding.Bindings} to allow easier usage of the binding mechanisms.
 *
 * @author Xyanid on 06.05.2016.
 */
public final class Bindings {

    //region Constructor

    /**
     * Prevents others from creating an instance of this class.
     */
    private Bindings() {}

    //endregion

    // region Methods

    /**
     * Creates a new {@link CascadedRelayBinding} using the given information.
     *
     * @param property                the {@link ObjectProperty} the binding will be observing for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link CascadedRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> CascadedRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedCascaded(final ObjectProperty<TPropertyValue>
                                                                                                                                                  property,
                                                                                                                                          final Function<TPropertyValue,
                                                                                                                                                  ObjectProperty
                                                                                                                                                          <TRelayedPropertyValue>> relayProvider) {
        return new CascadedRelayBinding<>(property, relayProvider);
    }

    /**
     * Creates a new {@link UnidirectionalRelayBinding} that is not reversed using the given information.
     *
     * @param observedProperty        the {@link ObjectProperty} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link ObjectProperty} to bind the relayedProperty against.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link UnidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> UnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayed(
            @NotNull final ObjectProperty<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider,
            @NotNull final ObjectProperty<TRelayedPropertyValue> targetProperty) {
        return new UnidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty);
    }

    /**
     * Creates a new {@link ReverseUnidirectionalRelayBinding} using the given information.
     *
     * @param observedProperty        the {@link ObjectProperty} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link ObjectProperty} to bind the relayedProperty against.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link ReverseUnidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> ReverseUnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindReversedRelayed(
            @NotNull final ObjectProperty<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider,
            @NotNull final ObjectProperty<TRelayedPropertyValue> targetProperty) {
        return new ReverseUnidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty);
    }

    /**
     * Creates a new {@link BidirectionalRelayBinding} using the given information.
     *
     * @param observedProperty        the {@link ObjectProperty} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link ObjectProperty} to bind the relayedProperty against.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link BidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> BidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedBidirectional(
            @NotNull final ObjectProperty<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider,
            @NotNull final ObjectProperty<TRelayedPropertyValue> targetProperty) {
        return new BidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty);
    }

    // endregion
}
