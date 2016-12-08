package org.fluentlenium.core.hook.wait;

import org.assertj.core.api.ThrowableAssert;
import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.components.DefaultComponentInstantiator;
import org.fluentlenium.core.wait.FluentWait;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WaitHookTest {

    @Mock
    private FluentControl fluentControl;

    @Mock
    private WebElement element;

    @Mock
    private ElementLocator locator;

    private ComponentInstantiator instantiator;

    private WaitHook waitHook;

    private FluentWait wait;

    @Before
    public void before() {
        instantiator = new DefaultComponentInstantiator(fluentControl);
        wait = new FluentWait(fluentControl);

        when(fluentControl.await()).thenReturn(wait);
        when(element.isEnabled()).thenReturn(true);
        when(element.isDisplayed()).thenReturn(true);

        final WaitHookOptions waitHookOptions = new WaitHookOptions();
        waitHookOptions.setAtMost(100L);
        waitHookOptions.setTimeUnit(TimeUnit.MILLISECONDS);
        waitHookOptions.setPollingEvery(10L);

        waitHook = new WaitHook(fluentControl, instantiator, () -> element, () -> locator, () -> "toString",
                waitHookOptions);
    }

    @Test
    public void testElementNotFound() {
        assertThatThrownBy(() -> waitHook.findElement()).isExactlyInstanceOf(TimeoutException.class);
    }

    @Test
    public void testElementListNotFound() {
        assertThatThrownBy(() -> waitHook.findElements()).isExactlyInstanceOf(TimeoutException.class);
    }

    @Test
    public void testElementFound() {
        final WebElement childElement = mock(WebElement.class);

        when(locator.findElement()).thenReturn(childElement);

        final WebElement found = waitHook.findElement();
        assertThat(found).isSameAs(childElement);
    }

    @Test
    public void testElementListFound() {
        final WebElement element1 = mock(WebElement.class);
        final WebElement element2 = mock(WebElement.class);
        final WebElement element3 = mock(WebElement.class);

        when(locator.findElements()).thenReturn(Arrays.asList(element1, element2, element3));

        final List<WebElement> found = waitHook.findElements();
        assertThat(found).containsExactly(element1, element2, element3);
    }

    @Test
    public void testElementClick() {
        final WebElement childElement = mock(WebElement.class);

        waitHook.click();
        verify(element).click();
    }

    @Test
    public void testElementSendKeys() {
        final WebElement childElement = mock(WebElement.class);

        waitHook.sendKeys("abc");
        verify(element).sendKeys("abc");
    }

    @Test
    public void testElementSubmit() {
        final WebElement childElement = mock(WebElement.class);

        waitHook.submit();
        verify(element).submit();
    }

    @Test
    public void testElementClear() {
        final WebElement childElement = mock(WebElement.class);

        waitHook.clear();
        verify(element).clear();
    }

    @Test
    public void testDefaultOptions() {
        final WaitHook defaultWaitHook = new WaitHook(fluentControl, instantiator, () -> element,
                () -> locator, () -> "toString", null);

        assertThat(defaultWaitHook.getOptions()).isEqualToComparingFieldByField(new WaitHookOptions());
    }
}
