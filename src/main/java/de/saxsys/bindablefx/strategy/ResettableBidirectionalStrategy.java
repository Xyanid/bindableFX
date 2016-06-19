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
public final class ResettableBidirectionalStrategy<TValue> extends BidirectionalStrategy<TValue> {

    // region Fields

    /**
     * This value wil be used when the relayed property is unbound.
     */
    @Nullable
    private final TValue resetValue;

    // endregion

    // region Constructor

    ResettableBidirectionalStrategy(@NotNull final Property<TValue> targetProperty, @Nullable final TValue resetValue) {
        super(targetProperty);
        this.resetValue = resetValue;
    }

    // endregion

    // region Override BidirectionalBinding

    @Override
    protected void unbind(@NotNull final Property<TValue> target) {
        super.unbind(target);
        target.setValue(resetValue);
    }

    // endregion
}