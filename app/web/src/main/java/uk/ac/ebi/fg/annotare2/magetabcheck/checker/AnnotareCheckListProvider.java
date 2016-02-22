package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.reflections.util.ClasspathHelper.forPackage;

/**
 * to be removed
 *
 * @author Olga Melnichuk
 */
public class AnnotareCheckListProvider implements Provider<List<CheckDefinition>> {

    private static final Logger log = LoggerFactory.getLogger(AnnotareCheckListProvider.class);

    private final List<CheckDefinition> checks = newArrayList();

    private final ClassInstanceProvider instanceProvider;

    @Inject
    public AnnotareCheckListProvider(final Injector injector) {

        instanceProvider = new ClassInstanceProvider() {
            @Override
            public <T> T newInstance(Class<T> clazz) {
                return injector.getInstance(clazz);
            }
        };

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(forPackage("uk.ac.ebi.fg.annotare2.magetabcheck.checks"))
                        .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner())
        );

        Set<Class<?>> classBasedChecks = reflections.getTypesAnnotatedWith(MageTabCheck.class);
        for (final Class<?> clazz : classBasedChecks) {
            checks.add(new ClassBasedCheckDefinition(clazz, instanceProvider));
        }

        Set<Method> methodBasedChecks = reflections.getMethodsAnnotatedWith(MageTabCheck.class);
        for (Method method : methodBasedChecks) {
            checks.add(new MethodBasedCheckDefinition(method, instanceProvider));
        }
        log.info("Number of magetabcheck annotations found: {}", checks.size());
    }

    @Override
    public List<CheckDefinition> get() {
        return checks;
    }

}

