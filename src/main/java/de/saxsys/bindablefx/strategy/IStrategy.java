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

import org.jetbrains.annotations.Nullable;

/**
 * This interface is used to determine the actual value of any binding.
 *
 * @author Xyanid on 18.06.2016.
 */
public interface IStrategy<TValue, TComputedValue> {

    /**
     * This will use the given value and provide a computed value which is based on it. This method will be called by the
     * {@link de.saxsys.bindablefx.StrategyBinding#computeValue()} method, whenever its {@link de.saxsys.bindablefx.StrategyBinding#observedValue} was set.
     *
     * @param value the value that will be used.
     */
    TComputedValue computeValue(@Nullable final TValue value);

    /**
     * Disposes this strategy, allowing for clean up of the strategy. This method will be called by the {@link de.saxsys.bindablefx.StrategyBinding#dispose()} method.
     */
    void dispose();
}
