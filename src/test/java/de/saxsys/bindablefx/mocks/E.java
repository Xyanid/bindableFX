package de.saxsys.bindablefx.mocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author x1rhents on 31.03.2016.
 */
public class E {

    /**
     * Determines the f
     */
    private final ObjectProperty<Long> f = new SimpleObjectProperty<>();

    /**
     * Sets the value of the {@link #f}.
     *
     * @param value the value to use.
     */
    public final void setF(final Long value) {
        f.set(value);
    }

    /**
     * Gets the value of the {@link #f}.
     *
     * @return the value of the {@link #f}.
     */
    public final Long getF() {
        return f.getValue();
    }

    /**
     * Gets the property {@link #f}.
     *
     * @return the {@link #f} property.
     */
    public final ObjectProperty<Long> fProperty() {
        return f;
    }
}
