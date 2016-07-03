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

package de.saxsys.bindablefx.strategy;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This strategy unbinds the {@link #oldValue} and then binds the new {@link Property} it against the {@link #target}.
 *
 * @author xyanid on 30.03.2016.
 */
public class UnidirectionalStrategy<TValue, TProperty extends Property<TValue>> extends TargetStrategy<TProperty, Void, ObservableValue<TValue>> {

    // region Constructor

    UnidirectionalStrategy(@NotNull final ObservableValue<TValue> target) {
        super(target);
    }

    // endregion

    // region Protected

    /**
     * Unbinds the {@link #oldValue}.
     */
    private void unbind() {
        getOldValue().ifPresent(Property::unbind);
    }

    /**
     * Binds provided {@link Property} against the current {@link #target}.
     *
     * @param property the new {@link Property} of this strategy.
     */
    private void bind(@Nullable final TProperty property) {
        if (property != null) {
            final ObservableValue<TValue> target = getTarget();
            if (target != null) {
                property.bind(target);
                setOldValue(property);
            }
        }
    }

    // endregion

    // region Override RelayBinding

    /**
     * Unbinds the {@link #oldValue} and binds the new {@link Property} against the {@link #target}.
     *
     * @param property the new {@link Property} of this strategy.
     *
     * @return null.
     */
    @Override
    public final Void computeValue(@Nullable final TProperty property) {
        unbind();
        bind(property);
        return null;
    }

    /**
     * Disposes this strategy and unbinds the {@link #oldValue}.
     */
    @Override
    public void dispose() {
        unbind();
    }

    // endregion
}