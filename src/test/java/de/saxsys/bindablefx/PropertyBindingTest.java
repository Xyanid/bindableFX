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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.saxsys.bindablefx.TestUtil.getObservedValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author xyanid on 31.03.2016.
 */
@SuppressWarnings ({"OptionalGetWithoutIsPresent", "ConstantConditions"})
@RunWith (MockitoJUnitRunner.class)
public class PropertyBindingTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private IPropertyBinding<Long> cut;

    // endregion

    // region Setup

    @Before
    public void setUp() {
        a = new A();
        x = new SimpleObjectProperty<>();
    }

    // endregion

    // region Tests

    /**
     * A binding chain can be created even if only the first observed value is known but does not yet have a value.
     */
    @Test
    public void aPropertyBindingCanBeCreated() {
        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        assertNull(getObservedValue(cut));

        a.bProperty().setValue(new B());

        assertNotNull(getObservedValue(cut));
    }

    /**
     * When {@link IPropertyBinding#setValue(Object)} is called and the underlying {@link javafx.beans.value.ObservableValue} is present, the value will be used and not memorized.
     */
    @Test
    public void settingTheValueWillSetTheValueOfTheObservedValueAndTheValueWillNotBeMemorized() {

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);
        a.bProperty().setValue(new B());

        // set the value since observed value is known
        cut.setValue(10L);
        assertEquals(10L, cut.getValue().longValue());
        assertEquals(10L, a.bProperty().getValue().xProperty().getValue().longValue());

        // after new observed value is know should not apply value again
        a.bProperty().setValue(new B());
        assertNull(cut.getValue());
        assertNull(a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When {@link IPropertyBinding#setValue(Object)} is called and the underlying {@link javafx.beans.value.ObservableValue} is not yet set, then it the value will be memorized and set once the
     * {@link javafx.beans.value.ObservableValue} gets known. This will happen only once though.
     */
    @Test
    public void whenAValueIsSetAndNoObservedValueIsPresentYetTheValueWillBeMemorizedAndSetOnceTheObservedValueIsKnownButOnlyOnce() {

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);
        cut.setValue(10L);

        // should apply the memorized value here
        a.bProperty().setValue(new B());
        assertEquals(10L, cut.getValue().longValue());
        assertEquals(10L, a.bProperty().getValue().xProperty().getValue().longValue());

        // after new observed value is know should not apply value again
        a.bProperty().setValue(new B());
        assertNull(cut.getValue());
        assertNull(a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * A {@link IPropertyBinding} can be bound against an {@link javafx.beans.value.ObservableValue} and can also be unbound.
     */
    @Test
    public void aPropertyBindingCanBeBoundAgainstAnotherObservedValue() {

        a.bProperty().setValue(new B());
        x.set(1L);

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        // after initial binding we should have the same value as the observed value
        cut.bind(x);

        assertTrue(cut.isBound());
        assertEquals(1L, cut.getValue().longValue());
        assertEquals(1L, a.bProperty().getValue().xProperty().getValue().longValue());

        // changing should transmit the new value
        x.set(20L);

        assertEquals(20L, cut.getValue().longValue());
        assertEquals(20L, a.bProperty().getValue().xProperty().getValue().longValue());

        // change in the parent to null will prevent not allow for the value to be set
        a.bProperty().setValue(null);

        assertTrue(cut.isBound());
        assertNull(cut.getValue());

        // change of the parent to a non null value will immediately transmit the bound value gain
        a.bProperty().setValue(new B());

        assertEquals(20L, cut.getValue().longValue());
        assertEquals(20L, a.bProperty().getValue().xProperty().getValue().longValue());

        // unbinding will prevent further changes to be transmitted
        cut.unbind();
        x.set(10L);

        assertFalse(cut.isBound());
        assertEquals(20L, cut.getValue().longValue());
        assertEquals(20L, a.bProperty().getValue().xProperty().getValue().longValue());
    }

    /**
     * When a value has been set previously, binding will forget the previously set value so that when the binding is unbound and a new {@link javafx.beans.value.ObservableValue} is set, the
     * {@link javafx.beans.value.ObservableValue} will not be adjusted.
     */
    @Test
    public void whenAPropertyBindingGetBoundItWillForgetItsPreviouslySetValue() {

        x.set(1L);

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        cut.setValue(10L);

        cut.bind(x);

        a.bProperty().setValue(new B());

        assertEquals(1L, cut.getValue().longValue());
        assertEquals(1L, a.bProperty().getValue().xProperty().getValue().longValue());

        cut.unbind();

        a.bProperty().setValue(new B());

        assertNull(cut.getValue());
        assertNull(a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * A {@link IPropertyBinding} can be bound bidirectional against an {@link javafx.beans.property.Property} and can also be unbound bidirectional.
     */
    @Test
    public void aPropertyBindingCanBeBidirectionalBoundAgainstAnotherProperty() {

        a.bProperty().setValue(new B());
        x.set(1L);

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        // after initial binding we should have the same value as the observed value
        cut.bindBidirectional(x);

        assertFalse(cut.isBound());
        assertEquals(1L, cut.getValue().longValue());
        assertEquals(1L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(1L, x.get().longValue());

        // change the other property will transmit the value to the binding
        x.set(20L);

        assertEquals(20L, cut.getValue().longValue());
        assertEquals(20L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(20L, x.get().longValue());

        // change the binding will transmit the value to the other property
        cut.setValue(30L);

        assertEquals(30L, cut.getValue().longValue());
        assertEquals(30L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(30L, x.get().longValue());

        // setting the parent to null thus destroying the property will set the other property to null
        a.bProperty().setValue(null);

        assertNull(cut.getValue());
        assertNull(x.get());

        // setting the binding will memorize the value and thus transmit it to other property
        cut.setValue(33L);
        a.bProperty().setValue(new B());

        assertEquals(33L, cut.getValue().longValue());
        assertEquals(33L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(33L, x.get().longValue());

        // setting the parent to null thus destroying the property will set the other property to null
        a.bProperty().setValue(null);

        assertNull(cut.getValue());
        assertNull(x.get());

        // setting the other property will memorize the value as well and set it once the binding gets its property
        x.setValue(22L);
        a.bProperty().setValue(new B());

        assertEquals(22L, cut.getValue().longValue());
        assertEquals(22L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(22L, x.get().longValue());

        // unbinding will cause the relationship to end
        cut.unbindBidirectional(x);
        x.set(40L);

        assertEquals(22L, cut.getValue().longValue());
        assertEquals(22L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(40L, x.get().longValue());

        cut.setValue(44L);

        assertEquals(44L, cut.getValue().longValue());
        assertEquals(44L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(40L, x.get().longValue());
    }


    // endregion
}