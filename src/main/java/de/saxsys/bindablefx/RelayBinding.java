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
 * This class will act as a relay binding, meaning when the {@link #observedValue} is changed the {@link #relayProvider} will be invoked, so that the
 * next desired relayed object will be known.
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class RelayBinding<TValue, TComputedValue> extends BaseBinding<ObservableValue<TValue>, ObservableValue<TComputedValue>> {

    //region Fields

    /**
     * This function will be called when the {@link #observedValue} has changed.
     */
    @Nullable
    private Function<TValue, ObservableValue<TComputedValue>> relayProvider;

    //endregion

    // region Constructor

    protected RelayBinding() {super();}

    // endregion

    // region Setter

    final void setRelayProvider(@NotNull final Function<TValue, ObservableValue<TComputedValue>> relayProvider) {
        this.relayProvider = relayProvider;
    }

    // endregion

    // region Override

    @Override
    public final ObservableValue<TComputedValue> computeValue() {
        final ObservableValue<TValue> currentValue = getCurrentObservableValue().orElse(null);

        if (relayProvider != null && currentValue != null && currentValue.getValue() != null) {
            return relayProvider.apply(currentValue.getValue());
        }

        return null;
    }

    // endregion
}