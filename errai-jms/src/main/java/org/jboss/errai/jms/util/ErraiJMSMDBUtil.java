package org.jboss.errai.jms.util;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJBException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.builder.MessageBuildCommand;
import org.jboss.errai.bus.client.api.builder.MessageBuildParms;
import org.jboss.errai.bus.client.api.builder.MessageBuildSendableWithReply;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.util.ClassChangeUtil;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.common.rebind.ClassListReader;
import org.jboss.errai.jms.generator.DiscoveryContext;
import org.jboss.errai.jms.generator.DiscoveryStrategy;
import org.jboss.errai.jms.server.impl.ClientReceiverQueueImpl;
import org.jboss.errai.jms.shared.impl.MessageImpl;
import org.jboss.errai.jms.shared.impl.TextMessageImpl;
import org.jboss.errai.jms.shared.impl.Type;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.marshalling.server.ServerMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;

public class ErraiJMSMDBUtil {
  private static final String[] candidateOutputDirectories = { "target/classes/", "war/WEB-INF/classes/",
      "web/WEB-INF/classes/", "target/war/WEB-INF/classes/", "WEB-INF/classes/", "src/main/webapp/WEB-INF/classes/" };

  private final static Logger logger = LoggerFactory.getLogger("HelloWorldMDBUtil.class");

  private static final DiscoveryStrategy[] rootDiscoveryStrategies = findDiscoveryStrategies();

  public static String getOutputDirCandidate(GeneratorContext generatorContext) {

    if (rootDiscoveryStrategies == null) {
      throw new RuntimeException();
    }

    logger.debug("searching candidate output directories for generated recievers");
    String result = "";
    File outputDirCdt = null;
    class DiscoveryContextImpl implements DiscoveryContext {
      boolean absolute = false;
      boolean vetoed = false;

      @Override
      public void resultsAbsolute() {
        this.absolute = true;
      }

      @Override
      public void veto() {
        this.vetoed = true;
      }
    }

    int deposits = 0;
    Strategies: for (final DiscoveryStrategy strategy : rootDiscoveryStrategies) {
      final DiscoveryContextImpl discoveryContext = new DiscoveryContextImpl();
      for (final String rootPath : strategy.getCandidate(generatorContext, discoveryContext)) {
        for (final String candidate : discoveryContext.absolute ? new String[] { "/" } : candidateOutputDirectories) {
          logger.debug("considering '" + rootPath + candidate + "' as module output path ...");

          if (discoveryContext.vetoed) {
            continue Strategies;
          }

          outputDirCdt = new File(rootPath + "/" + candidate).getAbsoluteFile();
          if (outputDirCdt.exists()) {
            logger.debug("   found '" + outputDirCdt + "' output directory");
            logger.debug("** deposited mdb reciever class in : " + outputDirCdt.getAbsolutePath());
            deposits++;
          }
          else {
            logger.debug("   " + outputDirCdt + " does not exist");
          }
        }
      }
      if (deposits > 0) {
        break;
      }
    }
    if (deposits == 0) {
      logger.warn(" *** the server mdb reciever was not deposited into your build output!\n"
              + "   A target output could not be resolved through configuration or auto-detection!");
    }
    else if (deposits > 0 && outputDirCdt != null) {
      result = outputDirCdt.getAbsolutePath();
    }
    return result;
  }

