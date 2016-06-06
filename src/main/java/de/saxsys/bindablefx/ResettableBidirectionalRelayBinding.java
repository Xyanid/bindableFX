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
 * This binding will allow for bidirectional binding between the {@link Property} which is supplied by the {@link #relayProvider} for the value of the
 * {@link #observedProperty} and the {@link #target} and will have its value set to the {@link #resetValue} when
 * its unbound.
 *
 * @author xyanid on 30.03.2016.
 */
public class ResettableBidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> extends BidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> {

    // region Fields

    /**
     * This value wil be used when the relayed property is unbound.
     */
    @Nullable
    private final TRelayedPropertyValue resetValue;

    // endregion

    // region Constructor

    ResettableBidirectionalRelayBinding(@NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                                        @NotNull final Property<TRelayedPropertyValue> targetProperty,
                                        @Nullable final TRelayedPropertyValue resetValue) {
        super(relayProvider, targetProperty);

        this.resetValue = resetValue;
    }

    public ResettableBidirectionalRelayBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                                               @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                                               @NotNull final Property<TRelayedPropertyValue> targetProperty,
                                               @Nullable final TRelayedPropertyValue resetValue) {
        super(observedProperty, relayProvider, targetProperty);

        this.resetValue = resetValue;
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(@Nullable final Property<TRelayedPropertyValue> relayedObject) {
        if (relayedObject != null) {
            Property<TRelayedPropertyValue> targetProperty = getTarget();
            if (targetProperty != null) {
                targetProperty.unbindBidirectional(relayedObject);
                targetProperty.setValue(resetValue);
            }
        }
    }

    // endregion
}