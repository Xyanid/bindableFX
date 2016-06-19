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
 * @author Xyanid on 18.06.2016.
 */
public abstract class OldValueStrategy<TValue, TComputedValue> implements IComputeStrategy<TValue, TComputedValue> {

    // region Fields

    @Nullable
    private WeakReference<TValue> oldValue;

    // endregion

    //region Getter/Setter

    public Optional<TValue> getOldValue() {
        if (oldValue != null) {
            return Optional.ofNullable(oldValue.get());
        }
        return Optional.empty();
    }

    public void setOldValue(@NotNull final TValue oldValue) {
        this.oldValue = new WeakReference<>(oldValue);
    }

    //endregion
}