  public static DiscoveryStrategy[] findDiscoveryStrategies() {
    return new DiscoveryStrategy[] { new DiscoveryStrategy() {
      @Override
      public Set<String> getCandidate(final GeneratorContext context, final DiscoveryContext veto) {
        final File cwd = new File("").getAbsoluteFile();
        final Set<File> matching = ClassChangeUtil.findAllMatching("classlist.mf", cwd);
        final Set<String> candidateDirectories = new HashSet<String>();

        veto.resultsAbsolute();

        if (!matching.isEmpty()) {
          class Candidate {
            File root;
            int score;
          }

          Candidate bestCandidate = null;
          String gwtModuleName = RebindUtils.getModuleName(context);

          if (gwtModuleName != null) {
            if (gwtModuleName.endsWith(".JUnit")) {
              gwtModuleName = gwtModuleName.substring(0, gwtModuleName.length() - 6);
            }
            final int endIndex = gwtModuleName.lastIndexOf('.');
            if (endIndex != -1) {
              gwtModuleName = gwtModuleName.substring(0, endIndex);
            }
            else {
              gwtModuleName = "";
            }

            for (final File f : matching) {
              final Candidate candidate = new Candidate();
              candidate.root = f.getParentFile();
              final Set<String> clazzes = ClassListReader.getClassSetFromFile(f);
              for (final String fqcn : clazzes) {
                try {
                  final JClassType type = context.getTypeOracle().findType(fqcn);
                  if (type != null && fqcn.startsWith(gwtModuleName)) {
                    candidate.score++;
                  }
                } catch (Throwable ignored) {
                }
              }

              if (candidate.score > 0 && (bestCandidate == null || candidate.score > bestCandidate.score)) {
                bestCandidate = candidate;
              }
            }
            if (bestCandidate != null) {
              candidateDirectories.add(bestCandidate.root.getAbsolutePath());
            }
          }
        }
        return candidateDirectories;
      }
    }, new DiscoveryStrategy() {
      @Override
      public Set<String> getCandidate(final GeneratorContext context, final DiscoveryContext discoveryContext) {
        final ServerMappingContext ctx = MappingContextSingleton.get();
        final Map<String, String> matchNames = new HashMap<String, String>();
        for (final MetaClass cls : ctx.getDefinitionsFactory().getExposedClasses()) {
          matchNames.put(cls.getName(), cls.getName());
        }

        final File cwd = new File("").getAbsoluteFile();

        final Set<File> roots = ClassChangeUtil.findMatchingOutputDirectoryByModel(matchNames, cwd);

        if (!roots.isEmpty()) {
          for (final File file : roots) {
            logger.info(" ** signature matched root! " + file.getAbsolutePath());
          }
          discoveryContext.resultsAbsolute();
        }
        else {
          logger.warn(" ** NO ROOTS FOUND!");
          discoveryContext.veto();
        }

        final Set<String> rootsPaths = new HashSet<String>();
        for (final File f : roots) {
          rootsPaths.add(f.getAbsolutePath());
        }

        return rootsPaths;
      }
    }, new DiscoveryStrategy() {
      @Override
      public Set<String> getCandidate(final GeneratorContext context, final DiscoveryContext veto) {
        return Collections.singleton(new File("").getAbsolutePath());
      }
    }, new DiscoveryStrategy() {
      @Override
      public Set<String> getCandidate(final GeneratorContext context, final DiscoveryContext veto) {
        return Collections.singleton(RebindUtils.guessWorkingDirectoryForModule(context));
      }
    } };
  }

  public static byte getMessageType(Message message) {
    if (message instanceof TextMessage) {
      return org.jboss.errai.jms.shared.impl.Type.TEXT_TYPE;
    }
    else if (message instanceof ObjectMessage) {
      return org.jboss.errai.jms.shared.impl.Type.OBJECT_TYPE;
    }
    else if (message instanceof MapMessage) {
      return org.jboss.errai.jms.shared.impl.Type.MAP_TYPE;
    }
    else if (message instanceof BytesMessage) {
      return org.jboss.errai.jms.shared.impl.Type.BYTES_TYPE;
    }
    else if (message instanceof StreamMessage) {
      throw new EJBException("Unsupported message type");
    }
    else {
      return org.jboss.errai.jms.shared.impl.Type.DEFAULT_TYPE;
    }
  }

  public static Message parseMessage(Message message) {
    if (message instanceof TextMessage) {
      return new TextMessageImpl((TextMessage) message);
    }
    else {
      return new MessageImpl(message);
    }
  }

