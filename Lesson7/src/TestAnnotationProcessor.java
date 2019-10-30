import annotation.AfterSuite;
import annotation.BeforeSuite;
import annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Chaykin Ivan
 * @version 29.10.2019
 */
public class TestAnnotationProcessor {

    static int MIN_PRIORITY = 1;
    static int MAX_PRIORITY = 10;

    public static void start(String className) throws ClassNotFoundException {
        start(Class.forName(className));
    }

    public static void start(Class<?> clazz) {
        execute(clazz);
    }

    private static <T> void execute(Class<T> clazz) {
        final T testClass;
        Method beforeTestsMethod = null;
        Method afterTestsMethod = null;
        ArrayList<Method> methodList = new ArrayList<>();
        Comparator<Method> comparator = Comparator.comparing(Method -> Method.getAnnotation(Test.class).priority());

        try {
            testClass = clazz.newInstance();

            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(BeforeSuite.class)) {
                    if (beforeTestsMethod == null) {
                        beforeTestsMethod = method;
                    }
                    else {
                        throw new RuntimeException();
                    }
                } else if (method.isAnnotationPresent(AfterSuite.class)) {
                    if (afterTestsMethod == null) {
                        afterTestsMethod = method;
                    }
                    else {
                        throw new RuntimeException();
                    }
                } else {
                    final Test annotation = method.getAnnotation(Test.class);
                    if (annotation != null) {
                        final int priority = annotation.priority();
                        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
                            System.out.println("invalid priority: "
                                    + priority + ", should be in the range ["
                                    + MIN_PRIORITY + " - " + MAX_PRIORITY + "]");
                        }
                        else {
                            methodList.add(method);
                        }
                    }
                }
            }
            methodList.sort(comparator);

            if (beforeTestsMethod != null) {
                beforeTestsMethod.invoke(testClass);
            }
            for (Method method : methodList) {
                method.invoke(testClass);
            }
            if (afterTestsMethod != null) {
                afterTestsMethod.invoke(testClass);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
