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
 * This strategy is used to bind the computed {@link Property} bidirectional against the {@link #target}.
 *
 * @author xyanid on 30.03.2016.
 */
public class BidirectionalStrategy<TValue> extends TargetStrategy<Property<TValue>, Void, Property<TValue>> {

    // region Constructor

    BidirectionalStrategy(@NotNull final Property<TValue> target) {
        super(target);

    }

    // endregion

    //region Protected

    /**
     * Unbinds the {@link #oldValue} from the {@link #target}.
     *
     * @param target the current {@link #target}.
     */
    protected void unbind(@NotNull final Property<TValue> target) {
        getOldValue().ifPresent(target::unbindBidirectional);
    }

    /**
     * Bidirectional bind the {@link Property} against the {@link #target}.
     *
     * @param property the current {@link Property}.
     * @param target   the {@link #target}.
     */
    protected void bind(@Nullable final Property<TValue> property, @NotNull final Property<TValue> target) {
        if (property != null) {
            target.bindBidirectional(property);
            setOldValue(property);
        }

    }

    //endregion

    // region Override StrategyBinding

    /**
     * Unbinds the {@link #oldValue} and the binds the new computed {@link Property} bidirectional against the {@link #target}.
     *
     * @param property the {@link Property} that is being observed.
     *
     * @return null.
     */
    @Override
    public final Void computeValue(@Nullable final Property<TValue> property) {
        final Property<TValue> target = getTarget();
        if (target != null) {
            unbind(target);
            bind(property, target);
        }
        return null;
    }

    /**
     * Disposes this strategy and unbinds the {@link #target} from the {@link #oldValue}.
     */
    @Override
    public final void dispose() {
        final Property<TValue> target = getTarget();
        if (target != null) {
            unbind(target);
            getTarget();
        }
    }

    // endregion
}