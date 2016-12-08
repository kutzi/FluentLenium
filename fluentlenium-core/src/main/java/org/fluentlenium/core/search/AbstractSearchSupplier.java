package org.fluentlenium.core.search;

import java.util.function.Supplier;
import org.fluentlenium.core.proxy.LocatorProxies;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.List;

/**
 * Abstract supplier providing toString() representation from elements proxy and search filters.
 */
public abstract class AbstractSearchSupplier implements Supplier<List<WebElement>> {
    private final List<SearchFilter> searchFilters;
    private final Object proxy;

    /**
     * Creates a new search supplier.
     *
     * @param searchFilters filters to display in toString()
     * @param proxy         proxy to use for toString()
     */
    public AbstractSearchSupplier(final List<SearchFilter> searchFilters, final Object proxy) {
        this.searchFilters = searchFilters;
        this.proxy = proxy;
    }

    @Override
    public String toString() {
        final ElementLocator locator = LocatorProxies.getLocatorHandler(proxy).getLocator();

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(locator);

        for (int i = 0; i < searchFilters.size(); i++) {
            if (i > 0) {
                stringBuilder.append(" and");
            }
            stringBuilder.append(' ').append(searchFilters.get(i));
        }

        return stringBuilder.toString();
    }
}
