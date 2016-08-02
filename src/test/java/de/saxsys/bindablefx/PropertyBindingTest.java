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
import static org.junit.Assert.assertNotNull;

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

    //region Tests

    //region Initialization

    /**
     * A binding chain can be created even if only the first observed value is known but does not yet have a value.
     */
    @Test
    public void aPropertyBindingCanBeCreated() {

        cut = Bindings.observe(a.bProperty()).thenObserveProperty(B::xProperty);

        assertNotNull(getObservedValue(cut));
    }


    /**
     * A reverse unidirectional binding can be created with the nested binding. If any intermediate binding is propertyChanged, the property will be hooked up the the new value.
     */
    @Test
    public void creatingAReverseUnidirectionalBindingWillAllowToBindTheRelayedProperty() {


    }

    //    /**
    //     * A bidirectional binding can be created with the nested binding. If any intermediate binding is propertyChanged, the property will be hooked up the the new value.
    //     */
    //    @Test
    //    public void creatingABidirectionalBindingWillAllowToBindTheRelayedProperty() {
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty).thenBindBidirectional(x);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //
    //        // binding for E is disposed
    //        assertThat(bindingE, instanceOf(BidirectionalStrategy.class));
    //        assertTrue(bindingE.getObservedValue().isPresent());
    //
    //        // otherX will adjust the Es x property
    //        x.setValue(2L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //
    //        // otherX will adjust the Es x property
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //    }
    //
    //    /**
    //     * A bidirectional binding can be created with the nested binding. If any intermediate binding is propertyChanged, the property will be hooked up the the new value.
    //     */
    //    @Test
    //    public void creatingABidirectionalFallbackBindingWillAllowToBindTheRelayedProperty() {
    //
    //        // TODO fix
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty).thenBindBidirectionalOrFallbackOn(x, Long.MAX_VALUE);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //
    //        // binding for E is disposed
    //        assertThat(bindingE, instanceOf(BidirectionalStrategy.class));
    //        assertTrue(bindingE.getObservedValue().isPresent());
    //
    //        // otherX will adjust the Es x property
    //        x.setValue(2L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //
    //        // otherX will adjust the Es x property
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //    }
    //
    //    //endregion
    //
    //    //region No Strong Reference
    //
    //    /**
    //     * Creating a nested binding for the relayed property will work as expected when the binding was set up without a strong reference.
    //     */
    //    @Test
    //    public void creatingABindingWithOutAStrongReferenceWillAllowToBindTheRelayedPropertyWhenTheObservedPropertiesAreChanged() {
    //
    //        Bindings.observe(a.bProperty(), B::cProperty).thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty).thenBindBidirectional(x);
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // otherX will adjust the Es x property
    //        x.setValue(2L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //
    //        // otherX will adjust the Es x property
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //    }
    //
    //    /**
    //     * Creating a nested binding for the relayed property will work as expected when the binding was set up without a strong reference.
    //     */
    //    @Test
    //    public void creatingABindingWithOutAStrongReferenceAndGarbageCollectingTheFirstObservedPropertyWillDisposeTheEntireNestedChain() {
    //
    //        Bindings.observe(a.bProperty(), B::cProperty).thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty).thenBindBidirectional(x);
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // otherX will adjust the Es x property
    //        x.setValue(2L);
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //
    //        a = null;
    //
    //        System.gc();
    //
    //        a = new A();
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // otherX will adjust the Es x property
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
    //        assertNotEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //    }
    //
    //    //endregion
    //
    //    //region Disposing
    //
    //    /**
    //     * If a binding is disposes, all of its child bindings wil be disposed as well.
    //     */
    //    @Test
    //    public void disposingABindingWillDisposeAllItsChildBindings() throws Throwable {
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        bindingC.dispose();
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // binding for B know what to do
    //        assertTrue(cut.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue(), cut.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for C know what to do
    //        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedValue(bindingC).get());
    //        assertTrue(bindingC.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for D is disposed
    //        assertFalse(bindingD.getObservedValue().isPresent());
    //        assertNull(TestUtil.getObservedValue(bindingD));
    //
    //        // binding for E is disposed
    //        assertFalse(bindingE.getObservedValue().isPresent());
    //        assertNull(TestUtil.getObservedValue(bindingE));
    //    }
    //
    //    /**
    //     * When the observed property of the first {@link NestedBinding} is disposed, all the other observed properties of the child bindings will also no longer have a value.
    //     */
    //    @Test
    //    public void whenTheFirstObservedPropertyIsGarbageCollectedTheEntireNestedChainWillBeDisposed() throws Throwable {
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // binding for B know what to do
    //        assertTrue(cut.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue(), cut.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for C know what to do
    //        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedValue(bindingC).get());
    //        assertTrue(bindingC.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for D know what to do
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedValue(bindingD).get());
    //        assertTrue(bindingD.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for E know what to do
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), TestUtil.getObservedValue(bindingE).get());
    //        assertTrue(bindingE.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), bindingE.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        a = null;
    //
    //        System.gc();
    //
    //        a = new A();
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // TODO we still have not invoked dispose really since we did not get notified about the loose of the observed property
    //        // binding for B is disposed
    //        assertFalse(cut.getObservedValue().isPresent());
    //        //assertNull(TestUtil.getObservedValue(cut));
    //
    //        // binding for C is disposed
    //        assertFalse(bindingC.getObservedValue().isPresent());
    //        //assertNull(TestUtil.getObservedValue(bindingC));
    //
    //        // binding for D is disposed
    //        assertFalse(bindingD.getObservedValue().isPresent());
    //        //assertNull(TestUtil.getObservedValue(bindingD));
    //
    //        // binding for E is disposed
    //        assertFalse(bindingE.getObservedValue().isPresent());
    //        //assertNull(TestUtil.getObservedValue(bindingE));
    //    }

    //endregion

    // endregion
}