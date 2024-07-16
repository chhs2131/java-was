package codesquad.webserver.util;

import codesquad.webserver.annotation.Component;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.Repository;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.handler.HandlerPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationScanner {
    private AnnotationScanner() {}

    public static List<Class<?>> getComponents(List<Class<?>> classes) {
        return classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class)
                        || clazz.isAnnotationPresent(Repository.class)
                        || clazz.isAnnotationPresent(Component.class))
                .collect(Collectors.toList());
    }

    public static Map<Class<?>, Object> getInstances(List<Class<?>> components) {
        Map<Class<?>, Object> instances = new HashMap<>();
        Map<Class<?>, Class<?>> interfaceToImpl = new HashMap<>();
        Map<Class<?>, List<Class<?>>> dependencies = new HashMap<>();

        // 인터페이스로 추상화된 경우에도 탐색할 수 있도록 자료구조 추가
        for (Class<?> component : components) {
            for (Class<?> interf : component.getInterfaces()) {
                interfaceToImpl.put(interf, component);
            }
        }

        // 의존 관계가 있는 컴포넌트들 탐색
        for (Class<?> component : components) {
            Constructor<?> constructor = component.getConstructors()[0];
            dependencies.put(component, Arrays.stream(constructor.getParameterTypes()).toList());
        }

        // 의조넝 주입
        while (!dependencies.isEmpty()) {
            Iterator<Map.Entry<Class<?>, List<Class<?>>>> iterator = dependencies.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Class<?>, List<Class<?>>> entry = iterator.next();
                Class<?> component = entry.getKey();
                List<Class<?>> deps = entry.getValue();

                if (instances.keySet().containsAll(deps) || deps.stream().allMatch(dep -> instances.containsKey(interfaceToImpl.get(dep)))) {
                    try {
                        Constructor<?> constructor = component.getConstructors()[0];
                        Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                                .map(paramType -> {
                                    if (instances.containsKey(paramType)) {
                                        return instances.get(paramType);
                                    } else {
                                        return instances.get(interfaceToImpl.get(paramType));
                                    }
                                })
                                .toArray();
                        Object instance = constructor.newInstance(parameters);
                        instances.put(component, instance);
                        iterator.remove();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return instances;
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
