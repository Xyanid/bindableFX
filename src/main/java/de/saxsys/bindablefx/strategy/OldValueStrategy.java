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

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * This strategy allows for the new value that is used in the {@link IStrategy#onValueChanged(Object)} method to be saved and used when a new value gets computed. The old
 * value will be weakly referenced.
 *
 * @author Xyanid on 18.06.2016.
 */
public abstract class OldValueStrategy<TValue> implements IStrategy<TValue> {

    // region Fields

    /**
     * The weak reference to the old value.
     */
    @Nullable
    private WeakReference<TValue> oldValue;

    // endregion

    //region Getter/Setter

    /**
     * Get the {@link #oldValue}.
     *
     * @return the {@link #oldValue}.
     */
    public Optional<TValue> getOldValue() {
        if (oldValue != null) {
            return Optional.ofNullable(oldValue.get());
        }
        return Optional.empty();
    }

    /**
     * Sets the {@link #oldValue}
     *
     * @param oldValue the value to be used.
     */
    public void setOldValue(@NotNull final TValue oldValue) {
        this.oldValue = new WeakReference<>(oldValue);
    }

    //endregion
}
