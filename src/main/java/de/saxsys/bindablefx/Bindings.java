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

import de.saxsys.bindablefx.builder.BaseBindingBuilder;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

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

    // region 2.0 Methods

    public final <TObservable extends ObservableValue, TComputedValue> BaseBindingBuilder<TObservable, TComputedValue> observe(@NotNull final TObservable observable) {
        return new CascadedRelayBinding<>().setObservedValue(observable);
    }


    public static <TPropertyValue> BaseBinding<TPropertyValue> waitFor(@NotNull final ObservableValue<TPropertyValue> observableValue) {
        return new BaseBinding<TPropertyValue>(computeStrategy) {}
    }

    // endregion
    //
    //
    //    // region Methods
    //
    //    /**
    //     * Creates a new {@link CascadedRelayBinding} using the given information.
    //     *
    //     * @param property                the {@link Property} the binding will be observing for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param <TPropertyValue>        the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed {@link ObservableValue}.
    //     *
    //     * @return a new {@link CascadedRelayBinding}.
    //     */
    //    public static <TPropertyValue, TRelayedPropertyValue> CascadedRelayBinding<TPropertyValue, TRelayedPropertyValue> attach(
    //            @NotNull final ObservableValue<TPropertyValue> property, @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider) {
    //        return new CascadedRelayBinding<>(property, relayProvider);
    //    }
    //
    //    /**
    //     * Creates a new {@link UnidirectionalStrategy} that will not reset the target property when the relayed property is unbound.
    //     *
    //     * @param observedProperty        the {@link Property} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
    //     * @param <TPropertyValue>        the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed {@link ObservableValue}.
    //     *
    //     * @return a new {@link UnidirectionalStrategy}.
    //     */
    //    public static <TPropertyValue, TRelayedPropertyValue> UnidirectionalStrategy<TPropertyValue, TRelayedPropertyValue> bind(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final Property<TRelayedPropertyValue> targetProperty) {
    //        return new UnidirectionalStrategy<>(observedProperty, relayProvider, targetProperty);
    //    }
    //
    //    /**
    //     * Creates a new {@link ResettableUnidirectionalStrategy} using the given information.
    //     *
    //     * @param observedProperty          the {@link Property} that is observed for changes.
    //     * @param relayProvider             the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty            the {@link Property} to bind the relayedProperty against.
    //     * @param resetValue                value to use when the target property gets unbound.
    //     * @param <TPropertyValue>          the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedObservableValue> the type of the value of the relayed {@link ObservableValue}..
    //     *
    //     * @return a new {@link ResettableUnidirectionalStrategy}.
    //     */
    //    public static <TPropertyValue, TRelayedObservableValue> ResettableUnidirectionalStrategy<TPropertyValue, TRelayedObservableValue> bind(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, ObservableValue<TRelayedObservableValue>> relayProvider,
    //            @NotNull final Property<TRelayedObservableValue> targetProperty,
    //            @Nullable final TRelayedObservableValue resetValue) {
    //        return new ResettableUnidirectionalStrategy<>(observedProperty, relayProvider, targetProperty, resetValue);
    //    }
    //
    //    /**
    //     * Creates a new {@link ReverseUnidirectionalRelayBinding} using the given information.
    //     *
    //     * @param observedProperty        the {@link Property} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link ObservableValue} to bind the relayedProperty against.
    //     * @param <TPropertyValue>        the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed {@link Property}.
    //     *
    //     * @return a new {@link ReverseUnidirectionalRelayBinding}.
    //     */
    //    public static <TPropertyValue, TRelayedPropertyValue> ReverseUnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindReverse(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final ObservableValue<TRelayedPropertyValue> targetProperty) {
    //        return new ReverseUnidirectionalRelayBinding<>(observedProperty, relayProvider, targetProperty);
    //    }
    //
    //    /**
    //     * Creates a new {@link BidirectionalStrategy} that will not reset the target property when the relayed property is unbound.
    //     *
    //     * @param observedProperty        the {@link ObservableValue} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
    //     * @param <TPropertyValue>        the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed {@link Property}.
    //     *
    //     * @return a new {@link BidirectionalStrategy}.
    //     */
    //    public static <TPropertyValue, TRelayedPropertyValue> BidirectionalStrategy<TPropertyValue, TRelayedPropertyValue> bindBidirectional(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final Property<TRelayedPropertyValue> targetProperty) {
    //        return new BidirectionalStrategy<>(observedProperty, relayProvider, targetProperty);
    //    }
    //
    //    /**
    //     * Creates a new {@link ResettableBidirectionalStrategy} using the given information.
    //     *
    //     * @param observedProperty        the {@link ObservableValue} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
    //     * @param resetValue              value to use when the target property gets unbound.
    //     * @param <TPropertyValue>        the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed {@link Property}.
    //     *
    //     * @return a new {@link ResettableBidirectionalStrategy}.
    //     */
    //    public static <TPropertyValue, TRelayedPropertyValue> ResettableBidirectionalStrategy<TPropertyValue, TRelayedPropertyValue> bindBidirectional(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final Property<TRelayedPropertyValue> targetProperty,
    //            @Nullable final TRelayedPropertyValue resetValue) {
    //        return new ResettableBidirectionalStrategy<>(observedProperty, relayProvider, targetProperty, resetValue);
    //    }
    //
    //    /**
    //     * Creates a new {@link ConsumerStrategy} using the given information.
    //     *
    //     * @param observedProperty              the {@link ObservableValue} that is observed for changes.
    //     * @param relayProvider                 the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param unbindRelayedPropertyConsumer the {@link Consumer} to be invoked when a new relayed property is unbound.
    //     * @param bindRelayedPropertyConsumer   the {@link Consumer} to be invoked when a new relayed property is bound.
    //     * @param <TPropertyValue>              the type of the value of the {@link ObservableValue}.
    //     * @param <TRelayedObject>              the type of the relayed object.
    //     *
    //     * @return a new {@link BidirectionalStrategy}.
    //     */
    //    public static <TPropertyValue, TRelayedObject> ConsumerStrategy<TPropertyValue, TRelayedObject> consume(@NotNull final ObservableValue<TPropertyValue> observedProperty,
    //                                                                                                                @NotNull
    //                                                                                                                final Function<TPropertyValue, TRelayedObject> relayProvider,
    //                                                                                                                @NotNull
    //                                                                                                                final Consumer<TRelayedObject> unbindRelayedPropertyConsumer,
    //                                                                                                                @NotNull
    //                                                                                                                final Consumer<TRelayedObject> bindRelayedPropertyConsumer) {
    //        return new ConsumerStrategy<>(observedProperty, relayProvider, unbindRelayedPropertyConsumer, bindRelayedPropertyConsumer);
    //    }
    //
    //    // endregion
    //
    //    // region Deprecated Methods
    //
    //    /**
    //     * Creates a new {@link CascadedRelayBinding} using the given information.
    //     *
    //     * @param property                the {@link Property} the binding will be observing for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param <TPropertyValue>        the type of the value of the observed property.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
    //     *
    //     * @return a new {@link CascadedRelayBinding}.
    //     *
    //     * @deprecated will be removed in the next version use {@link #attach(ObservableValue, Function)} instead.
    //     */
    //    @Deprecated
    //    public static <TPropertyValue, TRelayedPropertyValue> CascadedRelayBinding<TPropertyValue, TRelayedPropertyValue> bindRelayedCascaded(
    //            @NotNull final ObservableValue<TPropertyValue> property, @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider) {
    //        return attach(property, relayProvider);
    //    }
    //
    //    /**
    //     * Creates a new {@link UnidirectionalStrategy} using the given information.
    //     *
    //     * @param observedProperty        the {@link Property} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
    //     * @param <TPropertyValue>        the type of the value of the observed property.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
    //     *
    //     * @return a new {@link UnidirectionalStrategy}.
    //     *
    //     * @deprecated will be removed in the next version use {@link #bind(ObservableValue, Function, Property)} instead.
    //     */
    //    @Deprecated
    //    public static <TPropertyValue, TRelayedPropertyValue> UnidirectionalStrategy<TPropertyValue, TRelayedPropertyValue> bindRelayed(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final Property<TRelayedPropertyValue> targetProperty) {
    //        return bind(observedProperty, relayProvider, targetProperty);
    //    }
    //
    //    /**
    //     * Creates a new {@link ReverseUnidirectionalRelayBinding} using the given information.
    //     *
    //     * @param observedProperty        the {@link Property} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link ObservableValue} to bind the relayedProperty against.
    //     * @param <TPropertyValue>        the type of the value of the observed property.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
    //     *
    //     * @return a new {@link ReverseUnidirectionalRelayBinding}.
    //     *
    //     * @deprecated will be removed in the next version use {@link #bindReverse(ObservableValue, Function, ObservableValue)} instead.
    //     */
    //    @Deprecated
    //    public static <TPropertyValue, TRelayedPropertyValue> ReverseUnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> bindReversedRelayed(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final ObservableValue<TRelayedPropertyValue> targetProperty) {
    //        return bindReverse(observedProperty, relayProvider, targetProperty);
    //    }
    //
    //    /**
    //     * Creates a new {@link BidirectionalStrategy} that will not reset the target property when the relayed property is unbound.
    //     *
    //     * @param observedProperty        the {@link ObservableValue} that is observed for changes.
    //     * @param relayProvider           the {@link Function} to use when the relayed property the binding requires in needed.
    //     * @param targetProperty          the {@link Property} to bind the relayedProperty against.
    //     * @param <TPropertyValue>        the type of the value of the observed property.
    //     * @param <TRelayedPropertyValue> the type of the value of the relayed property.
    //     *
    //     * @return a new {@link BidirectionalStrategy}.
    //     *
    //     * @deprecated will be removed in the next version use {@link #bindBidirectional(ObservableValue, Function, Property)} instead.
    //     */
    //    @Deprecated
    //    public static <TPropertyValue, TRelayedPropertyValue> BidirectionalStrategy<TPropertyValue, TRelayedPropertyValue> bindRelayedBidirectional(
    //            @NotNull final ObservableValue<TPropertyValue> observedProperty,
    //            @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
    //            @NotNull final Property<TRelayedPropertyValue> targetProperty) {
    //        return bindBidirectional(observedProperty, relayProvider, targetProperty);
    //    }
    //
    //    // endregion
}