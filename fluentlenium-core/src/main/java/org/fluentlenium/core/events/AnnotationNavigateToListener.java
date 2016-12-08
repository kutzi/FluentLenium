package org.fluentlenium.core.events;

import java.util.function.Function;
import org.fluentlenium.utils.ReflectionUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * NavigateTo annotation listener
 */
class AnnotationNavigateToListener extends AbstractAnnotationListener implements NavigateToListener {
    private final Method method;
    private final String annotationName;

    /**
     * Creates a new navigate to annotation listener
     *
     * @param method         method to call when the event occurs
     * @param container      container to call when the event occurs
     * @param annotationName name of the annotation
     * @param priority       priority of this listener
     */
    AnnotationNavigateToListener(final Method method, final Object container, final String annotationName, final int priority) {
        super(container, priority);
        this.method = method;
        this.annotationName = annotationName;
    }

    /**
     * Get a function that retrieves argument value based on argument class.
     *
     * @param url    url
     * @param driver driver
     * @return function returning argument value from argument class
     */
    protected Function<Class<?>, Object> getArgsFunction(final String url, final WebDriver driver) {
        return input -> {
            if (input.isAssignableFrom(String.class)) {
                return url;
            }
            if (input.isAssignableFrom(WebDriver.class)) {
                return driver;
            }
            return null;
        };
    }

    @Override
    public void on(final String url, final WebDriver driver) {
        final Class<?>[] parameterTypes = method.getParameterTypes();

        final Object[] args = ReflectionUtils.toArgs(getArgsFunction(url, driver), parameterTypes);

        try {
            ReflectionUtils.invoke(method, getContainer(), args);
        } catch (final IllegalAccessException e) {
            throw new EventAnnotationsException("An error has occured in " + annotationName + " " + method, e);
        } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new EventAnnotationsException("An error has occured in " + annotationName + " " + method, e);
        }
    }
}
