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
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.saxsys.bindablefx.TestUtil.getObservedValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Xyanid on 27.07.2016.
 */
@SuppressWarnings ("unchecked")
@RunWith (MockitoJUnitRunner.class)
public class RootBindingTest {

    //region Fields

    private A a;

    private IFluentBinding<B> cut;

    //endregion

    //region Setup

    @Before
    public void setUp() {

        a = new A();

        cut = Bindings.observe(a.bProperty());
    }

    //endregion

    // region Tests

    /**
     * Providing a {@link javafx.beans.value.ObservableValue} allows for a {@link RootBinding} to be created.
     */
    @Test
    public void aRootBindingCanBeCreated() {
        cut = Bindings.observe(a.bProperty());

        assertThat(cut, instanceOf(RootBinding.class));
        assertNotNull(getObservedValue(cut));
        assertSame(a.bProperty(), getObservedValue(cut).get());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} is changed, the binding will be invalidated as well and have the same value as the observed one
     */
    @Test
    public void whenTheObservedValueIsChangedTheBindingWillBeInvalidated() {
        cut = Bindings.observe(a.bProperty());

        a.bProperty().setValue(new B());

        assertSame(a.bProperty().getValue(), cut.getValue());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} of the binding is not set, the binding will returns the fallback value. It is possible to check if the binding has a fallback value and it
     * can also be removed.
     */
    @Test
    public void aFallbackValueCanBeSetAndRemovedAndWillBeUsedIfTheObservedValueIsNotYetSet() {
        final B fallbackValue = new B();

        cut = new RootBinding<B>().fallbackOn(fallbackValue);

        assertTrue(cut.hasFallbackValue());
        assertSame(fallbackValue, cut.getValue());

        cut.stopFallbackOn();

        assertFalse(cut.hasFallbackValue());
        assertNull(cut.getValue());
    }

    /**
     * {@link javafx.beans.value.ChangeListener} or {@link javafx.beans.InvalidationListener} can be added and removed and will be invoked once the underlying
     * {@link javafx.beans.value.ObservableValue} has changed. It is also possible to determine if listeners are currently added and all Listeners can be removed at once.
     */
    @Test
    public void listenersCanBeAddedAndRemovedAndWillBeInvokedWhenTheBindingChanges() {

        cut = Bindings.observe(a.bProperty());

        final Property changeMock = mock(Property.class);
        final ChangeListener<B> changeListener = (observable, oldValue, newValue) -> {
            assertSame(cut, observable);
            changeMock.setValue(null);
        };

        final Property invalidationMock = mock(Property.class);
        final InvalidationListener invalidationListener = (observable) -> {
            assertSame(cut, observable);
            invalidationMock.setValue(null);
        };

        cut.addListener(changeListener);
        cut.addListener(invalidationListener);

        assertTrue(cut.hasListeners());
        a.bProperty().setValue(new B());
        verify(changeMock, times(1)).setValue(any());
        verify(invalidationMock, times(1)).setValue(any());

        cut.stopListeners();

        assertFalse(cut.hasListeners());
        a.bProperty().setValue(new B());
        verify(changeMock, times(1)).setValue(any());
        verify(invalidationMock, times(1)).setValue(any());

        cut.addListener(changeListener);
        assertTrue(cut.hasListeners());

        cut.removeListener(changeListener);
        assertFalse(cut.hasListeners());

        cut.addListener(invalidationListener);
        assertTrue(cut.hasListeners());

        cut.removeListener(invalidationListener);
        assertFalse(cut.hasListeners());
    }

    /**
     * A replacement can be added and removed and will be used instead of the valeu of the {@link javafx.beans.value.ObservableValue}. It is also possible to determine if a replacement is currently
     * added.
     */
    @Test
    public void aReplacementCanBeAddedAndRemovedAndWillBeUsedInsteadOfTheValueOfTheObservedValue() {

        final B substituteValue = new B();
        final Function<B, B> replacement = (value) -> {
            if (value == null) {
                return substituteValue;
            }
            return value;
        };

        cut = Bindings.observe(a.bProperty()).replaceWith(replacement);
        assertTrue(cut.hasReplacement());
        assertSame(substituteValue, cut.getValue());

        a.bProperty().setValue(new B());
        assertSame(a.bProperty().getValue(), cut.getValue());

        a.bProperty().setValue(null);
        assertSame(substituteValue, cut.getValue());

        // stop replacing and use the current value of the observed value
        cut.stopReplacement();
        assertNull(cut.getValue());
        assertFalse(cut.hasReplacement());

        // use predicate to replace
        final B fallbackValue = new B();
        final B fallbackTriggerValue = new B();
        final Predicate<B> fallbackPredicate = value -> Objects.equals(value, fallbackTriggerValue);

        cut.replaceWith(fallbackPredicate, fallbackValue);

        a.bProperty().setValue(new B());
        assertSame(a.bProperty().getValue(), cut.getValue());

        a.bProperty().setValue(fallbackTriggerValue);
        assertSame(fallbackValue, cut.getValue());

        a.bProperty().setValue(new B());
        assertSame(a.bProperty().getValue(), cut.getValue());

        // stop replacing and use the current value of the observed value
        cut.stopReplacement();
        a.bProperty().setValue(null);
        assertNull(cut.getValue());
        assertFalse(cut.hasReplacement());
    }

    /**
     * When the {@link javafx.beans.value.ObservableValue} is disposed the binding will no longer work.
     */
    @Test
    public void whenTheObservedValueIsDisposedTheBindingWillNoLongerWork() {
        cut = Bindings.observe(a.bProperty());

        a = new A();

        System.gc();

        assertNull(getObservedValue(cut).get());

        assertTrue(cut.wasGarbageCollected());
    }

    /**
     * When the binding is disposed, all {@link ChangeListener}, {@link InvalidationListener}, replacement or fallback value will be removed.
     */
    @Test
    public void disposingTheBindingWillRemoveAllListenersReplacementsAndFallbackValues() {

        final B fallbackValue = new B();

        final Property changeMock = mock(Property.class);
        final ChangeListener<B> changeListener = (observable, oldValue, newValue) -> {
            assertSame(cut, observable);
            changeMock.setValue(null);
        };

        final Property invalidationMock = mock(Property.class);
        final InvalidationListener invalidationListener = (observable) -> {
            assertSame(cut, observable);
            invalidationMock.setValue(null);
        };

        final B substituteValue = new B();
        final Function<B, B> replacement = (value) -> {
            if (value == null) {
                return substituteValue;
            }
            return value;
        };

        a.bProperty().setValue(new B());
        cut = Bindings.observe(a.bProperty()).fallbackOn(fallbackValue).replaceWith(replacement);
        cut.addListener(changeListener);
        cut.addListener(invalidationListener);

        assertSame(a.bProperty().getValue(), cut.getValue());
        assertTrue(cut.hasListeners());
        assertTrue(cut.hasReplacement());
        assertTrue(cut.hasFallbackValue());

        cut.dispose();
        a.bProperty().setValue(new B());

        assertNotSame(a.bProperty().getValue(), cut.getValue());
        assertFalse(cut.hasListeners());
        assertFalse(cut.hasReplacement());
        assertFalse(cut.hasFallbackValue());
    }

    // endregion
}