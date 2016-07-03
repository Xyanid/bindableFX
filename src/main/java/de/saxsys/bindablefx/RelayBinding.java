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
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This binding acts as a relay binding, which waits until its {@link #observedValue} is set and then relays to the next {@link ObservableValue} if the value is no null.
 *
 * @author xyanid on 30.03.2016.
 */
abstract class RelayBinding<TValue, TComputedValue> extends BaseBinding<ObservableValue<TValue>, TComputedValue> {

    // region Fields

    /**
     * This function will be called when the {@link #observedValue} has changed.
     */
    @Nullable
    private Function<TValue, TComputedValue> relayResolver;

    // endregion

    // region Constructor

    RelayBinding() {}

    // endregion

    // region Setter

    protected void setRelayResolver(@NotNull final Function<TValue, TComputedValue> relayResolver) {
        this.relayResolver = relayResolver;
        computeValue();
    }

    // endregion


    // region Override BaseBinding

    /**
     * Computes the relayed {@link }
     *
     * @return the {@link Property} which is relayed by the {@link #relayResolver} or null if either the {@link #relayResolver} or {@link #observedValue} is null.
     */
    @Override
    public TComputedValue computeValue() {

        final ObservableValue<TValue> observedValue = getObservableValue().orElse(null);

        return relayResolver != null && observedValue != null && observedValue.getValue() != null ? relayResolver.apply(observedValue.getValue()) : null;
    }

    // endregion
}