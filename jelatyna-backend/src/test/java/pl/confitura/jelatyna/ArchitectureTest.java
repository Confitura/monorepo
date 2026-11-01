package pl.confitura.jelatyna;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * The package structure of ADR-0005. Every rule is strict: any violation fails.
 */
@AnalyzeClasses(packages = "pl.confitura.jelatyna", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String FEATURES = "pl.confitura.jelatyna.(conference|webpage|identity).(*)..";
    private static final String DOMAINS = "pl.confitura.jelatyna.*.*.domain..";

    @ArchTest
    static final ArchRule everyClassLivesInAnArea = classes()
            .that().doNotBelongToAnyOf(JelatynaApplication.class)
            .should().resideInAnyPackage(
                    "pl.confitura.jelatyna.conference..",
                    "pl.confitura.jelatyna.webpage..",
                    "pl.confitura.jelatyna.identity..",
                    "pl.confitura.jelatyna.demo..",
                    "pl.confitura.jelatyna.platform..");

    @ArchTest
    static final ArchRule areasDependOnlyInTheAllowedDirection = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .withOptionalLayers(true)
            .layer("demo").definedBy("pl.confitura.jelatyna.demo..")
            .layer("webpage").definedBy("pl.confitura.jelatyna.webpage..")
            .layer("conference").definedBy("pl.confitura.jelatyna.conference..")
            .layer("identity").definedBy("pl.confitura.jelatyna.identity..")
            .layer("platform").definedBy("pl.confitura.jelatyna.platform..")
            .whereLayer("demo").mayNotBeAccessedByAnyLayer()
            .whereLayer("webpage").mayOnlyBeAccessedByLayers("demo")
            .whereLayer("conference").mayOnlyBeAccessedByLayers("webpage", "demo")
            .whereLayer("identity").mayOnlyBeAccessedByLayers("conference", "webpage", "demo")
            .whereLayer("platform").mayNotAccessAnyLayer();

    @ArchTest
    static final ArchRule domainsUseNoWebOrClientLibraries = noClasses()
            .that().resideInAPackage(DOMAINS)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.web..",
                    "java.net.http..",
                    "com.eventuallycoding.listmonk..",
                    "com.github.scribejava..",
                    "com.microtripit.mandrillapp..");

    @ArchTest
    static final ArchRule featuresUseOtherFeaturesDomainOnly = slices().matching(FEATURES)
            .should().notDependOnEachOther()
            .ignoreDependency(alwaysTrue(), resideInAPackage(DOMAINS));

    @ArchTest
    static final ArchRule featuresHaveNoCycles = slices().matching(FEATURES)
            .should().beFreeOfCycles();
}
