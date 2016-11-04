package org.fluentlenium.core.action;

import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.script.JavascriptControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FluentJavascriptActionsTest {

    private final Object self = new Object();

    @Mock
    private JavascriptControl javascript;

    @Mock
    private FluentWebElement fluentWebElement;

    @Mock
    private WebElement element;

    FluentJavascriptActions actions;

    @Before
    public void before() {
        when(fluentWebElement.getElement()).thenReturn(element);
        actions = new FluentJavascriptActionsImpl(self, javascript, Supplier.ofInstance(fluentWebElement));
    }

    @Test
    public void testWithNoArgument() {
        actions.scrollIntoView();
        verify(javascript).executeScript("arguments[0].scrollIntoView();", element);
    }

    @Test
    public void testWithArgument() {
        actions.scrollIntoView(true);
        verify(javascript).executeScript("arguments[0].scrollIntoView(arguments[1]);", element, true);
    }

}
