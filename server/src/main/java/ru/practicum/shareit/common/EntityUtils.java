package ru.practicum.shareit.common;

import lombok.experimental.UtilityClass;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;

@UtilityClass
public class EntityUtils {
    private static final String DEFAULT_NAME = "Объект";

    public static <T> T findOrThrow(JpaRepository<T, Long> repository, Long id) {
        String entityName = getEntityClassDisplayName(repository);

        return repository.findById(id).orElseThrow(() ->
                new NotFoundException(
                        String.format("%s с id=%d не найден(-о)", entityName, id)
                )
        );
    }

    private static String getClassDisplayName(Class<?> clazz) {
        DisplayName annotation = clazz.getAnnotation(DisplayName.class);
        return annotation != null ? annotation.value() : DEFAULT_NAME;
    }

    private static <T> String getEntityClassDisplayName(JpaRepository<T, ?> repository) {
        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(
                repository.getClass(),
                JpaRepository.class
        );
        if (typeArguments == null || typeArguments.length == 0) {
            return DEFAULT_NAME;
        }
        return getClassDisplayName(typeArguments[0]);
    }
}
