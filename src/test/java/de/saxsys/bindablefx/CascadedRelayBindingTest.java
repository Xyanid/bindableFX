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

        assertEquals(a.bProperty().get().cProperty(), TestUtil.getObservedProperty(bindingC).get());
        assertTrue(bindingC.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get(), bindingC.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingD.getCurrentObservedValue().isPresent());

        a.bProperty().get().cProperty().get().dProperty().set(new D());

        assertEquals(a.bProperty().get().cProperty().get().dProperty(), TestUtil.getObservedProperty(bindingD).get());
        assertTrue(bindingD.getCurrentObservedValue().isPresent());
        assertEquals(a.bProperty().get().cProperty().get().dProperty().get(), bindingD.getCurrentObservedValue().orElseThrow(IllegalArgumentException::new));
        assertFalse(bindingE.getCurrentObservedValue().isPresent());

        a.bProperty().get().cProperty().get().dProperty().get().eProperty().set(new E());

        assertEquals(a.bProperty().get().cProperty().get().dProperty().get().eProperty(), TestUtil.getObservedProperty(bindingE).get());
        assertTrue(bindingE.getCurrentObservedValue().isPresent());
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
    }

    // endregion
}
