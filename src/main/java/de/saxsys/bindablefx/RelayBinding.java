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

import java.util.function.Function;

/**
 * This class will act as a relay binding, meaning when the {@link #observedProperty} is changed the {@link #relayProvider} will be invoked, so that the
 * next desired relayed object will be known.
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class RelayBinding<TPropertyValue, TRelayedObject> extends BaseBinding<TPropertyValue> {

    // region Fields

    /**
     * This {@link Function} is called when the underlying {@link #observedProperty} has changed and we need a new property which we can then use.
     */
    private final Function<TPropertyValue, TRelayedObject> relayProvider;

    // endregion

    // region Constructor

    protected RelayBinding(@NotNull final Function<TPropertyValue, TRelayedObject> relayProvider) {
        super();

        this.relayProvider = relayProvider;
    }

    // endregion

    // region Getter

    /**
     * Returns the {@link #relayProvider}.
     *
     * @return the {@link #relayProvider}.
     */
    protected final Function<TPropertyValue, TRelayedObject> getRelayProvider() {
        return relayProvider;
    }

    // endregion

    // region Abstract

    /**
     * Will be invoked when the value of the {@link #observedProperty} is changed and the {@link #relayProvider} is applied to the old value, so the old
     * relayed object can be unbound. This will only happen if the old value is not null.
     *
     * @param relayedObject the relayed object which was previously bound.
     */
    protected abstract void unbindProperty(@Nullable final TRelayedObject relayedObject);

    /**
     * Will be invoked when the value of the {@link #observedProperty} is changed and the {@link #relayProvider} is applied to the new value, so the new
     * relayed object can be bound. This will only happen if the new value is not null.
     *
     * @param relayedObject the relayed object which was set and needs to be bound now.
     */
    protected abstract void bindProperty(@Nullable final TRelayedObject relayedObject);

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
    public final void changed(@Nullable final ObservableValue<? extends TPropertyValue> observable,
                              @Nullable final TPropertyValue oldValue,
                              @Nullable final TPropertyValue newValue) {
        if (oldValue != null) {
            unbindProperty(relayProvider.apply(oldValue));
        }
        if (newValue != null) {
            bindProperty(relayProvider.apply(newValue));
        }
    }

    // endregion
}