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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Xyanid on 31.07.2016.
 */
public class BidirectionalBindingTest {

    // region enumeration

    private enum ConvertedValue {
        NONE,
        A,
        B,
        C
    }

    // endregion

    // region Fields

    private BidirectionalBinding<Object> cut;

    private Property<Long> x;

    private Property<ConvertedValue> y;

    private IConverter<Long, ConvertedValue> converter;

    // endregion

    // region setup

    @Before
    public void setup() {
        x = new SimpleObjectProperty<>();
        y = new SimpleObjectProperty<>();

        converter = new IConverter<Long, ConvertedValue>() {
            @Nullable
            @Override
            public ConvertedValue convertTo(Long aLong) {
                if (1L == aLong) {
                    return ConvertedValue.A;
                } else if (2L == aLong) {
                    return ConvertedValue.B;
                } else if (3L == aLong) {
                    return ConvertedValue.C;
                } else {
                    return ConvertedValue.NONE;
                }
            }

            @Nullable
            @Override
            public Long convertBack(ConvertedValue s) {

                if (s == null) {
                    return null;
                }

                switch (s) {
                    case A:
                        return 1L;
                    case B:
                        return 2L;
                    case C:
                        return 3L;
                    case NONE:
                    default:
                        return null;
                }
            }
        };
    }

    // endregion

    // region

    /**
     * When the binding is created the current value of the second property will be used for the first property.
     */
    @Test
    public void whenTheBidirectionalBindingIsCreatedTheCurrentValueOfTheSecondPropertyWillBeSetAsTheValueOfTheFirstProperty() {
        y.setValue(ConvertedValue.B);

        cut = Bindings.bindBidirectional(x, y, converter);

        assertEquals(2L, x.getValue().longValue());
    }

    /**
     * When the second property changes, the value will be converted and the first property will be set.
     */
    @Test
    public void whenTheSecondPropertyIsChangedTheFirstPropertyWillHaveItsValueAdjusted() {
        cut = Bindings.bindBidirectional(x, y, converter);

        y.setValue(ConvertedValue.B);

        assertEquals(2L, x.getValue().longValue());
    }

    /**
     * When the first property changes, the value will be converted and the second property will be set.
     */
    @Test
    public void whenTheFirstPropertyIsChangedTheSecondPropertyWillHaveItsValueAdjusted() {
        cut = Bindings.bindBidirectional(x, y, converter);

        x.setValue(1L);

        assertEquals(ConvertedValue.A, y.getValue());
    }

    /**
     * When the first property is garbage collected, the binding will not work and a change of the second property will not effect the first property.
     */
    @Test
    public void whenTheFirstPropertyIsDisposedTheBindingWillNoLongerWork() {
        cut = Bindings.bindBidirectional(x, y, converter);

        x = new SimpleObjectProperty<>();

        System.gc();

        y.setValue(ConvertedValue.C);

        assertNull(x.getValue());
        assertTrue(cut.wasGarbageCollected());
    }

    /**
     * When the first property is garbage collected, the binding will not work and a change of the second property will not effect the first property.
     */
    @Test
    public void whenTheSecondPropertyIsDisposedTheBindingWillNoLongerWork() {
        cut = Bindings.bindBidirectional(x, y, converter);

        y = new SimpleObjectProperty<>();

        System.gc();

        x.setValue(3L);

        assertNull(y.getValue());
        assertTrue(cut.wasGarbageCollected());
    }

    // endregion
}