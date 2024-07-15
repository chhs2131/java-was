package codesquad.webserver.util;

import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.handler.HandlerPath;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationScanner {
    private AnnotationScanner() {}

    public static List<Class<?>> getComponents(List<Class<?>> classes) {
        return classes.stream()
            .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
            .toList();
    }

    public static Map<Class<?>, Object> getInstances(List<Class<?>> components) {
        Map<Class<?>, Object> map = new HashMap<>();

        components.forEach(component -> {
                try {
                    final Constructor<?> constructor = component.getConstructor();
                    final Object o = constructor.newInstance();
                    map.put(component, o);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
        });
        return map;
    }

    public static Map<HandlerPath, Method> getRequestMap(List<Class<?>> components) {
        Map<HandlerPath, Method> requestMap = new HashMap<>();

        components.stream()
            .map(clazz -> Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .toList()
            )
            .flatMap(List::stream)
            .forEach(method -> {
                final RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                final HandlerPath handlerPath = new HandlerPath(annotation.method(), annotation.path());
                requestMap.put(handlerPath, method);
            });

        return requestMap;
    }
}
