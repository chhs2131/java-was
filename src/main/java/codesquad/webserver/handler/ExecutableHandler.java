package codesquad.webserver.handler;

import codesquad.webserver.file.ErrorPageResponseFactory;
import codesquad.webserver.http.HttpRequest;
import codesquad.webserver.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record ExecutableHandler(
        Method method,
        Object object
) {
    private static final Logger logger = LoggerFactory.getLogger(ExecutableHandler.class);

    public HttpResponse invoke(HttpRequest httpRequest) {
        try {
            return (HttpResponse) method.invoke(object, httpRequest);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.debug("핸들링 에러");
            e.getCause().printStackTrace();
            return ErrorPageResponseFactory.internalServerError("Message: " + e.getCause().getMessage() + " (" + e.getCause().toString() + ")");
        }
    }
}
