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
import de.saxsys.bindablefx.strategy.BidirectionalStrategy;
import de.saxsys.bindablefx.strategy.UnidirectionalStrategy;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.saxsys.bindablefx.Bindings.attach;
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
public class NestedBindingIntegrationTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private NestedBinding<B, C> cut;

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

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        assertFalse(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty(), TestUtil.getObservedProperty(cut).get());

        assertNotNull(bindingC);
        assertFalse(bindingC.getCurrentObservedValue().isPresent());
        assertThat(bindingC, instanceOf(NestedBinding.class));

        assertNotNull(bindingD);
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertThat(bindingD, instanceOf(NestedBinding.class));

        assertNotNull(bindingE);
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertThat(bindingE, instanceOf(NestedBinding.class));

        assertNull(TestUtil.getChild((NestedBinding) bindingE));
    }

    /**
     * Initializing the observed property will set the corresponding bindings in the cascaded chain.
     */
    @Test
    public void changingTheObservedPropertiesWillSetTheBindings() throws Throwable {

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        a.bProperty().setValue(new B());

        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingC.getCurrentObservedValue().isPresent());

        a.bProperty().getValue().cProperty().setValue(new C());

        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingD.getCurrentObservedValue().isPresent());

        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());

        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingE.getCurrentObservedValue().isPresent());

        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(),
                     bindingE.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
    }

    /**
     * Creating a cascaded chain when the observed properties are already set will also initialize the bindings, so the properties will be know to each binding.
     */
    @Test
    public void creatingACascadedChainForAlreadySetObservedPropertiesWillSetTheBindings() throws Throwable {

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        // binding for B know what to do
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for C know what to do
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D know what to do
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for E know what to do
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(),
                     bindingE.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
    }

    //endregion

    //region Changing

    /**
     * If a any top level observed property that has child observed property is changed, the child bindings will also be informed of this change.
     */
    @Test
    public void changingAObservedPropertyWillEffectAllChildBindings() throws Throwable {

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        C oldC = a.bProperty().getValue().cProperty().getValue();

        a.bProperty().getValue().cProperty().setValue(new C());

        // binding for C know what to do
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertNotEquals(oldC, bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D now knows nothing
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedProperty(bindingD).get());

        // binding for E now knows nothing
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));

        // change D
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());

        // binding for D now knows what to do
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getCurrentObservedValue().get());

        // change E
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        // binding for D now knows what to do
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), bindingE.getCurrentObservedValue().get());
    }

    /**
     * If a an observed property is changed that is no longer part of the binding chain, then no child binding will be informed.
     */
    @Test
    public void changingAnObservedPropertyThatIsNoLongerBoundWillHaveNoEffectOnTheBindingChain() throws Throwable {

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        C oldC = a.bProperty().getValue().cProperty().getValue();

        a.bProperty().getValue().cProperty().setValue(new C());

        // binding for C know what to do
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertNotEquals(oldC, bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D now knows nothing
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedProperty(bindingD).get());

        // binding for E now knows nothing
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));

        // set the old C with a new D
        oldC.dProperty().setValue(new D());

        // binding for D still knows nothing
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedProperty(bindingD).get());

        // set the new D with a new E
        oldC.dProperty().getValue().eProperty().setValue(new E());

        // binding for E still knows nothing
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));
    }

    /**
     * A bidirectional binding can be created with the cascaded binding of any intermediate binding is changed, the property will be hooked up the the new value
     */
    @Test
    public void creatingAUnidirectionalBindingWillAllowToBindTheRelayedProperty() {

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).bind(E::xProperty, x);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(2L);

        // binding for E is disposed
        assertThat(bindingE, instanceOf(UnidirectionalStrategy.class));
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    }

    /**
     * A reverse unidirectional binding can be created with the cascaded binding. IF any intermediate binding is changed, the property will be hooked up the the new value.
     */
    @Test
    public void creatingAReverseUnidirectionalBindingWillAllowToBindTheRelayedProperty() {

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).bindReverse(E::xProperty, x);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        x.setValue(2L);

        // binding for E is disposed
        assertThat(bindingE, instanceOf(ReverseUnidirectionalRelayBinding.class));
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    }

    /**
     * A bidirectional binding can be created with the cascaded binding. If any intermediate binding is changed, the property will be hooked up the the new value.
     */
    @Test
    public void creatingABidirectionalBindingWillAllowToBindTheRelayedProperty() {

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).bindBidirectional(E::xProperty, x);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());


        // binding for E is disposed
        assertThat(bindingE, instanceOf(BidirectionalStrategy.class));
        assertTrue(bindingE.getCurrentObservedValue().isPresent());

        // otherX will adjust the Es x property
        x.setValue(2L);
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());

        // otherX will adjust the Es x property
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    }

    //endregion

    //region No Strong Reference

    /**
     * Creating a cascaded binding for the relayed property will work as expected when the binding was set up without a strong reference.
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceWillAllowToBindTheRelayedPropertyWhenTheObservedPropertiesAreChanged() {

        attach(a.bProperty(), B::cProperty).attach(C::dProperty).attach(D::eProperty).bindBidirectional(E::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        // otherX will adjust the Es x property
        x.setValue(2L);
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());

        // otherX will adjust the Es x property
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    }

    /**
     * Creating a cascaded binding for the relayed property will work as expected when the binding was set up without a strong reference.
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceAndGarbageCollectingTheFirstObservedPropertyWillDisposeTheEntireCascadedChain() {

        attach(a.bProperty(), B::cProperty).attach(C::dProperty).attach(D::eProperty).bindBidirectional(E::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        // otherX will adjust the Es x property
        x.setValue(2L);
        assertEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());

        a = null;

        System.gc();

        a = new A();
        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        // otherX will adjust the Es x property
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(3L);
        assertNotEquals(x.getValue(), a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue());
    }

    //endregion

    //region Disposing

    /**
     * If a binding is disposes, all of its child bindings wil be disposed as well.
     */
    @Test
    public void disposingABindingWillDisposeAllItsChildBindings() throws Throwable {

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        bindingC.dispose();

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        // binding for B know what to do
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for C know what to do
        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D is disposed
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingD));

        // binding for E is disposed
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertNull(TestUtil.getObservedProperty(bindingE));
    }

    /**
     * When the observed property of the first {@link NestedBinding} is disposed, all the other observed properties of the child bindings will also no longer have a value.
     */
    @Test
    public void whenTheFirstObservedPropertyIsGarbageCollectedTheEntireCascadedChainWillBeDisposed() throws Throwable {

        cut = new NestedBinding<>(a.bProperty(), B::cProperty);
        cut.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getChild(cut);
        BaseBinding bindingD = TestUtil.getChild((NestedBinding) bindingC);
        BaseBinding bindingE = TestUtil.getChild((NestedBinding) bindingD);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        // binding for B know what to do
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue(), cut.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for C know what to do
        assertEquals(a.bProperty().getValue().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for D know what to do
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        // binding for E know what to do
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(),
                     bindingE.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

        a = null;

        System.gc();

        a = new A();
        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

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

    // region new Stuff

    /**
     * These are new ideas for the library
     */
    @Test
    public void newStuff() {
        waitFor(a.bProperty()).waitFor(B::cProperty).waitFor(C::dProperty).waitFor(D::xProperty).consume(x);

        waitFor(a.bProperty()).waitFor(B::cProperty).waitFor(C::dProperty).waitFor(D::xProperty).bind(x);

        waitFor(a.bProperty()).waitFor(B::cProperty).waitFor(C::dProperty).waitFor(D::xProperty).bind(x, null);

        waitFor(a.bProperty()).waitFor(B::cProperty).waitFor(C::dProperty).waitFor(D::xProperty).bindReverse(x);

        waitFor(a.bProperty()).waitFor(B::cProperty).waitFor(C::dProperty).waitFor(D::xProperty).bindBidirectional(x);

        waitFor(a.bProperty()).waitFor(B::cProperty).waitFor(C::dProperty).waitFor(D::xProperty).bindBidirectional(x, null);
    }

    // endregion

    // endregion
}