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
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This binding will allow for unidirectional binding between the {@link #targetProperty} and {@link Property} which is supplied by the {@link #relayProvider} for the
 * value of the {@link #observedProperty}. So the {@link #targetProperty} will have the same value as the relayed property.
 *
 * @author xyanid on 30.03.2016.
 */
public class UnidirectionalRelayBinding<TPropertyValue, TRelayedPropertyValue> extends TargetBinding<TPropertyValue, TRelayedPropertyValue> {


    // region Constructor

    UnidirectionalRelayBinding(final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider, final Property<TRelayedPropertyValue> targetProperty) {
        super(relayProvider, targetProperty);
    }

    public UnidirectionalRelayBinding(final Property<TPropertyValue> observedProperty,
                                      final Function<TPropertyValue, Property<TRelayedPropertyValue>> relayProvider,
                                      final Property<TRelayedPropertyValue> targetProperty) {
        super(observedProperty, relayProvider, targetProperty);
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(@Nullable final Property<TRelayedPropertyValue> relayedProperty) {
        Property<TRelayedPropertyValue> targetProperty = getTargetPropertyProperty();
        if (targetProperty != null) {
            targetProperty.unbind();
        }
    }

    @SuppressWarnings ("Duplicates")
    @Override
    protected void bindProperty(@Nullable final Property<TRelayedPropertyValue> relayedProperty) {
        if (relayedProperty != null) {
            Property<TRelayedPropertyValue> targetProperty = getTargetPropertyProperty();
            if (targetProperty != null) {
                targetProperty.bind(relayedProperty);
            } else {
                dispose();
            }
        }
    }

    // endregion
}