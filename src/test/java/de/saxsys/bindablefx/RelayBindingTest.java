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

import java.util.function.Function;

import static de.saxsys.bindablefx.TestUtil.getObservedValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * @author xyanid on 31.03.2016.
 */
@SuppressWarnings ({"OptionalGetWithoutIsPresent", "ConstantConditions"})
@RunWith (MockitoJUnitRunner.class)
public class RelayBindingTest {

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
     * Setting the {@link javafx.beans.value.ObservableValue}s will set the corresponding bindings in the nested chain.
     */
    @Test
    public void changingTheObservedValuesWillUpdateTheBindingChain() throws Throwable {

        final IFluentBinding<B> bindingB = Bindings.observe(a.bProperty());
        cut = bindingB.thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty);

        a.bProperty().setValue(new B());

        assertSame(a.bProperty().getValue(), getObservedValue(bindingB).get().getValue());
        assertNotNull(getObservedValue(cut));
        assertSame(a.bProperty().getValue().cProperty(), getObservedValue(cut).get());
        assertNull(getObservedValue(bindingD));
        assertNull(getObservedValue(bindingE));
        assertNull(getObservedValue(bindingX));

        a.bProperty().getValue().cProperty().setValue(new C());

        assertSame(a.bProperty().getValue().cProperty().getValue(), getObservedValue(cut).get().getValue());
        assertNotNull(getObservedValue(bindingD));
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty(), getObservedValue(bindingD).get());
        assertNull(getObservedValue(bindingE));
        assertNull(getObservedValue(bindingX));

        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());

        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), getObservedValue(bindingD).get().getValue());
        assertNotNull(getObservedValue(bindingE));
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), getObservedValue(bindingE).get());
        assertNull(getObservedValue(bindingX));

        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());

        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), getObservedValue(bindingE).get().getValue());
        assertNotNull(getObservedValue(bindingX));
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty(), getObservedValue(bindingX).get());

        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(1L);

        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue(), getObservedValue(bindingX).get().getValue());

        a.bProperty().setValue(null);

        assertNull(getObservedValue(cut));
        assertNull(getObservedValue(bindingD));
        assertNull(getObservedValue(bindingE));
        assertNull(getObservedValue(bindingX));
    }

    /**
     * Creating a nested chain when the {@link javafx.beans.value.ObservableValue} are already set will also initialize the bindings, so the {@link javafx.beans.value.ObservableValue} will be
     * known to each binding.
     */
    @Test
    public void aBindingChainCanBeCreatedForAlreadySetObservedValues() throws Throwable {

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(1L);

        final IFluentBinding<B> bindingB = Bindings.observe(a.bProperty());
        cut = bindingB.thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty);

        assertNotNull(getObservedValue(bindingB));
        assertSame(a.bProperty(), getObservedValue(bindingB).get());
        assertSame(a.bProperty().getValue(), getObservedValue(bindingB).get().getValue());

        assertNotNull(getObservedValue(cut));
        assertSame(a.bProperty().getValue().cProperty(), getObservedValue(cut).get());
        assertSame(a.bProperty().getValue().cProperty().getValue(), getObservedValue(cut).get().getValue());

        assertNotNull(getObservedValue(bindingD));
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty(), getObservedValue(bindingD).get());
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue(), getObservedValue(bindingD).get().getValue());

        assertNotNull(getObservedValue(bindingE));
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty(), getObservedValue(bindingE).get());
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue(), getObservedValue(bindingE).get().getValue());

        assertNotNull(getObservedValue(bindingX));
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty(), getObservedValue(bindingX).get());
        assertSame(a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().getValue(), getObservedValue(bindingX).get().getValue());
    }

    /**
     * Creating a nested chain when the {@link javafx.beans.value.ObservableValue} are already set will also initialize the bindings, so the {@link javafx.beans.value.ObservableValue} will be
     * known to each binding.
     */
    @Test
    public void aTheFallbackValueWillBeUsedWhenTheObservedValuesHaveNotYetBeenSetUp() throws Throwable {

        final E fallbackE = new E();
        final Long fallbackX = 33L;

        final IFluentBinding<B> bindingB = Bindings.observe(a.bProperty());
        cut = bindingB.thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty);

        assertNull(bindingX.getValue());

        bindingX.fallbackOn(fallbackX);

        assertSame(fallbackX, bindingX.getValue());

        bindingE.fallbackOn(fallbackE);

        assertSame(fallbackE, bindingE.getValue());
        assertNull(bindingX.getValue());

        bindingE.fallbackOn(null);

        assertNull(bindingE.getValue());
        assertSame(fallbackX, bindingX.getValue());

    }

    /**
     * A {@link javafx.beans.property.Property} can bind itself against a created binding chain and will be updated in corresponds to the binding chain. So if we bind against the last binding and
     * update any {@link javafx.beans.value.ObservableValue} in between the chain, the last binding will updated and hence the {@link javafx.beans.property.Property} will also be updated correctly.
     */
    @Test
    public void aPropertyCanBindItselfAgainstABindingChain() {

        final Function<Long, Long> replacement = value -> {
            if (value != null && value == 10L) {
                return 666L;
            } else {
                return value;
            }
        };

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(1L);

        final IFluentBinding<B> bindingB = Bindings.observe(a.bProperty());
        cut = bindingB.thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty).replaceWith(replacement);

        x.bind(bindingX);
        assertEquals(1L, x.getValue().longValue());

        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        assertEquals(null, x.getValue());

        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(20L);
        assertEquals(20L, x.getValue().longValue());

        // should replace the value here
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(10L);
        assertEquals(666L, x.getValue().longValue());

        a.bProperty().setValue(null);
        assertEquals(null, x.getValue());
    }

    /**
     * If a binding is disposed, all bindings that depend on it will no longer have observed values and be informed about changes.
     */
    @Test
    public void disposingABindingWillDisposeBreakTheChainFromThatPoint() throws Throwable {

        final Function<Long, Long> replacement = value -> {
            if (value != null && value == 10L) {
                return 666L;
            } else {
                return value;
            }
        };

        a.bProperty().setValue(new B());
        a.bProperty().getValue().cProperty().setValue(new C());
        a.bProperty().getValue().cProperty().getValue().dProperty().setValue(new D());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().setValue(new E());
        a.bProperty().getValue().cProperty().getValue().dProperty().getValue().eProperty().getValue().xProperty().setValue(1L);

        final IFluentBinding<B> bindingB = Bindings.observe(a.bProperty());
        cut = bindingB.thenObserve(B::cProperty);
        final IFluentBinding<D> bindingD = cut.thenObserve(C::dProperty);
        final IFluentBinding<E> bindingE = bindingD.thenObserve(D::eProperty);
        final IFluentBinding<Long> bindingX = bindingE.thenObserve(E::xProperty).replaceWith(replacement);

        cut.dispose();

        assertNotNull(getObservedValue(bindingB));
        assertSame(a.bProperty().getValue(), bindingB.getValue());

        assertNull(getObservedValue(cut));
        assertNull(cut.getValue());

        assertNull(getObservedValue(bindingD));
        assertNull(bindingD.getValue());

        assertNull(getObservedValue(bindingE));
        assertNull(bindingE.getValue());

        assertNull(getObservedValue(bindingX));
        assertNull(bindingX.getValue());

        a.bProperty().getValue().cProperty().setValue(new C());

        assertNotNull(getObservedValue(bindingB));
        assertSame(a.bProperty().getValue(), bindingB.getValue());

        assertNull(getObservedValue(cut));
        assertNull(cut.getValue());

        assertNull(getObservedValue(bindingD));
        assertNull(bindingD.getValue());

        assertNull(getObservedValue(bindingE));
        assertNull(bindingE.getValue());

        assertNull(getObservedValue(bindingX));
        assertNull(bindingX.getValue());
    }

    // endregion
}