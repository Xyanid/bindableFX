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

/**
 * This strategy provides the ability to store a target value which can be used during computation of the result. The target is only weakly referenced so the strategy does not
 * prevent it from being garbage collected.
 *
 * @author Xyanid on 18.06.2016.
 */
public abstract class TargetStrategy<TValue, TComputedValue, TTarget> extends OldValueStrategy<TValue, TComputedValue> {

    // region Fields

    /**
     * The target which is to be used.
     */
    @NotNull
    private final WeakReference<TTarget> target;

    // endregion

    //region Constructor

    protected TargetStrategy(@NotNull final TTarget target) {
        this.target = new WeakReference<>(target);
    }

    //endregion

    //region Getter

    /**
     * Returns the current value of the {@link #target}.
     *
     * @return the current value of the {@link #target}.
     */
    @Nullable
    protected final TTarget getTarget() {
        return target.get();
    }

    //endregion
}
