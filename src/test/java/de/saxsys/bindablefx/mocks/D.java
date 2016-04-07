package de.saxsys.bindablefx.mocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author x1rhents on 31.03.2016.
 */
public class D {

    /**
     * Determines the e
     */
    private final ObjectProperty<E> e = new SimpleObjectProperty<>();

    /**
     * Sets the value of the {@link #e}.
     *
     * @param value the value to use.
     */
    public final void setE(final E value) {
        e.set(value);
    }

    /**
     * Gets the value of the {@link #e}.
     *
     * @return the value of the {@link #e}.
     */
    public final E getE() {
        return e.getValue();
    }

    /**
     * Gets the property {@link #e}.
     *
     * @return the {@link #e} property.
     */
    public final ObjectProperty<E> eProperty() {
        return e;
    }
}
