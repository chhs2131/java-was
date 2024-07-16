package codesquad.webserver.util;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.webserver.handler.HandlerPath;
import codesquad.webserver.http.type.HttpMethod;
import codesquad.webserver.util.fake.FakeClass;
import codesquad.webserver.util.fake.FakeComponent;
import codesquad.webserver.util.fake.FakeController;
import codesquad.webserver.util.fake.FakeRepository;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class AnnotationScannerTest {
    @Test
    @DisplayName("어노테이션을 기준으로 컴포넌트를 성공적으로 판별합니다.")
    void testGetComponents() {
        List<Class<?>> classes = Arrays.asList(
            FakeController.class,
            FakeRepository.class,
            FakeComponent.class,
            FakeClass.class
        );

        List<Class<?>> components = AnnotationScanner.getComponents(classes);
        assertEquals(3, components.size());
        assertTrue(components.contains(FakeController.class));
        assertTrue(components.contains(FakeRepository.class));
        assertTrue(components.contains(FakeComponent.class));
    }

    @Test
    @DisplayName("전달된 클래스들의 인스턴스를 정상적으로 생성합니다.")
    void testGetInstances() {
        List<Class<?>> components = Arrays.asList(
            FakeController.class,
            FakeRepository.class,
            FakeComponent.class
        );

        Map<Class<?>, Object> instances = AnnotationScanner.getInstances(components);
        assertEquals(3, instances.size());
        assertTrue(instances.containsKey(FakeController.class));
        assertTrue(instances.containsKey(FakeRepository.class));
        assertTrue(instances.containsKey(FakeComponent.class));
    }

    @Test
    @DisplayName("맵핑되는 Handler 요청이 있는 경우 RequestMap을 통해 정상 확인합니다.")
    void testGetRequestMap() {
        List<Class<?>> components = Arrays.asList(
            FakeController.class
        );

        Map<HandlerPath, Method> requestMap = AnnotationScanner.getRequestMap(components);
        assertEquals(1, requestMap.size());
        assertTrue(requestMap.containsKey(new HandlerPath(HttpMethod.GET, "/fake")));
    }

    @Test
    @DisplayName("맵핑되는 Handler 요청이 없다면 False를 반환합니다.")
    void testGetRequestMap_Failed() {
        List<Class<?>> components = Arrays.asList(
            FakeController.class
        );

        Map<HandlerPath, Method> requestMap = AnnotationScanner.getRequestMap(components);
        assertEquals(1, requestMap.size());
        assertFalse(requestMap.containsKey(new HandlerPath(HttpMethod.GET, "/nothave")));
    }
}
