package de.saxsys.bindablefx.mocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author x1rhents on 31.03.2016.
 */
public class B {

    /**
     * Determines the x
     */
    private final ObjectProperty<Long> y = new SimpleObjectProperty<>();

    /**
     * Determines the c
     */
    private final ObjectProperty<C> c = new SimpleObjectProperty<>();

    /**
     * Sets the value of the {@link #x}.
     *
     * @param value the value to use.
     */
    public final void setY(final Long value) {
        y.set(value);
    }

    /**
     * Gets the value of the {@link #x}.
     *
     * @return the value of the {@link #x}.
     */
    public final Long getY() {
        return y.getValue();
    }

    /**
     * Sets the value of the {@link #c}.
     *
     * @param value the value to use.
     */
    public final void setC(final C value) {
        c.set(value);
    }

    /**
     * Gets the value of the {@link #c}.
     *
     * @return the value of the {@link #c}.
     */
    public final C getC() {
        return c.getValue();
    }

    /**
     * Gets the property {@link #x}.
     *
     * @return the {@link #x} property.
     */
    public final ObjectProperty<Long> yProperty() {
        return y;
    }

    /**
     * Gets the property {@link #c}.
     *
     * @return the {@link #c} property.
     */
    public final ObjectProperty<C> cProperty() {
        return c;
    }
}
