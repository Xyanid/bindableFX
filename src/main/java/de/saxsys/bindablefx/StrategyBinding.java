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

import de.saxsys.bindablefx.strategy.IStrategy;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This binding executes the provided {@link IStrategy}, whenever its {@link BaseBinding#observedValue} is changed.
 *
 * @author xyanid on 30.03.2016.
 */
public final class StrategyBinding<TObservedValue extends ObservableValue, TComputedValue> extends BaseBinding<TObservedValue, TComputedValue> {

    // region Fields

    /**
     * This is the {@link IStrategy} that will be invoked when the target
     */
    private IStrategy<TObservedValue, TComputedValue> strategy;

    // endregion

    // region Constructor

    StrategyBinding() {}

    // endregion

    // region Setter

    /**
     * Sets the {@link #strategy} and then forces the binding to recompute its value base on the new strategy.
     *
     * @param strategy the {@link IStrategy} to use.
     */
    void setStrategy(final @NotNull IStrategy<TObservedValue, TComputedValue> strategy) {
        this.strategy = strategy;
        computeValue();
    }

    // endregion

    // region Override BaseBinding

    /**
     * Executes the current {@link #strategy} if it is know and returns its computed value or null if no strategy is known.
     *
     * @return the computed value of the {@link #strategy} or null.
     */
    @Override
    protected final TComputedValue computeValue() {
        if (strategy != null) {
            final Optional<TObservedValue> observableValue = getObservableValue();
            if (observableValue.isPresent()) {
                strategy.computeValue(observableValue.get());
            }
        }
        return null;
    }

    /**
     * Disposes this binding and also its {@link #strategy} if it is set.
     */
    @Override
    public final void dispose() {
        super.dispose();
        if (strategy != null) {
            strategy.dispose();
        }
    }

    // endregion
}