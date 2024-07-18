package codesquad.webserver.util;

import codesquad.webserver.annotation.Component;
import codesquad.webserver.annotation.Controller;
import codesquad.webserver.annotation.Primary;
import codesquad.webserver.annotation.Repository;
import codesquad.webserver.annotation.RequestMapping;
import codesquad.webserver.handler.HandlerPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationScanner {
    private AnnotationScanner() {}

    public static List<Class<?>> getComponents(List<Class<?>> classes) {  // TODO 내부적으로 관리하면 될듯!
        return classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class)
                        || clazz.isAnnotationPresent(Repository.class)
                        || clazz.isAnnotationPresent(Component.class))
                .toList();
    }

    /**
     * Component 객체들을 생성하고 보관하는 컨테이너를 반환합니다.
     * @param components
     * @return
     */
    public static Map<Class<?>, Object> getInstances(List<Class<?>> components) {
        Map<Class<?>, Object> instances = new HashMap<>();
        Map<Class<?>, Class<?>> interfaceToImpl = new HashMap<>();
        Map<Class<?>, List<Class<?>>> dependencies = new HashMap<>();

        // 인터페이스로 추상화된 경우에도 탐색할 수 있도록 자료구조 추가 (인터페이스 주입시에 어떤 구현체를 사용할지 지정)
        // 모든 컴포넌트를 순회하며 인터페이스에 해당하는 구현체 목록을 생성합니다.
        Map<Class<?>, List<Class<?>>> interfaceImpls = new HashMap<>();
        for (Class<?> component : components) {
            for (Class<?> interf : component.getInterfaces()) {
                final List<Class<?>> orDefault = interfaceImpls.getOrDefault(interf, new ArrayList<>());
                orDefault.add(component);
                interfaceImpls.put(interf, orDefault);
            }
        }

        // 인터페이스 구현체 목록을 확인하며 구현체를 선택합니다.
        for (final Class<?> interf : interfaceImpls.keySet()) {
            final List<Class<?>> classes = interfaceImpls.get(interf);  // 인터페이스 구현체들 목록 리스트

            // 구현체가 하나만 존재하면 그냥 그걸 추가한다.
            if (classes.size() == 1) {
                interfaceToImpl.put(interf, classes.get(0));
                continue;
            }

            // 2개 이상 존재하는 경우!
            boolean havePrimary = false;
            for (final Class<?> aClass : classes) {
                if (aClass.isAnnotationPresent(Primary.class)) {  // Primary가 2개 이상 존재하면..?
                    interfaceToImpl.put(interf, aClass);
                    havePrimary = true;
                }
            }
            if (!havePrimary) {
                throw new IllegalStateException("중복되는 Component가 존재합니다. 하나만 남기거나 우선순위(@Primary)를 지정하세요.");
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
