package de.saxsys.bindablefx.mocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author x1rhents on 31.03.2016.
 */
public class C {

    /**
     * Determines the x
     */
    private final ObjectProperty<D> d = new SimpleObjectProperty<>();

    /**
     * Determines the x
     */
    private final ObjectProperty<Long> x = new SimpleObjectProperty<>();

    /**
     * Sets the value of the {@link #x}.
     *
     * @param value the value to use.
     */
    public final void setD(final D value) {
        d.set(value);
    }

    /**
     * Gets the value of the {@link #x}.
     *
     * @return the value of the {@link #x}.
     */
    public final D getD() {
        return d.getValue();
    }

    /**
     * Sets the value of the {@link #x}.
     *
     * @param value the value to use.
     */
    public final void setX(final Long value) {
        x.set(value);
    }

    /**
     * Gets the value of the {@link #x}.
     *
     * @return the value of the {@link #x}.
     */
    public final Long getX() {
        return x.getValue();
    }

    /**
     * Gets the property {@link #x}.
     *
     * @return the {@link #x} property.
     */
    public final ObjectProperty<D> dProperty() {
        return d;
    }

    /**
     * Gets the property {@link #x}.
     *
     * @return the {@link #x} property.
     */
    public final ObjectProperty<Long> xProperty() {
        return x;
    }
}
