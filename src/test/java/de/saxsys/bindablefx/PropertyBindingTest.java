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
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.ref.WeakReference;
import java.util.List;

import static de.saxsys.bindablefx.TestUtil.getBidirectionalBoundProperties;
import static de.saxsys.bindablefx.TestUtil.getObservedValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author xyanid on 31.03.2016.
 */
@SuppressWarnings ({"OptionalGetWithoutIsPresent", "ConstantConditions", "UnusedAssignment"})
@RunWith (MockitoJUnitRunner.class)
public class PropertyBindingTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private IPropertyBinding<Long> cut;

    private final IConverter<Long, String> converter = new IConverter<Long, String>() {
        @Nullable
        @Override
        public String convertTo(Long aLong) {
            if (aLong == null) {
                return null;
            }
            return aLong.toString();
        }

        @Nullable
        @Override
        public Long convertBack(String s) {
            if (s == null || s.isEmpty()) {
                return null;
            }
            return Long.parseLong(s);
        }
    };

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

        assertTrue(cut.isBidirectionalBound());
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

        assertFalse(cut.isBidirectionalBound());
        assertEquals(22L, cut.getValue().longValue());
        assertEquals(22L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(40L, x.get().longValue());

        cut.setValue(44L);

        assertEquals(44L, cut.getValue().longValue());
        assertEquals(44L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(40L, x.get().longValue());
    }

    /**
     * A {@link IPropertyBinding} can be bound bidirectional against an {@link javafx.beans.property.Property} of another type and can also be unbound bidirectional.
     */
    @Test
    public void aPropertyBindingCanBeBidirectionalBoundAgainstAnotherOfDifferentTypeProperty() {

        a.bProperty().setValue(new B());
        final Property<String> y = new SimpleObjectProperty<>("1");

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        // after initial binding we should have the same value as the observed value
        cut.bindBidirectional(y, converter);

        assertTrue(cut.isBidirectionalBound());
        assertFalse(cut.isBound());
        assertEquals(1L, cut.getValue().longValue());
        assertEquals(1L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("1", y.getValue());

        // change the other property will transmit the value to the binding
        y.setValue("20");

        assertEquals(20L, cut.getValue().longValue());
        assertEquals(20L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("20", y.getValue());

        // change the binding will transmit the value to the other property
        cut.setValue(30L);

        assertEquals(30L, cut.getValue().longValue());
        assertEquals(30L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("30", y.getValue());

        // setting the parent to null thus destroying the property will set the other property to null
        a.bProperty().setValue(null);

        assertNull(cut.getValue());
        assertNull(y.getValue());

        // setting the binding will memorize the value and thus transmit it to other property
        cut.setValue(33L);
        a.bProperty().setValue(new B());

        assertEquals(33L, cut.getValue().longValue());
        assertEquals(33L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("33", y.getValue());

        // setting the parent to null thus destroying the property will set the other property to null
        a.bProperty().setValue(null);

        assertNull(cut.getValue());
        assertNull(x.get());

        // setting the other property will memorize the value as well and set it once the binding gets its property
        y.setValue("22");
        a.bProperty().setValue(new B());

        assertEquals(22L, cut.getValue().longValue());
        assertEquals(22L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("22", y.getValue());

        // unbinding will cause the relationship to end
        cut.unbindBidirectionalConverted(y);
        y.setValue("40");

        assertFalse(cut.isBidirectionalBound());
        assertEquals(22L, cut.getValue().longValue());
        assertEquals(22L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("40", y.getValue());

        cut.setValue(44L);

        assertEquals(44L, cut.getValue().longValue());
        assertEquals(44L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals("40", y.getValue());
    }

    /**
     * A {@link IPropertyBinding} can be bound bidirectional against an multiple {@link javafx.beans.property.Property}. It is also possible to unbind the {@link IPropertyBinding} bidirectiona
     * without knowing which {@link Property}s have been bound.
     */
    @Test
    public void aPropertyBindingBeBidirectionalBoundAgainstMultiplePropertiesAndCanBeUnboundWithoutNeedingToKnowThePropertiesAgainstItWasBound() {

        a.bProperty().setValue(new B());
        final Property<String> prop1 = new SimpleObjectProperty<>("1");
        final Property<String> prop2 = new SimpleObjectProperty<>("2");
        final Property<Long> prop3 = new SimpleObjectProperty<>(3L);
        final Property<Long> prop4 = new SimpleObjectProperty<>(4L);

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        // bind first prop
        cut.bindBidirectional(prop1, converter);

        assertTrue(cut.isBidirectionalBound());
        assertEquals(1L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(1L, cut.getValue().longValue());
        assertEquals("1", prop1.getValue());
        assertEquals("2", prop2.getValue());
        assertEquals(3L, prop3.getValue().longValue());
        assertEquals(4L, prop4.getValue().longValue());

        // bind second prop
        cut.bindBidirectional(prop2, converter);

        assertTrue(cut.isBidirectionalBound());
        assertEquals(2L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(2L, cut.getValue().longValue());
        assertEquals("2", prop1.getValue());
        assertEquals("2", prop2.getValue());
        assertEquals(3L, prop3.getValue().longValue());
        assertEquals(4L, prop4.getValue().longValue());

        // bind third prop
        cut.bindBidirectional(prop3);

        assertTrue(cut.isBidirectionalBound());
        assertEquals(3L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(3L, cut.getValue().longValue());
        assertEquals("3", prop1.getValue());
        assertEquals("3", prop2.getValue());
        assertEquals(3L, prop3.getValue().longValue());
        assertEquals(4L, prop4.getValue().longValue());

        // bind third prop
        cut.bindBidirectional(prop4);

        assertTrue(cut.isBidirectionalBound());
        assertEquals(4L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(4L, cut.getValue().longValue());
        assertEquals("4", prop1.getValue());
        assertEquals("4", prop2.getValue());
        assertEquals(4L, prop3.getValue().longValue());
        assertEquals(4L, prop4.getValue().longValue());

        // set to 23
        cut.setValue(23L);
        assertEquals(23L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(23L, cut.getValue().longValue());
        assertEquals("23", prop1.getValue());
        assertEquals("23", prop2.getValue());
        assertEquals(23L, prop3.getValue().longValue());
        assertEquals(23L, prop4.getValue().longValue());

        // set to 44
        prop1.setValue("44");
        assertEquals(44L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(44L, cut.getValue().longValue());
        assertEquals("44", prop1.getValue());
        assertEquals("44", prop2.getValue());
        assertEquals(44L, prop3.getValue().longValue());
        assertEquals(44L, prop4.getValue().longValue());


        // unbind a singe prop of different type
        cut.unbindBidirectionalConverted(prop2);
        cut.setValue(55L);
        assertTrue(cut.isBidirectionalBound());
        assertEquals(55L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(55L, cut.getValue().longValue());
        assertEquals("55", prop1.getValue());
        assertEquals("44", prop2.getValue());
        assertEquals(55L, prop3.getValue().longValue());
        assertEquals(55L, prop4.getValue().longValue());

        // unbind a singe prop of different type
        cut.unbindBidirectional(prop4);
        cut.setValue(66L);
        assertTrue(cut.isBidirectionalBound());
        assertEquals(66L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(66L, cut.getValue().longValue());
        assertEquals("66", prop1.getValue());
        assertEquals("44", prop2.getValue());
        assertEquals(66L, prop3.getValue().longValue());
        assertEquals(55L, prop4.getValue().longValue());

        // bind again
        cut.bindBidirectional(prop2, converter);
        cut.bindBidirectional(prop4);
        assertEquals(55L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(55L, cut.getValue().longValue());
        assertEquals("55", prop1.getValue());
        assertEquals("55", prop2.getValue());
        assertEquals(55L, prop3.getValue().longValue());
        assertEquals(55L, prop4.getValue().longValue());

        // unbind all
        cut.unbindBidirectional();
        cut.setValue(0L);
        prop1.setValue("1");
        prop2.setValue("2");
        prop3.setValue(3L);
        prop4.setValue(4L);
        assertFalse(cut.isBidirectionalBound());
        assertEquals(0L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(0L, cut.getValue().longValue());
        assertEquals("1", prop1.getValue());
        assertEquals("2", prop2.getValue());
        assertEquals(3L, prop3.getValue().longValue());
        assertEquals(4L, prop4.getValue().longValue());
    }

    /**
     * A {@link IPropertyBinding} can be bound bidirectional against a {@link javafx.beans.property.Property}, this will not prevent the {@link Property} from being garbage collected.
     */
    @Test
    public void aBidirectionalBoundPropertyCanBeGarbageCollectedAndWillNoLongerAffectTheBinding() {

        a.bProperty().setValue(new B());
        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        Property<String> prop1 = new SimpleObjectProperty<>("1");
        Property<Long> prop2 = new SimpleObjectProperty<>(2L);
        final List<WeakReference<Property>> boundProperties = getBidirectionalBoundProperties(cut);

        // bind first prop
        cut.bindBidirectional(prop1, converter);

        assertEquals(1L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(1L, cut.getValue().longValue());
        assertEquals("1", prop1.getValue());
        assertEquals(2L, prop2.getValue().longValue());
        assertFalse(boundProperties.isEmpty());
        assertEquals(1, boundProperties.size());

        // bind second prop
        cut.bindBidirectional(prop2);

        assertEquals(2L, a.bProperty().getValue().xProperty().getValue().longValue());
        assertEquals(2L, cut.getValue().longValue());
        assertEquals("2", prop1.getValue());
        assertEquals(2L, prop2.getValue().longValue());
        assertFalse(boundProperties.isEmpty());
        assertEquals(2, boundProperties.size());

        prop1 = null;

        System.gc();

        assertFalse(boundProperties.isEmpty());
        assertEquals(2, boundProperties.size());
        assertTrue(boundProperties.stream().anyMatch(item -> item.get() == null));

        prop2 = null;

        System.gc();

        assertFalse(boundProperties.isEmpty());
        assertEquals(2, boundProperties.size());
        assertTrue(boundProperties.stream().allMatch(item -> item.get() == null));
    }

    /**
     * A {@link IPropertyBinding} can be disposed and when it was bound it will no longer be bound.
     */
    @Test
    public void disposingTheBindingWillUnbindAgainstABoundObservedValue() {

        a.bProperty().setValue(new B());
        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        cut.bind(x);

        assertTrue(cut.isBound());
        assertTrue(a.bProperty().getValue().xProperty().isBound());

        cut.dispose();

        assertFalse(cut.isBound());
        assertFalse(a.bProperty().getValue().xProperty().isBound());
    }

    /**
     * A {@link IPropertyBinding} can be disposed and when it was bound bidirectional it will no longer be bound bidirectional.
     */
    @Test
    public void disposingTheBindingWillUnbindBidirectional() {

        a.bProperty().setValue(new B());
        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);
        final List<WeakReference<Property>> boundProperties = getBidirectionalBoundProperties(cut);

        cut.bindBidirectional(x);

        assertFalse(boundProperties.isEmpty());

        cut.dispose();

        assertTrue(boundProperties.isEmpty());
    }

    // endregion
}