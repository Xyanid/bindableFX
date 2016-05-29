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
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.function.Function;

/**
 * This represents a final binding, which should bind the {@link ObjectProperty} provided by the {@link #relayProvider} to the given {@link #targetProperty}.
 * Binding and unbinding of the {@link #observedProperty} should happen when ever
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class TargetBinding<TPropertyValue, TRelayedProperty, TTargetProperty> extends RelayBinding<TPropertyValue, TRelayedProperty> {

    // region Fields

    /**
     * This is the target property that will be bound to the relayed {@link ObjectProperty} which is provided by applying the value of the
     * {@link #observedProperty} to the {@link #relayProvider}.
     */
    private final WeakReference<TTargetProperty> targetProperty;

    // endregion

    // region Constructor

    protected TargetBinding(@NotNull final Function<TPropertyValue, TRelayedProperty> relayProvider, @NotNull final TTargetProperty targetProperty) {
        super(relayProvider);

        this.targetProperty = new WeakReference<>(targetProperty);
    }

    protected TargetBinding(@NotNull final ObservableValue<TPropertyValue> observedProperty,
                            @NotNull final Function<TPropertyValue, TRelayedProperty> relayProvider,
                            @NotNull final TTargetProperty targetProperty) {
        this(relayProvider, targetProperty);

        createObservedProperty(observedProperty);
    }

    // endregion

    // region Getter

    /**
     * Returns the {@link #targetProperty}.
     *
     * @return the {@link #targetProperty}.
     *
     * @deprecated will be removed in the next release use {@link #getTargetProperty()} instead.
     */
    @Nullable
    @Deprecated
    protected final TTargetProperty getTargetPropertyProperty() {
        return getTargetProperty();
    }

    /**
     * Returns the {@link #targetProperty}.
     *
     * @return the {@link #targetProperty}.
     */
    @Nullable
    protected final TTargetProperty getTargetProperty() {
        return targetProperty.get();
    }

    // endregion

    // region Override BaseBinding

    @Override
    public void dispose() {
        super.dispose();

        targetProperty.clear();
    }

    // endregion
}