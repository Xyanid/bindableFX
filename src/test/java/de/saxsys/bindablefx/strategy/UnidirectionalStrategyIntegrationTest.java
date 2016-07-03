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

package de.saxsys.bindablefx.strategy;

import de.saxsys.bindablefx.mocks.A;
import de.saxsys.bindablefx.mocks.B;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.saxsys.bindablefx.Bindings.bind;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author xyanid on 31.03.2016.
 */
@RunWith (MockitoJUnitRunner.class)
public class UnidirectionalStrategyIntegrationTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private UnidirectionalStrategy<B, Long> cut;

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
     * Creating a bidirectional binding will allow for the desired property to be observed and the binding will be informed about changes.
     */
    @Test
    public void whenTheObservedPropertyIsChangedTheBindingWillBeInformed() throws Throwable {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertFalse(cut.getCurrentObservedValue().isPresent());

        a.bProperty().setValue(new B());

        assertTrue(cut.getCurrentObservedValue().isPresent());

        a.bProperty().setValue(null);

        assertFalse(cut.getCurrentObservedValue().isPresent());
    }

    /**
     * When a Binding is creates and the observed property is already set, the binding mechanism will be invoked and the target property will be bound against the relayed property.
     */
    @Test
    public void creatingABindingWhenTheObservedPropertyIsAlreadySetWillBindTheTargetPropertyAgainstTheRelayedProperty() throws Throwable {

        a.bProperty().setValue(new B());

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertTrue(cut.getCurrentObservedValue().isPresent());

        cut.dispose();

        a.bProperty().setValue(null);

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertFalse(cut.getCurrentObservedValue().isPresent());
    }

    //endregion

    //region Changing

    /**
     * When the target property and the observed property are set before the binding is created, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyAndTheObservedPropertyAreAlreadySetTheTargetPropertyWillHaveTheSameValueAsTheRelayedProperty() {

        x.setValue(2L);
        a.bProperty().setValue(new B());

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the target property and the observed property are set before the binding is created, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyAndTheRelayedPropertyAreAlreadySetTheRelayedPropertyWillBePreferred() {

        x.setValue(2L);
        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(10L);

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertEquals(10L, x.getValue().longValue());
    }

    /**
     * When the target property is changed after the observed property is changed, an exception will be thrown because it is already bound and hence can not be changed.
     */
    @Test (expected = RuntimeException.class)
    public void changingTheTargetPropertyAfterTheObservedPropertyWillThrowAnException() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        x.setValue(2L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the target property is changed before the observed property is changed, no exception will be thrown because the target property is not yet bound.
     */
    @Test
    public void changingTheTargetPropertyBeforeTheObservedPropertyWillNotThrowAnException() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        x.setValue(2L);
        a.bProperty().setValue(new B());

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property is changed, target property gets changes as well. This only work for non reverse bindings.
     */
    @Test
    public void changingTheRelayedPropertyWillAdjustTheTargetProperty() throws Throwable {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(2L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the target property is already set and the observed property get set, the target property have the same value as the relayed property.
     */
    @Test
    public void whenTheTargetPropertyIsAlreadySetAndTheObservedPropertyChangesTheTargetPropertyWillHaveTheSameValueAsTheRelayedProperty() {

        x.setValue(2L);

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property get set and the observed property is already set, the target property have the same value as the relayed property.
     */
    @Test
    public void whenTheRelayedPropertyChangesAndTheObservedPropertyIsAlreadySetTheTargetPropertyWillHaveTheSameValueAsTheRelayedProperty() {

        a.bProperty().setValue(new B());

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().getValue().xProperty().setValue(2L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property is already set, the target property will have the same value
     */
    @Test
    public void whenTheRelayedPropertyIsAlreadySetTheTargetPropertyWillHaveTheSameValue() {

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(2L);

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property is set to null and the binding is supposed to not set the target property to null, the target property will remain its old value.
     */
    @Test
    public void whenTheBindingIsUnboundAndTheTargetPropertyShallNotBeResetTheTargetPropertyRemainItsOldValue() {

        a.bProperty().setValue(new B());

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().getValue().xProperty().setValue(2L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());

        a.bProperty().setValue(null);

        assertEquals(2L, x.getValue().longValue());
    }

    //endregion

    // region No Strong Reference

    /**
     * Creating a binding without a strong reference will have the desired effect and the binding will remain until the observed property was made invalid.
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceWillCreateTheDesiredEffect() {
        bind(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(20L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());

        a.bProperty().setValue(null);
        x.setValue(10L);
        a.bProperty().setValue(new B());

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * Creating a binding without a strong reference and garbage collecting the desired
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceAndGarbageCollectingTheTargetPropertyWillDisposeTheBindingWhenTheObservedPropertyChanges() {
        bind(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(10L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());

        x = null;

        System.gc();

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(10L);
        x = new SimpleObjectProperty<>();
        x.setValue(20L);

        assertNotEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    // endregion

    // region Disposing

    /**
     * When the binding is disposed, changes made to the observed property will no longer be listened to and the observed property will be unhooked.
     */
    @Test
    public void disposingTheBindingWillStopListeningForChangesOnTheObservedProperty() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        assertFalse(cut.getCurrentObservedValue().isPresent());

        a.bProperty().setValue(new B());

        assertTrue(cut.getCurrentObservedValue().isPresent());

        cut.dispose();

        a.bProperty().setValue(new B());

        assertFalse(cut.getCurrentObservedValue().isPresent());
    }

    /**
     * When the binding is disposed, the reference to the target property is cleared.
     */
    @Test
    public void disposingTheBindingClearTheReferenceToTheTargetProperty() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());

        assertNotNull(cut.getTarget());

        cut.dispose();

        assertNull(cut.getTarget());
    }

    /**
     * When the binding is disposed, changes made to the relayed property will not affect the target property.
     */
    @Test
    public void disposingTheBindingWillPreventTheRelayedPropertyToAffectTheTargetProperty() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(2L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());

        cut.dispose();

        a.bProperty().getValue().xProperty().setValue(10L);

        assertNotEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the target property is garbage collected, the binding will be disposed when a change event on the observed property occurs
     */
    @Test
    public void garbageCollectingTheTargetPropertyWillDisposeTheBindingWhenTheObservedPropertyChanges() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(2L);

        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertNotNull(cut.getTarget());

        x = null;

        System.gc();

        a.bProperty().setValue(new B());

        assertFalse(cut.getCurrentObservedValue().isPresent());
        assertNull(cut.getTarget());
    }

    /**
     * When the observed property is garbage collected, the binding will be disposed when the target property is changed.
     */
    @Test
    public void garbageCollectingTheObservedPropertyWillDisposeTheBinding() {

        cut = new UnidirectionalStrategy<>(a.bProperty(), B::xProperty, x);

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(2L);

        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertNotNull(cut.getTarget());

        a = null;

        System.gc();

        a = new A();
        a.bProperty().setValue(new B());

        assertNotEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
        assertFalse(cut.getCurrentObservedValue().isPresent());
        // TODO we still have not invoked dispose really since we did not get notified about the loose of the observed property
        //assertNull(TestUtil.getObservedValue(cut));
        //assertNull(cut.getTarget());
    }

    // endregion

    // endregion
}