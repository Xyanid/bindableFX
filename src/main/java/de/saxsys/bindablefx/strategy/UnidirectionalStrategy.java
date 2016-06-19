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
 * @author xyanid on 30.03.2016.
 */
public class UnidirectionalStrategy<TValue> extends TargetStrategy<Property<TValue>, Property<TValue>, ObservableValue<TValue>> {

    // region Constructor

    UnidirectionalStrategy(@NotNull final ObservableValue<TValue> target) {
        super(target);
    }

    // endregion

    // region Protected

    private void unbind() {
        getOldValue().ifPresent(Property::unbind);
    }

    private void bind(@Nullable final Property<TValue> property) {
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

    @Override
    public final Property<TValue> computeValue(@Nullable final Property<TValue> property) {
        unbind();
        bind(property);
        return property;
    }


    @Override
    public void dispose() {
        unbind();
    }

    // endregion
}