  public static org.jboss.errai.jms.generator.MessageDrivenBeanConfiguration parseMessageDrivenBeanAnnotationConfiguration() {// InjectableInstance<MessageDriven>
                                                                                                                              // instance)
                                                                                                                              // {
    return new org.jboss.errai.jms.generator.MessageDrivenBeanConfiguration();
  }

  private static void processBytesMessage(Message message,
          MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand) {
    try {
      messageBuildCommand.with("value",
              new String(((BytesMessage) message).getBody(byte[].class), StandardCharsets.UTF_8));
      messageBuildCommand.with("type", Type.BYTES_TYPE);
    } catch (JMSException e) {
      throw new EJBException(e);
    }

  }

  private static void processMapMessage(Message message,
          MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand) {
    try {
      messageBuildCommand.with("value", ((MapMessage) message).getBody(Map.class));
      messageBuildCommand.with("type", Type.MAP_TYPE);
    } catch (JMSException e) {
      throw new EJBException(e);
    }

  }

  private static void processObjectMessage(Message message,
          MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand) {
    try {
      messageBuildCommand.with("value", ((ObjectMessage) message).getObject());
      messageBuildCommand.with("type", Type.OBJECT_TYPE);
    } catch (JMSException e) {
      throw new EJBException(e);
    }

  }

  private static void processTextMessage(Message message,
          MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand) {
    try {
      messageBuildCommand.with("value", ((TextMessage) message).getText());
      messageBuildCommand.with("type", Type.TEXT_TYPE);
    } catch (JMSException e) {
      throw new EJBException(e);
    }

  }

  private static MessageBuildCommand<MessageBuildSendableWithReply> setValue(Message message,
          MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand) {
    switch (getMessageType(message)) {
    case 0:
      processTextMessage(message, messageBuildCommand);
      break;
    case 2:
      processObjectMessage(message, messageBuildCommand);
      break;
    case 3:
      processTextMessage(message, messageBuildCommand);
      break;
    case 4:
      processBytesMessage(message, messageBuildCommand);
      break;
    case 5:
      processMapMessage(message, messageBuildCommand);
      break;
    case 6:
      throw new EJBException("Stream message not supported");
    default:
      throw new EJBException("can't parse message");
    }

    return messageBuildCommand;
  }

  public static MessageBuildCommand<MessageBuildSendableWithReply> toMessageBusMessage(Message message)
          throws JMSException {
    org.jboss.errai.bus.client.api.messaging.Message busMessage = null;
    String destination = "";
    String destinationType = "";
    String replyTo = "";
    logger.info("Message type :" + message.getClass() + " corrid " + message.getJMSCorrelationID());

    if (message.getJMSDestination() instanceof Queue) {
      destination = ((Queue) message.getJMSDestination()).getQueueName();
      destinationType = Queue.class.getSimpleName();
    }
    else {
      destination = ((Topic) message.getJMSDestination()).getTopicName();
      destinationType = Topic.class.getSimpleName();
    }

    MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand = MessageBuilder.createMessage()
            .toSubject(destination);

    messageBuildCommand.with("JMSID", UUID.randomUUID().toString());
    messageBuildCommand.with("JMSDeliveryTime", message.getJMSDeliveryTime());
    messageBuildCommand.with("JMSDestinationType", destinationType);
    messageBuildCommand.with("JMSRedelivered", message.getJMSRedelivered());
    messageBuildCommand.with("JMSDeliveryCount", 0);

    for (@SuppressWarnings("unchecked")
    Enumeration<String> props = message.getPropertyNames(); props.hasMoreElements();) {
      String name = props.nextElement();

      logger.info("> " + name);
      Object prop = message.getObjectProperty(name);
      messageBuildCommand.with(name, prop);
    }

    messageBuildCommand.with("type", getMessageType(message));
    logger.debug("MessageParts.SessionID " + MessageParts.SessionID.name());
    setValue(message, messageBuildCommand).signalling();
    return messageBuildCommand;
  }
}
