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

import static de.saxsys.bindablefx.Bindings.bindRelayedCascaded;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author xyanid on 31.03.2016.
 */
@SuppressWarnings ("OptionalGetWithoutIsPresent")
@RunWith (MockitoJUnitRunner.class)
public class CascadedRelayBindingIntegrationTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private CascadedRelayBinding<B, C> cut;

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
     * A cascaded binding chain can be created even if the view model is not yet initialized.
     */
    @Test
    public void cascadedBindingChainCanBeCreatedEvenIfTheObservedPropertiesAreNotYetSet() {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        assertFalse(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty(), TestUtil.getObservedProperty(cut).get());

        assertNotNull(bindingC);
        assertFalse(bindingC.getCurrentObservedValue().isPresent());
        assertThat(bindingC, instanceOf(CascadedRelayBinding.class));

        assertNotNull(bindingD);
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertThat(bindingD, instanceOf(CascadedRelayBinding.class));

        assertNotNull(bindingE);
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertThat(bindingE, instanceOf(CascadedRelayBinding.class));

        assertNull(TestUtil.getChild((CascadedRelayBinding) bindingE));
    }

    /**
     * Initializing the observed property will set the corresponding bindings in the cascaded chain.
     */
    @Test
    public void changingTheObservedPropertiesWillSetTheBindings() throws Throwable {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        a.bProperty().set(new B());

        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingC.getCurrentObservedValue().isPresent());

        a.bProperty().get().cProperty().set(new C());

        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingD.getCurrentObservedValue().isPresent());

        a.bProperty().get().cProperty().get().dProperty().set(new D());

        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingE.getCurrentObservedValue().isPresent());

        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty().get(), bindingE.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
    }

    /**
     * Creating a cascaded chain when the observed properties are already set will also initialize the bindings, so the properties will be know to each binding.
     */
    @Test
    public void creatingACascadedChainForAlreadySetObservedPropertiesWillSetTheBindings() throws Throwable {

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        // binding for B know what to do
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for C know what to do
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D know what to do
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for E know what to do
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty().get(), bindingE.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
    }

    //endregion

    //region Changing

    /**
     * If a any top level observed property that has child observed property is changed, the child bindings will also be informed of this change.
     */
    @Test
    public void changingAObservedPropertyWillEffectAllChildBindings() throws Throwable {

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        C oldC = a.bProperty().get().cProperty().get();

        a.bProperty().get().cProperty().set(new C());

        // binding for C know what to do
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertNotEquals(oldC, bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D now knows nothing
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());

        // binding for E now knows nothing
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));

        // change D
        a.bProperty().get().cProperty().get().dProperty().set(new D());

        // binding for D now knows what to do
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get(), bindingD.getCurrentObservedValue().get());

        // change E
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // binding for D now knows what to do
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty().get(), bindingE.getCurrentObservedValue().get());
    }

    /**
     * If a an observed property is changed that is no longer part of the binding chain, then no child binding will be informed.
     */
    @Test
    public void changingAnObservedPropertyThatIsNoLongerBoundWillHaveNoEffectOnTheBindingChain() throws Throwable {

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        C oldC = a.bProperty().get().cProperty().get();

        a.bProperty().get().cProperty().set(new C());

        // binding for C know what to do
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertNotEquals(oldC, bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D now knows nothing
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());

        // binding for E now knows nothing
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));

        // set the old C with a new D
        oldC.dProperty().set(new D());

        // binding for D still knows nothing
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());

        // set the new D with a new E
        oldC.dProperty().get().eProperty().set(new E());

        // binding for E still knows nothing
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));
    }

    /**
     * A bidirectional binding can be created with the cascaded binding of any intermediate binding is changed, the property will be hooked up the the new value
     */
    @Test
    public void creatingAUnidirectionalBindingWillAllowToBindTheRelayedProperty() {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).bind(E::xProperty, x);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().set(2L);

        // binding for E is disposed
        assertThat(bindingE, instanceOf(UnidirectionalRelayBinding.class));
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());
    }

    /**
     * A reverse unidirectional binding can be created with the cascaded binding. IF any intermediate binding is changed, the property will be hooked up the the new value.
     */
    @Test
    public void creatingAReverseUnidirectionalBindingWillAllowToBindTheRelayedProperty() {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).bindReverse(E::xProperty, x);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());
        x.set(2L);

        // binding for E is disposed
        assertThat(bindingE, instanceOf(ReverseUnidirectionalRelayBinding.class));
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());
    }

    /**
     * A bidirectional binding can be created with the cascaded binding. If any intermediate binding is changed, the property will be hooked up the the new value.
     */
    @Test
    public void creatingABidirectionalBindingWillAllowToBindTheRelayedProperty() {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).bindBidirectional(E::xProperty, x);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());


        // binding for E is disposed
        assertThat(bindingE, instanceOf(BidirectionalRelayBinding.class));
        assertTrue(bindingE.getCurrentObservedValue().isPresent());

        // otherX will adjust the Es x property
        x.set(2L);
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());

        // otherX will adjust the Es x property
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().set(3L);
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());
    }

    //endregion

    //region No Strong Reference

    /**
     * Creating a cascaded binding for the relayed property will work as expected when the binding was set up without a strong reference.
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceWillAllowToBindTheRelayedPropertyWhenTheObservedPropertiesAreChanged() {

        bindRelayedCascaded(a.bProperty(), B::cProperty).attach(C::dProperty).attach(D::eProperty).bindBidirectional(E::xProperty, x);

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // otherX will adjust the Es x property
        x.set(2L);
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());

        // otherX will adjust the Es x property
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().set(3L);
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());
    }

    /**
     * Creating a cascaded binding for the relayed property will work as expected when the binding was set up without a strong reference.
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceAndGarbageCollectingTheFirstObservedPropertyWillDisposeTheEntireCascadedChain() {

        bindRelayedCascaded(a.bProperty(), B::cProperty).attach(C::dProperty).attach(D::eProperty).bindBidirectional(E::xProperty, x);

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // otherX will adjust the Es x property
        x.set(2L);
        assertEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());

        a = null;

        System.gc();

        a = new A();
        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // otherX will adjust the Es x property
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().set(3L);
        assertNotEquals(x.get(), a.bProperty().get().cProperty().get().dProperty().get().eProperty().get().xProperty().get());
    }

    //endregion

    //region Disposing

    /**
     * If a binding is disposes, all of its child bindings wil be disposed as well.
     */
    @Test
    public void disposingABindingWillDisposeAllItsChildBindings() throws Throwable {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        bindingC.dispose();

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // binding for B know what to do
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for C know what to do
        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D is disposed
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingD));

        // binding for E is disposed
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));
    }

    /**
     * When the observed property of the first {@link CascadedRelayBinding} is disposed, all the other observed properties of the child bindings will also no longer have a value.
     */
    @Test
    public void whenTheFirstObservedPropertyIsGarbageCollectedTheEntireCascadedChainWillBeDisposed() throws Throwable {

        cut = new CascadedRelayBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((CascadedRelayBinding) bindingD);

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // binding for B know what to do
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for C know what to do
        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D know what to do
        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for E know what to do
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty().get(), bindingE.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        a = null;

        System.gc();

        a = new A();
        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // TODO we still have not invoked dispose really since we did not get notified about the loose of the observed property
        // binding for B is disposed
        assertFalse(cut.getCurrentObservedValue().isPresent());
        //assertNull(TestUtil.getObservedProperty(cut));

        // binding for C is disposed
        assertFalse(bindingC.getCurrentObservedValue().isPresent());
        //assertNull(TestUtil.getObservedProperty(bindingC));

        // binding for D is disposed
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        //assertNull(TestUtil.getObservedProperty(bindingD));

        // binding for E is disposed
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        //assertNull(TestUtil.getObservedProperty(bindingE));
    }

    //endregion

    // endregion
}