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
 * {@link #observedProperty} and the {@link #targetProperty}.
 *
 * @author xyanid on 30.03.2016.
 */
public class BidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue>
        extends TargetBinding<TPropertyValue, Property<TRelayedPropertyValue>, Property<TRelayedPropertyValue>> {

    // region Fields

    /**
     * Determines if the {@link #targetProperty} will be set to null when the binding is unbound.
     */
    private final boolean willResetTargetProperty;

    // endregion

    // region Constructor

    BidirectionalRelayBinding(@NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                              @NotNull final Property<TRelayedPropertyValue> targetProperty,
                              final boolean willResetTargetProperty) {
        super(relayProvider, targetProperty);

        this.willResetTargetProperty = willResetTargetProperty;
    }

    BidirectionalRelayBinding(@NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                              @NotNull final Property<TRelayedPropertyValue> targetProperty) {
        this(relayProvider, targetProperty, false);
    }

    public BidirectionalRelayBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                                     @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                                     @NotNull final Property<TRelayedPropertyValue> targetProperty,
                                     final boolean willResetTargetProperty) {
        super(observedProperty, relayProvider, targetProperty);

        this.willResetTargetProperty = willResetTargetProperty;
    }

    public BidirectionalRelayBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                                     @NotNull final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                                     @NotNull final Property<TRelayedPropertyValue> targetProperty) {
        this(observedProperty, relayProvider, targetProperty, false);
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(@Nullable final Property<TRelayedPropertyValue> relayedProperty) {
        if (relayedProperty != null) {
            Property<TRelayedPropertyValue> targetProperty = getTargetProperty();
            if (targetProperty != null) {
                targetProperty.unbindBidirectional(relayedProperty);
                if (willResetTargetProperty) {
                    targetProperty.setValue(null);
                }
            }
        }
    }

    @Override
    protected void bindProperty(@Nullable final Property<TRelayedPropertyValue> relayedProperty) {
        if (relayedProperty != null) {
            Property<TRelayedPropertyValue> targetProperty = getTargetProperty();
            if (targetProperty != null) {
                targetProperty.bindBidirectional(relayedProperty);
            } else {
                dispose();
            }
        }
    }

    // endregion
}