package xw.legacyserver.tools;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import xw.legacyserver.listeners.hibernate.RelationshipMetadata;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassMetadata {
    private static ClassMetadata instance = new ClassMetadata();

    private Reflections reflections;
    private Map<String, RelationshipMetadata> schemaMetadataMap;
    private Set<Field> fieldsOneToOne;
    private Set<Field> fieldsOneToMany;
    private Set<Field> fieldsManyToOne;
    private Set<Field> fieldsManyToMany;

    private ClassMetadata() {
        reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("xw.legacyserver.entities"))
            .setScanners(
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner(),
                new SubTypesScanner()
            )
            .useParallelExecutor(4));

        schemaMetadataMap = new HashMap<>();

        fieldsOneToOne = reflections.getFieldsAnnotatedWith(
            OneToOne.class);
        fieldsOneToMany = reflections.getFieldsAnnotatedWith(
            OneToMany.class);
        fieldsManyToOne = reflections.getFieldsAnnotatedWith(
            ManyToOne.class);
        fieldsManyToMany = reflections.getFieldsAnnotatedWith(
            ManyToMany.class);
    }

    public static RelationshipMetadata getSchemaMetadata(String entityName) {
        if (!instance.schemaMetadataMap.containsKey(entityName)) {
            instance.schemaMetadataMap.put(
                entityName,
                instance.generateSchemaMetadata(entityName)
            );
        }
        return instance.schemaMetadataMap.get(entityName);
    }

    private RelationshipMetadata generateSchemaMetadata(String entityName) {
        List<Map<String, Object>> allData = Stream.of(
            fieldsManyToMany.stream()
                .filter(field -> field.getDeclaringClass()
                    .getName()
                    .equals(entityName))
                .map(field -> this.mapCollection(field, "manyToMany")),

            fieldsOneToMany.stream()
                .filter(field -> field.getDeclaringClass()
                    .getName()
                    .equals(entityName))
                .map(field -> this.mapCollection(field, "oneToMany")),

            fieldsOneToOne.stream()
                .filter(field -> field.getDeclaringClass()
                    .getName()
                    .equals(entityName))
                .map(field -> this.mapObject(field, "oneToOne")),

            fieldsManyToOne.stream()
                .filter(field -> field.getDeclaringClass()
                    .getName()
                    .equals(entityName))
                .map(field -> this.mapObject(field, "manyToOne"))
        )
            .flatMap(Function.identity())
            .collect(Collectors.toList());

        return new RelationshipMetadata(allData);
    }

    private Map<String, Object> mapCollection(
        Field field,
        String type
    ) {
        ParameterizedType genericType =
            (ParameterizedType) field.getGenericType();
        Type actualTypeArgument =
            genericType.getActualTypeArguments()[0];

        Map<String, Object> data = new HashMap<>();
        data.put("field", field.getName());
        data.put("type", type);
        data.put("destination", actualTypeArgument.getTypeName());
        return data;
    }

    private Map<String, Object> mapObject(Field field, String type) {
        field.getAnnotation(JoinColumn.class);

        Map<String, Object> data = new HashMap<>();
        data.put("field", field.getName());

        data.put("type", type);
        data.put("destination", field.getType());
        return data;
    }

}
