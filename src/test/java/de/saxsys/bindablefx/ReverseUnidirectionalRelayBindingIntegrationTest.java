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

import static de.saxsys.bindablefx.Bindings.bindReversedRelayed;
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
public class ReverseUnidirectionalRelayBindingIntegrationTest {

    // region Fields

    private A a;

    private ObjectProperty<Long> x;

    private ReverseUnidirectionalRelayBinding<B, Long> cut;

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

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        assertFalse(cut.getCurrentObservedValue().isPresent());

        a.bProperty().set(new B());

        assertTrue(cut.getCurrentObservedValue().isPresent());

        a.bProperty().set(null);

        assertFalse(cut.getCurrentObservedValue().isPresent());
    }

    /**
     * When a Binding is creates and the observed property is already set, the binding mechanism will be invoked and the target property will be bound against the relayed property.
     */
    @Test
    public void creatingABindingWhenTheObservedPropertyIsAlreadySetWillBindTheTargetPropertyAgainstTheRelayedProperty() throws Throwable {

        a.bProperty().set(new B());

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        assertTrue(cut.getCurrentObservedValue().isPresent());

        cut.dispose();

        a.bProperty().set(null);

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        assertFalse(cut.getCurrentObservedValue().isPresent());
    }

    //endregion

    // region Changing Reverse Binding

    /**
     * When the target property and the observed property are set before the binding is created, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyAndTheObservedPropertyAreAlreadySetTheRelayedPropertyWillHaveTheSameValue() {

        x.set(2L);
        a.bProperty().set(new B());

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    /**
     * When the target property and the observed property are set before the binding is created, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyAndTheRelayedPropertyAreAlreadySetTheTargetPropertyWillBePreferred() {

        x.set(2L);
        a.bProperty().set(new B());
        a.bProperty().get().xProperty().set(10L);

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        assertEquals(2L, a.bProperty().get().xProperty().get().longValue());
    }

    /**
     * When the relayed property is changed, target property gets changes as well. This only work for non reverse bindings.
     */
    @Test (expected = RuntimeException.class)
    public void changingTheRelayedPropertyWillCauseAnException() throws Throwable {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        a.bProperty().get().xProperty().set(2L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    /**
     * When the relayed property is changed, target property gets changes as well. This only work for non reverse bindings.
     */
    @Test
    public void changingTheTargetPropertyWillAdjustTheRelayedProperty() throws Throwable {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        x.set(20L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    /**
     * When the target property is already set and the observed property get set, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyIsAlreadySetAndTheObservedPropertyChangesTheRelayedPropertyWillHaveTheSameValueAsTheTargetProperty() {

        x.set(2L);

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    /**
     * When the relayed property is already set and the target property get set, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheRelayedPropertyIsAlreadySetAndTheTargetPropertyChangesTheRelayedPropertyWillHaveTheSameValueAsTheTargetProperty() {

        a.bProperty().set(new B());
        a.bProperty().get().xProperty().set(10L);

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        x.set(2L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    // endregion

    // region No Strong Reference

    /**
     * Creating a binding without a strong reference will have the desired effect and the binding will remain until the observed property was made invalid.
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceWillCreateTheDesiredEffect() {
        bindReversedRelayed(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        x.set(20L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());

        a.bProperty().set(null);
        x.set(10L);
        a.bProperty().set(new B());

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    /**
     * Creating a binding without a strong reference and garbage collecting the desired
     */
    @Test
    public void creatingABindingWithOutAStrongReferenceAndGarbageCollectingTheTargetPropertyWillDisposeTheBindingWhenTheObservedPropertyChanges() {
        bindReversedRelayed(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        x.set(20L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());

        a.bProperty().set(null);
        x = null;

        System.gc();

        a.bProperty().set(new B());
        x = new SimpleObjectProperty<>();
        x.set(20L);

        assertNotEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    // endregion

    // region Disposing

    /**
     * When the binding is disposed, changes made to the observed property will no longer be listened to and the observed property will be unhooked.
     */
    @Test
    public void disposingTheBindingWillStopListeningForChangesOnTheObservedProperty() {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        assertFalse(cut.getCurrentObservedValue().isPresent());

        a.bProperty().set(new B());

        assertTrue(cut.getCurrentObservedValue().isPresent());

        cut.dispose();

        a.bProperty().set(new B());

        assertFalse(cut.getCurrentObservedValue().isPresent());
    }

    /**
     * When the binding is disposed, the reference to the target property is cleared.
     */
    @Test
    public void disposingTheBindingClearTheReferenceToTheTargetProperty() {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());

        assertNotNull(cut.getTargetPropertyProperty());

        cut.dispose();

        assertNull(cut.getTargetPropertyProperty());
    }

    /**
     * When the binding is disposed, changes made to the target property will not affect the relayed property.
     */
    @Test
    public void disposingTheBindingWillPreventTheTargetPropertyToAffectTheRelayedProperty() {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        x.set(2L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());

        cut.dispose();

        x.set(10L);

        assertNotEquals(x.get(), a.bProperty().get().xProperty().get());
    }

    /**
     * When the target property is garbage collected, the binding will be disposed when a change event on the observed property occurs
     */
    @Test
    public void garbageCollectingTheTargetPropertyWillDisposeTheBindingWhenTheObservedPropertyChanges() {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        x.set(2L);

        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertNotNull(cut.getTargetPropertyProperty());

        a.bProperty().set(null);
        x = null;

        System.gc();

        a.bProperty().set(new B());

        assertFalse(cut.getCurrentObservedValue().isPresent());
        assertNull(cut.getTargetPropertyProperty());
    }

    /**
     * When the observed property is garbage collected, the binding will be disposed when the target property is changed.
     */
    @Test
    public void garbageCollectingTheObservedPropertyWillDisposeTheBinding() {

        cut = new ReverseUnidirectionalRelayBinding<>(a.bProperty(), B::xProperty, x);

        a.bProperty().set(new B());
        x.set(2L);

        assertEquals(x.get(), a.bProperty().get().xProperty().get());
        assertTrue(cut.getCurrentObservedValue().isPresent());
        assertNotNull(cut.getTargetPropertyProperty());

        a = null;

        System.gc();

        a = new A();
        a.bProperty().set(new B());

        assertNotEquals(x.get(), a.bProperty().get().xProperty().get());
        assertFalse(cut.getCurrentObservedValue().isPresent());
        // TODO we still have not invoked dispose really since we did not get notified about the loose of the observed property
        //assertNull(TestUtil.getObservedProperty(cut));
        //assertNull(cut.getTargetPropertyProperty());
    }

    // endregion

    // endregion
}