package codesquad.webserver.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class ClassFinderTest {
    @Test
    @DisplayName("지정 패키지에 존재하는 클래스를 모두 찾습니다.")
    void getClassesForPackage_shouldReturnClassesInPackage() throws IOException, ClassNotFoundException {
        // Arrange
        String packageName = "codesquad.webserver.util";

        // Act
        List<Class<?>> classes = ClassFinder.getClassesForPackage(packageName);

        // Assert
        Assertions.assertFalse(classes.isEmpty());
        Assertions.assertTrue(classes.stream()
            .anyMatch(c -> c.getName().equals("codesquad.webserver.util.ClassFinder")));
    }

    @Test
    @DisplayName("지정 패키지에 존재하는 클래스가 없는 경우 빈 리스트를 반환합니다.")
    void getClassesForPackage_shouldReturnEmptyListForNonExistentPackage() throws IOException, ClassNotFoundException {
        // Arrange
        String packageName = "non.existent.package";

        // Act
        List<Class<?>> classes = ClassFinder.getClassesForPackage(packageName);

        // Assert
        Assertions.assertTrue(classes.isEmpty());
    }
}
