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
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
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
     * @param property                the {@link Property} the binding will be observing for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link CascadedRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> CascadedRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedCascaded(
            @NotNull final ObservableValue<TPropertyValue> property, @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider) {
        return new CascadedRelayBinding<>(property, relayProvider);
    }

    /**
     * Creates a new {@link UnidirectionalRelayBinding} using the given information.
     *
     * @param observedProperty        the {@link Property} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
     * @param willResetTargetProperty determines if the target property will be set to null when its unbound.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link UnidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> UnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayed(
            @NotNull final ObservableValue<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
            @NotNull final Property<TRelayedPropertyValue> targetProperty,
            final boolean willResetTargetProperty) {
        return new UnidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty, willResetTargetProperty);
    }

    /**
     * Creates a new {@link UnidirectionalRelayBinding} that will not reset the target property when the relayed property is unbound.
     *
     * @param observedProperty        the {@link Property} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link UnidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> UnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayed(
            @NotNull final ObservableValue<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
            @NotNull final Property<TRelayedPropertyValue> targetProperty) {
        return new UnidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty, false);
    }

    /**
     * Creates a new {@link ReverseUnidirectionalRelayBinding} using the given information.
     *
     * @param observedProperty        the {@link Property} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link ObservableValue} to bind the relayedProperty against.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link ReverseUnidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> ReverseUnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindReversedRelayed(
            @NotNull final ObservableValue<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
            @NotNull final ObservableValue<TRelayedPropertyValue> targetProperty) {
        return new ReverseUnidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty);
    }

    /**
     * Creates a new {@link BidirectionalRelayBinding} that will not reset the target property when the relayed property is unbound.
     *
     * @param observedProperty        the {@link ObservableValue} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link BidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> BidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedBidirectional(
            @NotNull final ObservableValue<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
            @NotNull final Property<TRelayedPropertyValue> targetProperty) {
        return new BidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty, false);
    }

    /**
     * Creates a new {@link BidirectionalRelayBinding} using the given information.
     *
     * @param observedProperty        the {@link ObservableValue} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
     * @param willResetTargetProperty determines if the target property will be set to null when its unbound.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link BidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> BidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedBidirectional(
            @NotNull final ObservableValue<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
            @NotNull final Property<TRelayedPropertyValue> targetProperty,
            final boolean willResetTargetProperty) {
        return new BidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty, willResetTargetProperty);
    }

    /**
     * Creates a new {@link ConsumerRelayBinding} using the given information.
     *
     * @param observedProperty        the {@link ObservableValue} that is observed for changes.
     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
     * @param relayedPropertyConsumer the {@link Consumer} to be invoked when a new relayed property is know.
     * @param <TPropertyValue>        the type of the value of the observed property.
     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
     *
     * @return a new {@link BidirectionalRelayBinding}.
     */
    public static <TPropertyValue, TRelayedPropertyValue> ConsumerRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedConsumer(
            @NotNull final ObservableValue<TPropertyValue> observedProperty,
            @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
            @NotNull final Consumer<ObservableValue<TRelayedPropertyValue>> relayedPropertyConsumer) {
        return new ConsumerRelayBinding<>(observedProperty, relayProvider, relayedPropertyConsumer);
    }

    // endregion
}