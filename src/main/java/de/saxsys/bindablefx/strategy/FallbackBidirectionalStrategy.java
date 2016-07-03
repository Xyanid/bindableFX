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
public final class FallbackBidirectionalStrategy<TValue, TProperty extends Property<TValue>> extends BidirectionalStrategy<TValue, TProperty> {

    // region Fields

    /**
     * This is fallback value which will be used for the {@link #target} whenever it is unbound and the new {@link Property} is still null.
     */
    @Nullable
    private final TValue fallbackValue;

    // endregion

    // region Constructor

    FallbackBidirectionalStrategy(@NotNull final TProperty target, @Nullable final TValue fallbackValue) {
        super(target);
        this.fallbackValue = fallbackValue;
    }

    // endregion

    // region Override BidirectionalBinding

    /**
     * Unbinds the {@link #target} from the {@link #oldValue} and sets the value of the {@link #target} using the {@link #fallbackValue}.
     *
     * @param target the current {@link #target}.
     */
    @Override
    protected void unbind(@NotNull final TProperty target) {
        super.unbind(target);
        target.setValue(fallbackValue);
    }

    // endregion
}