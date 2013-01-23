package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * to be removed
 *
 * @author Olga Melnichuk
 */
public class AnnotareCheckListProvider implements Provider<List<CheckDefinition>> {

    private final List<CheckDefinition> checks = newArrayList();

    private final InstanceProvider instanceProvider;

    @Inject
    public AnnotareCheckListProvider(final Injector injector, @Named("libPaths") Set<URL> libPaths) {

        instanceProvider = new InstanceProvider() {
            @Override
            public <T> T newInstance(Class<T> clazz) {
                return injector.getInstance(clazz);
            }
        };

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(libPaths)
                        .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner())
        );

        Set<Class<?>> classBasedChecks = reflections.getTypesAnnotatedWith(MageTabCheck.class);
        for (final Class<?> clazz : classBasedChecks) {
            checks.add(new ClassBasedCheckDefinition(clazz, instanceProvider));
        }

        Set<Method> methodBasedChecks = reflections.getMethodsAnnotatedWith(MageTabCheck.class);
        for (Method method : methodBasedChecks) {
            checks.add(new MethodBasedCheckDefinition(method, instanceProvider));
        }
    }

    @Override
    public List<CheckDefinition> get() {
        return checks;
    }

}

