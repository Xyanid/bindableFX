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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * This class represents a container for a value. It allows to determine if the container currently has a value and also allows for NULL to be recognised as a valid value.
 *
 * @author Xyanid on 05.08.2016.
 */
public class ValueContainer<TValue> {

    // region Fields

    /**
     * Determines if this {@link ValueContainer}s {@link #value} has been set. This is needed since even null might be a valid value that has been set.
     */
    private boolean hasValue;

    /**
     * The value this {@link ValueContainer} currently provides
     */
    @Nullable
    private TValue value;

    // endregion

    // region Public

    /**
     * Determines if this containers {@link #value} has been set at one point in time.
     *
     * @return true if the {@link #value} has been set, otherwise false.
     */
    public boolean hasValue() {
        return hasValue;
    }

    /**
     * Returns the {@link #value}.
     *
     * @return the {@link #value}.
     */
    @Nullable
    public TValue getValue() {
        return value;
    }

    /**
     * Sets the {@link #value} and also marks this {@link ValueContainer} as having a value.
     *
     * @param value the value that is to bet set.
     */
    public void setValue(@Nullable final TValue value) {
        hasValue = true;
        this.value = value;
    }

    /**
     * Clears the {@link #value} and marks this {@link ValueContainer} has not having a value.
     */
    public void clearValue() {
        hasValue = false;
        value = null;
    }

    /**
     * Calls the given {@link Consumer} if this {@link ValueContainer} currently has a value.
     *
     * @param consumer the {@link Consumer} to use for this {@link ValueContainer}s {@link #value} if it has one.
     */
    public void ifPresent(@NotNull final Consumer<TValue> consumer) {
        if (hasValue) {
            consumer.accept(value);
        }
    }

    // endregion
}
