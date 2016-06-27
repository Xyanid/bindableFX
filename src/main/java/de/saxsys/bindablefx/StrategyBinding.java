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

import de.saxsys.bindablefx.strategy.IComputeStrategy;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

/**
 * This binding executes the provided {@link IComputeStrategy}, whenever its {@link BaseBinding#observedValue} is changed.
 *
 * @author xyanid on 30.03.2016.
 */
final class StrategyBinding<TObservedValue extends ObservableValue, TComputedValue> extends BaseBinding<TObservedValue, TComputedValue> {

    // region Fields

    /**
     * This is the {@link IComputeStrategy} that will be invoked when the target
     */
    private IComputeStrategy<TObservedValue, TComputedValue> strategy;

    // endregion

    // region Constructor

    StrategyBinding() {}

    // endregion

    // region Setter

    /**
     * sets
     *
     * @param strategy
     */
    void setStrategy(@NotNull final IComputeStrategy<TObservedValue, TComputedValue> strategy) {
        this.strategy = strategy;
        computeValue();
    }

    // endregion

    // region Override BaseBinding

    @Override
    protected final TComputedValue computeValue() {
        return strategy.computeValue(getObservableValue().orElse(null));
    }

    @Override
    public final void dispose() {
        super.dispose();
        strategy.dispose();
    }

    // endregion
}