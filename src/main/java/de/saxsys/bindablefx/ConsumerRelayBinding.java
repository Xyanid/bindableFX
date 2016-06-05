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

import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This binding will allow for the {@link #bindRelayedPropertyConsumer} to be invoked whenever a new relayed property was set and {@link #unbindRelayedPropertyConsumer} when for
 * the old relayed property.
 *
 * @author xyanid on 30.03.2016.
 */
public class ConsumerRelayBinding<TPropertyValue, TRelayedValue> extends RelayBinding<TPropertyValue, ObservableValue<TRelayedValue>> {


    //region Fields

    /**
     * The {@link Consumer} to be called when the {@link #observedProperty} is changed and the {@link #relayProvider} needs to bind the new relayed property.
     */
    @NotNull
    private final Consumer<ObservableValue<TRelayedValue>> bindRelayedPropertyConsumer;

    /**
     * The {@link Consumer} to be called when the {@link #observedProperty} is changed and the {@link #relayProvider} needs to unbound the old relayed property.
     */
    @NotNull
    private final Consumer<ObservableValue<TRelayedValue>> unbindRelayedPropertyConsumer;

    // endregion

    // region Constructor

    ConsumerRelayBinding(@NotNull final Function<TPropertyValue, ObservableValue<TRelayedValue>> relayProvider,
                         @NotNull final Consumer<ObservableValue<TRelayedValue>> unbindRelayedPropertyConsumer,
                         @NotNull final Consumer<ObservableValue<TRelayedValue>> bindRelayedPropertyConsumer) {
        super(relayProvider);

        this.unbindRelayedPropertyConsumer = unbindRelayedPropertyConsumer;
        this.bindRelayedPropertyConsumer = bindRelayedPropertyConsumer;
    }

    public ConsumerRelayBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                                @NotNull final Function<TPropertyValue, ObservableValue<TRelayedValue>> relayProvider,
                                @NotNull final Consumer<ObservableValue<TRelayedValue>> unbindRelayedPropertyConsumer,
                                @NotNull final Consumer<ObservableValue<TRelayedValue>> bindRelayedPropertyConsumer) {
        this(relayProvider, unbindRelayedPropertyConsumer, bindRelayedPropertyConsumer);

        createObservedProperty(observedProperty);
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(@Nullable final ObservableValue<TRelayedValue> relayedObject) {
        unbindRelayedPropertyConsumer.accept(relayedObject);
    }

    @Override
    protected void bindProperty(@Nullable final ObservableValue<TRelayedValue> relayedObject) {
        bindRelayedPropertyConsumer.accept(relayedObject);
    }

    // endregion
}