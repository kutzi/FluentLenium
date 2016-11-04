package org.fluentlenium.core.label;

import java.util.function.Supplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Apply label and label hints to element.
 *
 * @param <T> {@code this} class to chain method calls
 */
public class FluentLabelImpl<T> implements FluentLabel<T>, FluentLabelProvider {

    private final T reference;
    private final Supplier<String> defaultLabelSupplier;

    private String label;
    private final List<String> labelHints = new ArrayList<>();

    /**
     * Creates a new fluent label.
     *
     * @param reference            object reference to chain methods calls.
     * @param defaultLabelSupplier supplier for default label to display when no label is defined.
     */
    public FluentLabelImpl(final T reference, final Supplier<String> defaultLabelSupplier) {
        this.reference = reference;
        this.defaultLabelSupplier = defaultLabelSupplier;
    }

    @Override
    public T withLabel(final String label) {
        this.label = label;
        return reference;
    }

    /**
     * Get the defined label.
     *
     * @return defined value
     */
    public String getLabel() {
        return label;
    }

    @Override
    public T withLabelHint(final String... labelHint) {
        if (labelHint != null) {
            this.labelHints.addAll(Arrays.asList(labelHint));
        }
        return reference;
    }

    /**
     * Get the defined label hints.
     *
     * @return array of label hints
     */
    public String[] getLabelHints() {
        return labelHints.toArray(new String[labelHints.size()]);
    }

    @Override
    public String toString() {
        final StringBuilder toStringBuilder = new StringBuilder();
        if (this.label == null) {
            toStringBuilder.append(defaultLabelSupplier.get());
        } else {
            toStringBuilder.append(this.label);
        }

        if (!labelHints.isEmpty()) {
            toStringBuilder.append(" [");
            boolean notFirst = false;
            for (final String labelHint : labelHints) {
                if (notFirst) {
                    toStringBuilder.append(", ");
                }
                toStringBuilder.append(labelHint);
                notFirst = true;
            }
            toStringBuilder.append(']');
        }

        return toStringBuilder.toString();
    }
}
