package org.fluentlenium.integration;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentException;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.components.ComponentsManager;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.integration.localtest.IntegrationFluentTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

public class ElementAsTest extends IntegrationFluentTest {
    @FindBy(css = "a.go-next")
    private Component goNextLink;

    @FindBy(css = "a.go-next")
    private ComponentNotAnElement goNextLink2;

    @Test
    public void testAsComponent() {
        goTo(DEFAULT_URL);
        final Component span = el("span").as(Component.class);
        assertThat(span).isNotNull();

        final FluentList<Component> spans = find("span").as(Component.class);
        assertThat(spans).isNotEmpty();
    }

    @Test(expected = ComponentException.class)
    public void testAsNotAComponent() {
        goTo(DEFAULT_URL);
        el("span").as(NotAComponent.class);
    }

    @Test(expected = ComponentException.class)
    public void testAsDefaultConstructorComponent() {
        goTo(DEFAULT_URL);
        el("span").as(InvalidComponent.class);
    }

    @Test
    public void testAsFullConstructorComponent() {
        goTo(DEFAULT_URL);
        final FullConstructorComponent component = el("span").as(FullConstructorComponent.class);

        assertThat(component.fluentControl).isSameAs(this);
        assertThat(component.element.getTagName()).isEqualTo("span");
        assertThat(component.instantiator).isInstanceOf(ComponentsManager.class);
    }

    @Test
    public void findByComponent() {
        goTo(DEFAULT_URL);
        assertThat(goNextLink.displayed()).isTrue();
    }

    @Test
    public void findByComponentNotFluentWebElement() {
        goTo(DEFAULT_URL);
        assertThat(goNextLink2.isDisplayed()).isTrue();
    }

    public static class Component extends FluentWebElement {
        public Component(final WebElement webElement, final FluentControl fluentControl,
                final ComponentInstantiator instantiator) {
            super(webElement, fluentControl, instantiator);
        }
    }

    public static class ComponentNotAnElement {

        private final WebElement element;

        public ComponentNotAnElement(final WebElement webElement) {
            this.element = webElement;
        }

        public boolean isDisplayed() {
            return this.element.isDisplayed();
        }

    }

    public static class NotAComponent extends FluentWebElement {
        public NotAComponent(final String invalidConstructorParam) { // NOPMD UnusedFormalParameter
            super(null, null, null);
        }
    }

    public static class FullConstructorComponent {

        private final WebElement element;
        private final ComponentInstantiator instantiator;
        private final FluentControl fluentControl;

        public FullConstructorComponent(final WebElement webElement, final FluentControl fluentControl,
                final ComponentInstantiator instantiator) {
            this.element = webElement;
            this.fluentControl = fluentControl;
            this.instantiator = instantiator;
        }

    }

    public static class InvalidComponent {
    }

}
