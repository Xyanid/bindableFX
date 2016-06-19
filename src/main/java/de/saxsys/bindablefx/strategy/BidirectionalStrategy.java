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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author xyanid on 30.03.2016.
 */
public class BidirectionalStrategy<TValue> extends TargetStrategy<Property<TValue>, Property<TValue>, Property<TValue>> {

    // region Constructor

    BidirectionalStrategy(@NotNull final Property<TValue> target) {
        super(target);

    }

    // endregion

    //region Protected

    protected void unbind(@NotNull final Property<TValue> target) {
        getOldValue().ifPresent(target::unbindBidirectional);
    }

    protected void bind(@Nullable final Property<TValue> property, @NotNull final Property<TValue> target) {
        if (property != null) {
            target.bindBidirectional(property);
            setOldValue(property);
        }

    }

    //endregion

    // region Override StrategyBinding

    @Override
    public final Property<TValue> computeValue(@Nullable final Property<TValue> property) {
        final Property<TValue> targetProperty = getTarget();
        if (targetProperty != null) {
            unbind(targetProperty);
            bind(property, targetProperty);
        }
        return property;
    }

    @Override
    public final void dispose() {
        final Property<TValue> targetProperty = getTarget();
        if (targetProperty != null) {
            unbind(getTarget());
        }
    }

    // endregion
}