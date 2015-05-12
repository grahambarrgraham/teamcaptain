package org.rrabarg.teamcaptain;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.io.UnderscoredCamelCaseResolver;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.FilePrintStreamFactory.ResolveToPackagedName;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.ParameterConverters.ExamplesTableConverter;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;

@RunWith(JUnitReportingRunner.class)
public class JBehaveRunnerTest extends JUnitStories {

    private final CrossReference xref = new CrossReference();

    public JBehaveRunnerTest() {
        configuredEmbedder().embedderControls().doGenerateViewAfterStories(false).doIgnoreFailureInStories(false)
                .doIgnoreFailureInView(true).useThreads(4).useStoryTimeoutInSecs(360);
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(CodeLocations.codeLocationFromClass(this.getClass()), "jbehave/*.story", "");
    }

    @Override
    public Configuration configuration() {
        final Class<? extends Embeddable> embeddableClass = this.getClass();

        final Properties viewResources = new Properties();
        viewResources.put("decorateNonHtml", "true");

        // Start from default ParameterConverters instance
        final ParameterConverters parameterConverters = new ParameterConverters();

        // factory to allow parameter conversion and loading from external
        // resources (used by StoryParser too)
        final ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(),
                new LoadFromClasspath(embeddableClass), parameterConverters);

        // add custom converters
        parameterConverters.addConverters(new DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ExamplesTableConverter(examplesTableFactory));

        return new MostUsefulConfiguration()
                .useStoryControls(new StoryControls().doDryRun(false).doSkipScenariosAfterFailure(false))
                .useStoryLoader(new LoadFromClasspath(embeddableClass))
                .useStoryParser(new RegexStoryParser(examplesTableFactory))
                .useStoryPathResolver(new UnderscoredCamelCaseResolver())
                .useStoryReporterBuilder(
                        new StoryReporterBuilder()
                                .withCodeLocation(CodeLocations.codeLocationFromClass(embeddableClass))
                                .withDefaultFormats().withPathResolver(new ResolveToPackagedName())
                                .withViewResources(viewResources)
                                .withFormats(Format.CONSOLE, Format.TXT, Format.HTML, Format.XML)
                                .withFailureTrace(true).withFailureTraceCompression(false).withCrossReference(xref))
                .useParameterConverters(parameterConverters)
                // use '%' instead of '$' to identify parameters
                .useStepPatternParser(new RegexPrefixCapturingPatternParser("%")).useStepMonitor(xref.getStepMonitor());
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new SpringStepsFactory(configuration(), createContext());
        // return new InstanceStepsFactory(configuration(), new
        // ArrangeMatchSteps());
    }

    private ApplicationContext createContext() {
        System.setProperty("spring.profiles.active", "test, inmemory, mutableclock");
        return new SpringApplicationContextFactory("jbehave/applicationcontext.xml").createApplicationContext();
    }

}