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
 * This strategy consumes the {@link #oldValue} and the new value.
 *
 * @author xyanid on 30.03.2016.
 */
public class ConsumerStrategy<TValue> extends OldValueStrategy<TValue> {


    //region Fields

    /**
     * The {@link Consumer} which consumes the {@link #oldValue}.
     */
    @NotNull
    private final Consumer<TValue> previousValueConsumer;

    /**
     * The {@link Consumer} which is used for the new value.
     */
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

    /**
     * Uses the {@link #previousValueConsumer} to consume the {@link #oldValue}.
     */
    private void consumeOldValue() {
        getOldValue().ifPresent(previousValueConsumer);
    }

    /**
     * Uses the {@link #currentValueConsumer} to consume the given value.
     *
     * @param value the value to consume.
     */
    private void consumeValue(@Nullable final TValue value) {
        if (value != null) {
            currentValueConsumer.accept(value);
            setOldValue(value);
        }
    }

    // endregion

    // region Strategy Handling

    /**
     * Uses the {@link #previousValueConsumer} to consume the {@link #oldValue} and then uses the {@link #currentValueConsumer} to consume the new value.
     *
     * @param value the value to consume.
     */
    @Override
    public final void onValueChanged(@Nullable final TValue value) {
        consumeOldValue();
        consumeValue(value);
    }

    /**
     * Disposes this strategy and consumes the {@link #oldValue}.
     */
    @Override
    public void dispose() {
        consumeOldValue();
    }

    // endregion
}