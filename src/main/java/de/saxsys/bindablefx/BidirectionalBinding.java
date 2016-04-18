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

import javafx.beans.property.ObjectProperty;

import java.util.function.Function;

/**
 * This binding will allow for bidirectional binding between he {@link ObjectProperty} which is supplied by the {@link #relayProvider} for the value of the
 * {@link #observedProperty} and the {@link #targetProperty}.
 *
 * @author xyanid on 30.03.2016.
 */
public class BidirectionalBinding<TPropertyValue, TRelayedPropertyValue, TRelayedProperty extends ObjectProperty<TRelayedPropertyValue>>
        extends TargetBinding<TPropertyValue, TRelayedPropertyValue, TRelayedProperty> {

    // region Constructor

    public BidirectionalBinding(final Function<TPropertyValue, TRelayedProperty> relayProvider, final TRelayedProperty targetProperty) {
        super(relayProvider, targetProperty);
    }

    // endregion

    // region Override RelayBinding

    /**
     * {@inheritDoc}
     *
     * @param relayedProperty the {@link ObjectProperty} which was previously bound.
     */
    @Override
    protected void unbindProperty(final TRelayedProperty relayedProperty) {
        if (relayedProperty != null) {
            relayedProperty.unbindBidirectional(targetPropertyProperty());
        }
    }

    @Override
    protected void bindProperty(final TRelayedProperty relayedProperty) {
        if (relayedProperty != null) {
            relayedProperty.bindBidirectional(targetPropertyProperty());
        }
    }

    // endregion
}