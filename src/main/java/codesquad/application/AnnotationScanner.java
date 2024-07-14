package codesquad.application;

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
    public Map<Class<?>, Object> getComponents(List<Class<?>> controllers) {
        Map<Class<?>, Object> map = new HashMap<>();

        controllers.stream()
            .filter(controller -> controller.isAnnotationPresent(Controller.class))
            .forEach(controller -> {
                try {
                    final Constructor<?> constructor = controller.getConstructor();
                    final Object o = constructor.newInstance();
                    map.put(controller, o);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        return map;
    }

    public Map<HandlerPath, Method> getRequestMap(List<Class<?>> controllers) {
        Map<HandlerPath, Method> requestMap = new HashMap<>();

        controllers.stream()
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
