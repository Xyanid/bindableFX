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
 * This binding will allow for the {@link #relayedPropertyConsumer} to be invoked whenever a new relayed property was set.
 *
 * @author xyanid on 30.03.2016.
 */
public class ConsumerRelayBinding<TPropertyValue, TRelayedPropertyValue> extends RelayBinding<TPropertyValue, ObservableValue<TRelayedPropertyValue>> {


    //region Fields

    /**
     *
     */
    @NotNull
    private final Consumer<ObservableValue<TRelayedPropertyValue>> relayedPropertyConsumer;

    // endregion

    // region Constructor

    ConsumerRelayBinding(@NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
                         @NotNull final Consumer<ObservableValue<TRelayedPropertyValue>> relayedPropertyConsumer) {
        super(relayProvider);

        this.relayedPropertyConsumer = relayedPropertyConsumer;
    }

    public ConsumerRelayBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                                @NotNull final Function<TPropertyValue, ObservableValue<TRelayedPropertyValue>> relayProvider,
                                @NotNull final Consumer<ObservableValue<TRelayedPropertyValue>> relayedPropertyConsumer) {
        this(relayProvider, relayedPropertyConsumer);

        createObservedProperty(observedProperty);
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(@Nullable final ObservableValue<TRelayedPropertyValue> relayedProperty) {}

    @Override
    protected void bindProperty(@Nullable final ObservableValue<TRelayedPropertyValue> relayedProperty) {
        relayedPropertyConsumer.accept(relayedProperty);
    }

    // endregion
}