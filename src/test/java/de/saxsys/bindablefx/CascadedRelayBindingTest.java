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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
@RunWith(MockitoJUnitRunner.class)
public class CascadedRelayBindingTest {

    //region Tests

    /**
     * A cascaded binding chain can be created even if the view model is not yet initialized.
     */
    @Test
    public void cascadedBindingChainCanBeCreatedWithoutAInitializedViewModel() {

        A a = new A();

        CascadedRelayBinding<B, C> bindingB = Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty);
        bindingB.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getBinding(bindingB);
        BaseBinding bindingD = TestUtil.getBinding((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getBinding((CascadedRelayBinding) bindingD);

        assertFalse(bindingB.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty(), TestUtil.getObservedProperty(bindingB).get());

        assertNotNull(bindingC);
        assertFalse(bindingC.getCurrentObservedValue().isPresent());
        assertThat(bindingC, instanceOf(CascadedRelayBinding.class));

        assertNotNull(bindingD);
        assertFalse(bindingD.getCurrentObservedValue().isPresent());
        assertThat(bindingD, instanceOf(CascadedRelayBinding.class));

        assertNotNull(bindingE);
        assertFalse(bindingE.getCurrentObservedValue().isPresent());
        assertThat(bindingE, instanceOf(CascadedRelayBinding.class));

        assertNull(TestUtil.getBinding((CascadedRelayBinding) bindingE));
    }

    /**
     * Initializing the viewModel will set the corresponding bindings in the cascaded chain.
     */
    @Test
    public void initializingTheViewModelWillSetTheBindings() throws Throwable {

        A a = new A();

        CascadedRelayBinding<B, C> bindingB = Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty);
        bindingB.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getBinding(bindingB);
        BaseBinding bindingD = TestUtil.getBinding((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getBinding((CascadedRelayBinding) bindingD);

        a.bProperty().set(new B());

        assertTrue(bindingB.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), bindingB.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
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
     * Creating a cascaded chain for an already initialized view model will also initialize the bindings, so the properties will be know to each binding.
     */
    @Test
    public void creatingACascadedChainForAnInitializedViewModelWillSetTheBindings() throws Throwable {

        A a = new A();
        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        CascadedRelayBinding<B, C> bindingB = Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty);
        bindingB.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getBinding(bindingB);
        BaseBinding bindingD = TestUtil.getBinding((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getBinding((CascadedRelayBinding) bindingD);

        // binding for B know what to do
        assertTrue(bindingB.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), bindingB.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

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

    /**
     * If a Property is changed, the child bindings will also be informed of this change.
     */
    @Test
    public void changingAPropertyWillEffectAllChildBindings() throws Throwable {

        A a = new A();
        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        CascadedRelayBinding<B, C> bindingB = Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty);
        bindingB.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getBinding(bindingB);
        BaseBinding bindingD = TestUtil.getBinding((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getBinding((CascadedRelayBinding) bindingD);

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
     * If a an old property is changed, no child binding will react to it anymore since a new property is observed.
     */
    @Test
    public void changingAnOldPropertyWillHaveNoEffectOnTheOldValue() throws Throwable {

        A a = new A();
        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        CascadedRelayBinding<B, C> bindingB = Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty);
        bindingB.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getBinding(bindingB);
        BaseBinding bindingD = TestUtil.getBinding((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getBinding((CascadedRelayBinding) bindingD);

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
     * If a binding is disposes, all of its child bindings wil be disposed as well.
     */
    @Test
    public void disposingABindingWillDisposeAllItsChildBindings() throws Throwable {

        A a = new A();

        CascadedRelayBinding<B, C> bindingB = Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty);
        bindingB.attach(C::dProperty).attach(D::eProperty).attach(E::xProperty);

        BaseBinding bindingC = TestUtil.getBinding(bindingB);
        BaseBinding bindingD = TestUtil.getBinding((CascadedRelayBinding) bindingC);
        BaseBinding bindingE = TestUtil.getBinding((CascadedRelayBinding) bindingD);

        bindingC.dispose();

        a.bProperty().set(new B());
        a.bProperty().get().cProperty().set(new C());
        a.bProperty().get().cProperty().get().dProperty().set(new D());
        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        // binding for B know what to do
        assertTrue(bindingB.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get(), bindingB.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));

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

    // endregion
}
