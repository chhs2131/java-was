package codesquad.webserver.util;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.webserver.handler.HandlerPath;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.util.fake.FakeClass;
import codesquad.webserver.util.fake.FakeComponent;
import codesquad.webserver.util.fake.FakeConstructor;
import codesquad.webserver.util.fake.FakeController;
import codesquad.webserver.util.fake.FakeImpl1;
import codesquad.webserver.util.fake.FakeImpl2;
import codesquad.webserver.util.fake.FakeImplPrimary;
import codesquad.webserver.util.fake.FakeRepository;
import java.lang.reflect.Method;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class AnnotationScannerTest {
    private AnnotationScanner annotationScanner;

    @BeforeEach
    public void setUp() {
        annotationScanner = new AnnotationScanner();
    }

    @Test
    @DisplayName("어노테이션을 기준으로 컴포넌트를 성공적으로 판별합니다.")
    void testGetComponents() {
        List<Class<?>> classes = Arrays.asList(
            FakeController.class,
            FakeRepository.class,
            FakeComponent.class,
            FakeClass.class
        );

        List<Class<?>> components = annotationScanner.getComponents(classes);
        assertEquals(3, components.size());
        assertTrue(components.contains(FakeController.class));
        assertTrue(components.contains(FakeRepository.class));
        assertTrue(components.contains(FakeComponent.class));
        assertFalse(components.contains(FakeClass.class));
    }

    @Test
    @DisplayName("전달된 클래스들의 인스턴스를 정상적으로 생성합니다.")
    void testGetInstances() {
        List<Class<?>> components = Arrays.asList(
            FakeController.class,
            FakeRepository.class,
            FakeComponent.class
        );

        Map<Class<?>, Object> instances = annotationScanner.getInstances(components);
        assertEquals(3, instances.size());
        final Set<Class<?>> classes = instances.keySet();
        assertTrue(classes.contains(FakeController.class));
        assertTrue(classes.contains(FakeRepository.class));
        assertTrue(classes.contains(FakeComponent.class));
    }

    @Test
    @DisplayName("인터페이스를 구현하는 컴포넌트가 중복 존재하는 경우 예외를 반환합니다.")
    void testDuplicateComponents() {
        List<Class<?>> components = Arrays.asList(
            FakeImpl1.class,
            FakeImpl2.class
        );

        assertThrows(IllegalStateException.class, () -> annotationScanner.getInstances(components));
    }

    @Test
    @DisplayName("인터페이스를 구현하는 컴포넌트가 중복 존재하는 경우 @Primary가 붙은 것을 확인하고 모두 등록합니다.")
    void testDuplicateComponentsWithPrimary() {
        List<Class<?>> components = Arrays.asList(
            FakeImpl1.class,
            FakeImpl2.class,
            FakeImplPrimary.class
        );

        final Map<Class<?>, Object> instances = annotationScanner.getInstances(components);
        final Set<Class<?>> classes = instances.keySet();
        assertTrue(classes.contains(FakeImplPrimary.class));
        assertTrue(classes.contains(FakeImpl1.class));
        assertTrue(classes.contains(FakeImpl2.class));
    }

    @Test
    @DisplayName("여러 컴포넌트가 있을때 @Primary 컴포넌트를 주입 받습니다.")
    void testDuplicateComponentsWithPrimary2() {
        List<Class<?>> components = Arrays.asList(
            FakeImpl1.class,
            FakeImpl2.class,
            FakeImplPrimary.class,
            FakeConstructor.class
        );

        final Map<Class<?>, Object> instances = annotationScanner.getInstances(components);
        FakeConstructor o = (FakeConstructor) instances.get(FakeConstructor.class);

        assertEquals(100, o.getNumber());
    }

    @Test
    @DisplayName("맵핑되는 Handler 요청이 있는 경우 RequestMap을 통해 정상 확인합니다.")
    void testGetRequestMap() {
        List<Class<?>> components = Arrays.asList(
            FakeController.class
        );

        Map<HandlerPath, Method> requestMap = annotationScanner.getRequestMap(components);
        assertEquals(1, requestMap.size());
        assertTrue(requestMap.containsKey(new HandlerPath(HttpMethod.GET, "/fake")));
    }

    @Test
    @DisplayName("맵핑되는 Handler 요청이 없다면 False를 반환합니다.")
    void testGetRequestMap_Failed() {
        List<Class<?>> components = Arrays.asList(
            FakeController.class
        );

        Map<HandlerPath, Method> requestMap = annotationScanner.getRequestMap(components);
        assertEquals(1, requestMap.size());
        assertFalse(requestMap.containsKey(new HandlerPath(HttpMethod.GET, "/nothave")));
    }
}
