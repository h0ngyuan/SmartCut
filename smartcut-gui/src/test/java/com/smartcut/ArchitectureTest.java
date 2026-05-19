package com.smartcut;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.smartcut", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // No circular dependencies
    @ArchTest
    public static final ArchRule no_cycles = slices()
        .matching("com.smartcut.(*)..")
        .should().beFreeOfCycles()
        .allowEmptyShould(true);

    // service layer must not depend on ui layer
    @ArchTest
    public static final ArchRule service_should_not_depend_on_ui =
        noClasses()
            .that().resideInAPackage("com.smartcut.service..")
            .should().dependOnClassesThat().resideInAPackage("com.smartcut.ui..")
            .allowEmptyShould(true);

    // model layer must not depend on service or ui layers
    @ArchTest
    public static final ArchRule model_should_not_depend_on_upper_layers =
        noClasses()
            .that().resideInAPackage("com.smartcut.model..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("com.smartcut.service..", "com.smartcut.ui..")
            .allowEmptyShould(true);
}
