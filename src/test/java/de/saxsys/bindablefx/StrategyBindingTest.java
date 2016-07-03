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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Xyanid on 03.07.2016.
 */
@RunWith (MockitoJUnitRunner.class)
public class StrategyBindingTest {

    // region Fields

    @Mock
    private IStrategy<ObservableValue<Long>, Long> strategy;

    private StrategyBinding<ObservableValue<Long>, Long> cut;

    // endregion

    // region Setup

    @Before
    public void setUp() {
        cut = new StrategyBinding<>();
    }

    // endregion

    // region Tests

    /**
     * When the observed value is not set and the strategy gets set the strategy will not be computed.
     */
    @Test
    public void settingTheStrategyWhenAnObservedValueIsNotAvailableWillNotComputeTheValue() {

        final ObservableValue<Long> observableValue = new SimpleObjectProperty<>(0L);

        cut.setStrategy(strategy);

        verify(strategy, times(0)).computeValue(observableValue);
    }

    /**
     * When the observed value is already set and the strategy gets set the strategy will be computed.
     */
    @Test
    public void settingTheStrategyWhenAnObservedValueIsAvailableWillComputeTheValue() {

        final ObservableValue<Long> observableValue = new SimpleObjectProperty<>(0L);

        cut.setObservedValue(observableValue);

        cut.setStrategy(strategy);

        verify(strategy).computeValue(observableValue);
    }

    /**
     * When the strategy is already set and the observable value gets set the strategy will be computed.
     */
    @Test
    public void settingTheObservedValueWhileTheStrategyIsAvailableWillComputeTheValue() {

        final ObservableValue<Long> observableValue = new SimpleObjectProperty<>(0L);

        cut.setStrategy(strategy);

        cut.setObservedValue(observableValue);

        verify(strategy).computeValue(observableValue);
    }


    // endregion
}