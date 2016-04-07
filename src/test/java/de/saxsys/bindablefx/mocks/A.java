package de.saxsys.bindablefx.mocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author x1rhents on 31.03.2016.
 */
public class A {

    /**
     * Determines the b
     */
    private final ObjectProperty<B> b = new SimpleObjectProperty<>();

    /**
     * Sets the value of the {@link #b}.
     * @param value the value to use.
     */
    public final void setB(final B value){
            b.set(value);
    }

    /**
     * Gets the value of the {@link #b}.
     * @return the value of the {@link #b}.
     */
    public final B getB() {
        return b.getValue();
    }

    /**
     * Gets the property {@link #b}.
     * @return the {@link #b} property.
     */
    public final ObjectProperty<B> bProperty() {
        return b;
    }
}
