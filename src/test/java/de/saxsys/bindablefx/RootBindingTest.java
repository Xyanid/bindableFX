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

import de.saxsys.bindablefx.mocks.A;
import de.saxsys.bindablefx.mocks.B;
import javafx.beans.property.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.saxsys.bindablefx.TestUtil.getObservedValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Xyanid on 27.07.2016.
 */
@RunWith (MockitoJUnitRunner.class)
public class RootBindingTest {

    //region Fields

    private A a;

    private IFluentBinding<B> cut;

    //endregion

    //region Setup

    @Before
    public void setUp() {

        a = new A();

        cut = Bindings.observe(a.bProperty());
    }

    //endregion

    // region Tests

    /**
     * Providing a {@link javafx.beans.value.ObservableValue} allows for a {@link RootBinding} to be created.
     */
    @Test
    public void aRootBindingCanBeCreated() {
        cut = Bindings.observe(a.bProperty());

        assertThat(cut, instanceOf(RootBinding.class));
        assertNotNull(getObservedValue(cut));
        assertEquals(a.bProperty(), getObservedValue(cut).get());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} is changed, the binding will be invalidated as well and have the same value as the observed one
     */
    @Test
    public void whenTheObservedValueIsChangedTheBindingWillBeInvalidated() {
        cut = Bindings.observe(a.bProperty());

        a.bProperty().setValue(new B());

        assertEquals(a.bProperty().getValue(), cut.getValue());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} of the binding is not set, the binding will returns the fallback value.
     */
    @Test
    public void whenTheObservedValueIsNotSetTheFallbackValueWillBeUsedInstead() {
        final B fallbackValue = new B();

        cut = new RootBinding<B>().fallbackOn(fallbackValue);

        assertEquals(fallbackValue, cut.getValue());
    }

    /**
     * When the binding is changed, the listener attached will be invoked and stopping will remove the listener.
     */
    @SuppressWarnings ("unchecked")
    @Test
    public void whenTheObservedValueIsChangedTheAddedListenerWillBeInvokedAndCanBeRemoved() {

        final Property mock = mock(Property.class);

        cut = Bindings.observe(a.bProperty()).waitForChange((observable, oldValue, newValue) -> {
            assertEquals(cut, observable);
            mock.setValue(null);
        });

        a.bProperty().setValue(new B());

        verify(mock, times(1)).setValue(any());

        cut.stopWaitingForChange();

        a.bProperty().setValue(new B());

        verify(mock, times(1)).setValue(any());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} is disposed the binding will no longer work.
     */
    @Test
    public void whenTheObservedValueIsDisposedTheBindingWillNoLongerWork() {
        cut = Bindings.observe(a.bProperty());

        a = new A();

        System.gc();

        assertNull(getObservedValue(cut).get());
    }

    // endregion
}