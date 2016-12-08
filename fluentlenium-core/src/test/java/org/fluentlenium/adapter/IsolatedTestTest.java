package org.fluentlenium.adapter;

import org.assertj.core.api.Assertions;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.proxy.LocatorProxies;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class IsolatedTestTest {
    private final Supplier<WebDriver> webDriverFactory = () -> mock(WebDriver.class);

    @Mock
    private WebElement element;

    @Mock
    private WebElement pageElement;

    private static class SomePage extends FluentPage {
        private FluentWebElement pageElement;
    }

    private class IsolatedTestSpy extends IsolatedTest {
        @Page
        private SomePage page;

        private FluentWebElement element;

        @Override
        public WebDriver newWebDriver() {
            final WebDriver webDriver = webDriverFactory.get();

            when(webDriver.findElement(new ByIdOrName("element"))).thenReturn(IsolatedTestTest.this.element);
            when(webDriver.findElement(new ByIdOrName("pageElement"))).thenReturn(IsolatedTestTest.this.pageElement);

            return webDriver;
        }

        public void testSomething() {
            Assertions.assertThat(LocatorProxies.getLocatorResult(element.now().getElement()))
                    .isSameAs(IsolatedTestTest.this.element);
            Assertions.assertThat(LocatorProxies.getLocatorResult(page.pageElement.now().getElement()))
                    .isSameAs(IsolatedTestTest.this.pageElement);
        }
    }

    @Test
    public void testIsolated() {
        final IsolatedTestSpy spy = spy(new IsolatedTestSpy());
        spy.testSomething();

        verify(spy.getDriver(), never()).quit();
        spy.quit();

        //verify(spy.getDriver()).quit();
    }
}
