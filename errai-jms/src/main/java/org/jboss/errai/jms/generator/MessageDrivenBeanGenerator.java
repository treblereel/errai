package org.jboss.errai.jms.generator;

import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.jms.shared.ErraiJMSMDBClientUtil;
import org.jboss.errai.jms.util.ErraiJMSMDBUtil;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * Template for MDB server side generators
 * 
 * @author Dmitrii Tikhomirov
 *
 */

public abstract class MessageDrivenBeanGenerator {

    public static final String SERVER_MDB_PACKAGE_NAME = "org.jboss.errai.server.mdb";
    protected MessageDrivenBeanConfiguration config;

    public MessageDrivenBeanGenerator(Decorable decorable, MessageDrivenBeanConfiguration config) {
        GeneratorContext generatorContext = decorable.getInjectionContext().getProcessingContext().getGeneratorContext();
        this.config = config;
        String outputDirCdt = ErraiJMSMDBUtil.getOutputDirCandidate(generatorContext);
        generate(config, outputDirCdt);
    }

    public abstract void generate(MessageDrivenBeanConfiguration config, String outputPath);

    public String generateFullClassPath(MessageDrivenBeanConfiguration config, String path) {
        return path + "/" + ErraiJMSMDBClientUtil.replaceDotsWithSlashes(SERVER_MDB_PACKAGE_NAME) + "/"
                + getGeneratedBeanName(config);
    }

    public abstract String getGeneratedBeanName(MessageDrivenBeanConfiguration config);
}
