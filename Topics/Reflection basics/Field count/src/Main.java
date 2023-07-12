import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 Get number of accessible public fields for a given class.
 */
class FieldGetter {

    public int getNumberOfAccessibleFields(Class<?> clazz) {
        // Add implementation here
        Field[] fields = clazz.getClass().getDeclaredFields();

        int num = 0;
        for (Field field: fields) {
            if (Modifier.isPublic(field.getModifiers())) num++;
        }
        return clazz.getFields().length;
    }

}