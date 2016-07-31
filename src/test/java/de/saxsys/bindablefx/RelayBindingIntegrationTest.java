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
import de.saxsys.bindablefx.mocks.C;
import de.saxsys.bindablefx.mocks.D;
import de.saxsys.bindablefx.mocks.E;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.saxsys.bindablefx.TestUtil.getObservedValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author xyanid on 31.03.2016.
 */
@SuppressWarnings ("OptionalGetWithoutIsPresent")
@RunWith (MockitoJUnitRunner.class)
public class RelayBindingIntegrationTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private IFluentBinding<C> cut;

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
    public void aBindingChainCanBeCreatedEvenIfTheObservedValuesAreNotYetSet() {

        cut = Bindings.observe(a.bProperty()).thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty);

        assertNull(getObservedValue(cut));
        assertThat(cut, instanceOf(RelayBinding.class));

        assertNull(getObservedValue(bindingD));
        assertThat(bindingD, instanceOf(RelayBinding.class));

        assertNull(getObservedValue(bindingE));
        assertThat(bindingE, instanceOf(RelayBinding.class));

        assertNull(getObservedValue(bindingX));
        assertThat(bindingX, instanceOf(RelayBinding.class));
    }

    /**
     * Initializing the observed property will set the corresponding bindings in the nested chain.
     */
    @Test
    public void changingTheObservedPropertiesWillSetTheBindings() throws Throwable {

        cut = Bindings.observe(a.bProperty()).thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty);

        x.bind(cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty));

        a.bProperty().setValue(new B());

        //            assertNotNull(getObservedValue(cut));
        //            assertEquals(a.bProperty(), getObservedValue(cut));
        //            assertFalse(bindingC.getObservedValue().isPresent());
        //
        //            a.bProperty().getValue().cProperty().setValue(new C());
        //
        //            assertTrue(bindingC.getObservedValue().isPresent());
        //            assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedValue(bindingC).get());
        //            assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
        //            assertFalse(bindingD.getObservedValue().isPresent());
        //
        //            a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        //
        //            assertTrue(bindingD.getObservedValue().isPresent());
        //            assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedValue(bindingD).get());
        //            assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getObservedValue().orElseThrow(IllegalArgumentException::new));
        //            assertFalse(bindingE.getObservedValue().isPresent());
        //
        //            a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        //
        //            assertTrue(bindingE.getObservedValue().isPresent());
        //            assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), TestUtil.getObservedValue(bindingE).get());
        //            assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), bindingE.getObservedValue().orElseThrow
        // (IllegalArgumentException::new));
    }
    //
    //    /**
    //     * Creating a nested chain when the observed properties are already set will also initialize the bindings, so the properties will be know to each binding.
    //     */
    //    @Test
    //    public void creatingANestedChainForAlreadySetObservedPropertiesWillSetTheBindings() throws Throwable {
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        // binding for B know what to do
    //        assertTrue(cut.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue(), cut.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for C know what to do
    //        assertTrue(bindingC.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedValue(bindingC).get());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for D know what to do
    //        assertTrue(bindingD.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedValue(bindingD).get());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for E know what to do
    //        assertTrue(bindingE.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), TestUtil.getObservedValue(bindingE).get());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), bindingE.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //    }
    //
    //    //endregion
    //
    //    //region Changing
    //
    //    /**
    //     * If a any top level observed property that has child observed property is propertyChanged, the child bindings will also be informed of this change.
    //     */
    //    @Test
    //    public void changingAObservedPropertyWillEffectAllChildBindings() throws Throwable {
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        C oldC = a.bProperty().getValue().cProperty().getValue();
    //
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //
    //        // binding for C know what to do
    //        assertTrue(bindingC.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedValue(bindingC).get());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //        assertNotEquals(oldC, bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for D now knows nothing
    //        assertFalse(bindingD.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedValue(bindingD).get());
    //
    //        // binding for E now knows nothing
    //        assertFalse(bindingE.getObservedValue().isPresent());
    //        assertNull(TestUtil.getObservedValue(bindingE));
    //
    //        // change D
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //
    //        // binding for D now knows what to do
    //        assertTrue(bindingD.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getObservedValue().get());
    //
    //        // change E
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        // binding for D now knows what to do
    //        assertTrue(bindingE.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), bindingE.getObservedValue().get());
    //    }
    //
    //    /**
    //     * If a an observed property is propertyChanged that is no longer part of the binding chain, then no child binding will be informed.
    //     */
    //    @Test
    //    public void changingAnObservedPropertyThatIsNoLongerBoundWillHaveNoEffectOnTheBindingChain() throws Throwable {
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        C oldC = a.bProperty().getValue().cProperty().getValue();
    //
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //
    //        // binding for C know what to do
    //        assertTrue(bindingC.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedValue(bindingC).get());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //        assertNotEquals(oldC, bindingC.getObservedValue().orElseThrow(IllegalArgumentException::new));
    //
    //        // binding for D now knows nothing
    //        assertFalse(bindingD.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedValue(bindingD).get());
    //
    //        // binding for E now knows nothing
    //        assertFalse(bindingE.getObservedValue().isPresent());
    //        assertNull(TestUtil.getObservedValue(bindingE));
    //
    //        // set the old C with a new D
    //        oldC.dProperty().setValue(new D());
    //
    //        // binding for D still knows nothing
    //        assertFalse(bindingD.getObservedValue().isPresent());
    //        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedValue(bindingD).get());
    //
    //        // set the new D with a new E
    //        oldC.dProperty().getValue().eProperty().setValue(new E());
    //
    //        // binding for E still knows nothing
    //        assertFalse(bindingE.getObservedValue().isPresent());
    //        assertNull(TestUtil.getObservedValue(bindingE));
    //    }
    //
    //    /**
    //     * A bidirectional binding can be created with the nested binding of any intermediate binding is propertyChanged, the property will be hooked up the the new value
    //     */
    //    @Test
    //    public void creatingAUnidirectionalBindingWillAllowToBindTheRelayedProperty() {
    //
    //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).thenObserve(E::xProperty).thenBind(x);
    //
    //        BaseListener bindingC = TestUtil.getChild(cut);
    //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //
    //        a.bProperty().setValue(new B());
    //        a.bProperty().getValue().cProperty().setValue(new C());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(2L);
    //
    //        // binding for E is disposed
    //        assertThat(bindingE, instanceOf(UnidirectionalStrategy.class));
    //        assertTrue(bindingE.getObservedValue().isPresent());
    //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //    }
    //
    //    /**
    //     * A reverse unidirectional binding can be created with the nested binding. IF any intermediate binding is propertyChanged, the property will be hooked up the the new value.
    //     */
    //    @Test
    //    @Ignore (value = "need to be fixed")
    //    public void creatingAReverseUnidirectionalBindingWillAllowToBindTheRelayedProperty() {
    //
    //        //        cut = Bindings.observe(a.bProperty(), B::cProperty);
    //        //        cut.thenObserve(C::dProperty).thenObserve(D::eProperty).bindReverse(E::xProperty, x);
    //        //
    //        //        BaseListener bindingC = TestUtil.getChild(cut);
    //        //        BaseListener bindingD = TestUtil.getChild((NestedBinding) bindingC);
    //        //        BaseListener bindingE = TestUtil.getChild((NestedBinding) bindingD);
    //        //
    //        //        a.bProperty().setValue(new B());
    //        //        a.bProperty().getValue().cProperty().setValue(new C());
    //        //        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
    //        //        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
    //        //        x.setValue(2L);
    //        //
    //        //        // binding for E is disposed
    //        //        assertThat(bindingE, instanceOf(ReverseUnidirectionalRelayBinding.class));
    //        //        assertTrue(bindingE.getObservedValue().isPresent());
    //        //        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    //    }
    //
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