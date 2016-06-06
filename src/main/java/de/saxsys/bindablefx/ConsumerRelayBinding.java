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
 * This binding will allow for the {@link #bindRelayedObjectConsumer} to be invoked whenever a new relayed property was set and {@link #unbindRelayedObjectConsumer} when for
 * the old relayed property.
 *
 * @author xyanid on 30.03.2016.
 */
public class ConsumerRelayBinding<TPropertyValue, TRelayedObject> extends RelayBinding<TPropertyValue, TRelayedObject> {


    //region Fields

    /**
     * The {@link Consumer} to be called when the {@link #observedProperty} is changed and the {@link #relayProvider} needs to bind the new relayed property.
     */
    @NotNull
    private final Consumer<TRelayedObject> bindRelayedObjectConsumer;
    /**
     * The {@link Consumer} to be called when the {@link #observedProperty} is changed and the {@link #relayProvider} needs to unbound the old relayed property.
     */
    @NotNull
    private final Consumer<TRelayedObject> unbindRelayedObjectConsumer;

    // endregion

    // region Constructor

    ConsumerRelayBinding(@NotNull final Function<TPropertyValue, TRelayedObject> relayProvider,
                         @NotNull final Consumer<TRelayedObject> unbindRelayedObjectConsumer,
                         @NotNull final Consumer<TRelayedObject> bindRelayedObjectConsumer) {
        super(relayProvider);

        this.unbindRelayedObjectConsumer = unbindRelayedObjectConsumer;
        this.bindRelayedObjectConsumer = bindRelayedObjectConsumer;
    }

    public ConsumerRelayBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                                @NotNull final Function<TPropertyValue, TRelayedObject> relayProvider,
                                @NotNull final Consumer<TRelayedObject> unbindRelayedObjectConsumer,
                                @NotNull final Consumer<TRelayedObject> bindRelayedObjectConsumer) {
        this(relayProvider, unbindRelayedObjectConsumer, bindRelayedObjectConsumer);

        createObservedProperty(observedProperty);
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(@Nullable final TRelayedObject relayedObject) {
        unbindRelayedObjectConsumer.accept(relayedObject);
    }

    @Override
    protected void bindProperty(@Nullable final TRelayedObject relayedObject) {
        bindRelayedObjectConsumer.accept(relayedObject);
    }

    // endregion
}