package org.fluentlenium.core.wait;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.conditions.FluentConditions;
import org.fluentlenium.core.conditions.FluentListConditions;
import org.fluentlenium.core.conditions.wait.WaitConditionProxy;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.utils.SupplierOfInstance;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A wait object wrapping default selenium {@link org.openqa.selenium.support.ui.FluentWait} object into a more
 * complete API, allowing to wait for any condition to be verified.
 */
public class FluentWait
        implements FluentWaitFunctional<FluentControl>, FluentWaitConditions<FluentWait>, FluentWaitConfiguration<FluentWait> {

    private final org.openqa.selenium.support.ui.FluentWait<FluentControl> wait;
    private final WebDriver driver;
    private boolean useDefaultException;
    private boolean messageDefined;
    private boolean defaultExceptionsRegistered;

    /**
     * Creates a new fluent wait.
     *
     * @param control control interface
     */
    public FluentWait(final FluentControl control) {
        wait = new org.openqa.selenium.support.ui.FluentWait<>(control);
        wait.withTimeout(5, TimeUnit.SECONDS);
        driver = control.getDriver();
        useDefaultException = true;
    }

    @Override
    public org.openqa.selenium.support.ui.FluentWait getWait() {
        return wait;
    }

    @Override
    public FluentWait atMost(final long duration, final TimeUnit unit) {
        wait.withTimeout(duration, unit);
        return this;
    }

    @Override
    public FluentWait atMost(final long duration) {
        return atMost(duration, TimeUnit.MILLISECONDS);
    }

    @Override
    public FluentWait pollingEvery(final long duration, final TimeUnit unit) {
        wait.pollingEvery(duration, unit);
        return this;
    }

    @Override
    public FluentWait pollingEvery(final long duration) {
        return pollingEvery(duration, TimeUnit.MILLISECONDS);
    }

    @Override
    public FluentWait ignoreAll(final java.util.Collection<java.lang.Class<? extends Throwable>> types) {
        wait.ignoreAll(types);
        return this;
    }

    @Override
    public FluentWait ignoring(final java.lang.Class<? extends java.lang.RuntimeException> exceptionType) {
        wait.ignoring(exceptionType);
        return this;
    }

    @Override
    public FluentWait ignoring(final java.lang.Class<? extends java.lang.RuntimeException> firstType,
            final java.lang.Class<? extends java.lang.RuntimeException> secondType) {
        wait.ignoring(firstType, secondType);
        return this;
    }

    @Override
    public FluentWait withMessage(final String message) {
        wait.withMessage(message);
        messageDefined = true;
        return this;
    }

    @Override
    public FluentWait withMessage(final Supplier<String> message) {
        wait.withMessage(message);
        messageDefined = true;
        return this;
    }

    @Override
    public FluentWait withNoDefaultsException() {
        useDefaultException = false;
        return this;
    }

    private void updateWaitWithDefaultExceptions() {
        if (useDefaultException & !defaultExceptionsRegistered) {
            defaultExceptionsRegistered = true;
            wait.ignoring(NoSuchElementException.class);
            wait.ignoring(StaleElementReferenceException.class);
        }
    }

    /**
     * Check if a message is defined.
     *
     * @return true if this fluent wait use a custom message, false otherwise
     */
    public boolean hasMessageDefined() {
        return messageDefined;
    }

    @Override
    public void untilPredicate(final Predicate<FluentControl> predicate) {
        updateWaitWithDefaultExceptions();
        wait.until(predicate);
    }

    @Override
    public void until(final Supplier<Boolean> booleanSupplier) {
        updateWaitWithDefaultExceptions();
        wait.until(new Function<Object, Boolean>() {
            public Boolean apply(final Object input) {
                return booleanSupplier.get();
            }

            public String toString() {
                return booleanSupplier.toString();
            }
        });
    }

    @Override
    public <T> T until(final Function<? super FluentControl, T> function) {
        updateWaitWithDefaultExceptions();
        return wait.until(function);
    }

    @Override
    public FluentConditions until(final FluentWebElement element) {
        updateWaitWithDefaultExceptions();
        return WaitConditionProxy.element(this, "Element " + element.toString(), new SupplierOfInstance<>(element));
    }

    @Override
    public FluentListConditions until(final List<? extends FluentWebElement> elements) {
        updateWaitWithDefaultExceptions();
        return WaitConditionProxy
                .one(this, "Elements " + elements.toString(), Suppliers.<List<? extends FluentWebElement>>ofInstance(elements));
    }

    @Override
    public FluentListConditions untilEach(final List<? extends FluentWebElement> elements) {
        updateWaitWithDefaultExceptions();
        return WaitConditionProxy
                .each(this, "Elements " + elements.toString(), Suppliers.<List<? extends FluentWebElement>>ofInstance(elements));
    }

    @Override
    public FluentConditions untilElement(final Supplier<? extends FluentWebElement> element) {
        updateWaitWithDefaultExceptions();
        return WaitConditionProxy.element(this, "Element " + element, element);
    }

    @Override
    public FluentListConditions untilElements(final Supplier<? extends List<? extends FluentWebElement>> elements) {
        updateWaitWithDefaultExceptions();
        return WaitConditionProxy.one(this, "Elements " + elements, elements);
    }

    @Override
    public FluentListConditions untilEachElements(final Supplier<? extends List<? extends FluentWebElement>> elements) {
        updateWaitWithDefaultExceptions();
        return WaitConditionProxy.each(this, "Elements " + elements, elements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FluentWaitWindowConditions untilWindow(final String windowName) {
        return new FluentWaitWindowConditions(this, windowName);
    }

    @Override
    public FluentWaitPageConditions untilPage() {
        updateWaitWithDefaultExceptions();
        return new FluentWaitPageConditions(this, driver);
    }

    @Override
    public FluentWaitPageConditions untilPage(final FluentPage page) {
        updateWaitWithDefaultExceptions();
        return new FluentWaitPageConditions(this, driver, page);
    }

    @Override
    public FluentWait explicitlyFor(final long amount, final TimeUnit timeUnit) {
        try {
            timeUnit.sleep(amount);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    @Override
    public FluentWait explicitlyFor(final long amount) {
        return explicitlyFor(amount, TimeUnit.MILLISECONDS);
    }

}
