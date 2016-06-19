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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author xyanid on 30.03.2016.
 */
public class ConsumerStrategy<TValue> extends OldValueStrategy<TValue, Void> {


    //region Fields

    @NotNull
    private final Consumer<TValue> previousValueConsumer;

    @NotNull
    private final Consumer<TValue> currentValueConsumer;

    // endregion

    // region Constructor

    ConsumerStrategy(@NotNull final Consumer<TValue> previousValueConsumer, @NotNull final Consumer<TValue> currentValueConsumer) {
        this.previousValueConsumer = previousValueConsumer;
        this.currentValueConsumer = currentValueConsumer;
    }

    // endregion

    // region private

    private void consumeOldValue() {
        getOldValue().ifPresent(previousValueConsumer);
    }

    private void consumeValue(@Nullable final TValue value) {
        if (value != null) {
            currentValueConsumer.accept(value);
            setOldValue(value);
        }
    }

    // endregion

    // region Override BaseBinding

    @Override
    public final Void computeValue(@Nullable final TValue value) {
        consumeOldValue();
        consumeValue(value);
        return null;
    }

    @Override
    public void dispose() {
        consumeOldValue();
    }

    // endregion
}