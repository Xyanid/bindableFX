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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.saxsys.bindablefx.TestUtil.getParent;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * @author Xyanid on 27.07.2016.
 */
@RunWith (MockitoJUnitRunner.class)
public class ConverterBindingTest {

    //region Fields

    private Property<Long> x;

    private IFluentBinding<String> cut;

    //endregion

    //region Setup

    @Before
    public void setUp() {

        x = new SimpleObjectProperty<>();

        cut = Bindings.convert(x, value -> value != null ? value.toString() : "");
    }

    //endregion

    // region Tests

    /**
     * Providing a {@link javafx.beans.value.ObservableValue} allows for a {@link ConverterBinding} to be created.
     */
    @Test
    public void aConverterBindingCanBeCreated() {
        assertThat(cut, instanceOf(ConverterBinding.class));
        assertSame(x, getParent(cut).get());
    }

    /**
     * Providing when the {@link ConverterBinding} gets created, the value of the underlying {@link javafx.beans.value.ObservableValue} will be converted.
     */
    @Test
    public void whenAConverterBindingIsCreatedTheValueWillBeSet() {
        assertEquals("", cut.getValue());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} is changed, the binding will be invalidated and the value will be converted.
     */
    @Test
    public void whenTheObservedValueIsChangedTheBindingWillBeInvalidated() {
        x.setValue(1L);
        assertEquals("1", cut.getValue());

        x.setValue(null);
        assertEquals("", cut.getValue());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} is disposed the binding will no longer work.
     */
    @Test
    public void whenTheObservedValueIsDisposedTheBindingWillNoLongerWork() {
        x = new SimpleObjectProperty<>();

        System.gc();

        x.setValue(1L);

        assertNotSame(x, getParent(cut).get());
        assertEquals(null, cut.getValue());
    }

    // endregion
}