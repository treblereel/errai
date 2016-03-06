package org.jboss.errai.jms.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.bus.client.api.SubscribeListener;
import org.jboss.errai.bus.client.api.UnsubscribeListener;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.framework.SubscriptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@Startup
@Singleton
@Named("ClientQueueListener")
public class ClientQueueListener {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	private MessageBus messageBus;

	class QueueClients {
		private Iterator<String> iterator;
		private Queue<String> sessions = new ConcurrentLinkedQueue<String>();

		QueueClients() {
			iterator = sessions.iterator();
		}

		/**
		 * Add Queue client Session to pull
		 * 
		 * @param session
		 */
		@Lock(LockType.WRITE)
		public void add(String session) {
			sessions.add(session);
		}

		/**
		 * Obtain next Session
		 * 
		 * @return String as session id
		 */
		@Lock(LockType.WRITE)
		public String next() {
			if (sessions.size() != 0) {
				if (!iterator.hasNext()) {
					iterator = sessions.iterator();
				}

				String robin = iterator.next();
				return robin;
			}
			return "";
		}

		/**
		 * Remove Queue client Session to pull return true if last element
		 * 
		 * @param session
		 * @return
		 */
		@Lock(LockType.WRITE)
		public Boolean remove(String session) {
			sessions.remove(session);
			if (sessions.size() == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	private Map<String, QueueClients> cache = new HashMap<String, QueueClients>();

	public ClientQueueListener() {

	}

	@PostConstruct
	private void init() {
		messageBus.addSubscribeListener(new SubscribeListener() {
			@Override
			public void onSubscribe(SubscriptionEvent event) {
				if (StringUtils.isNotBlank(event.getSubject())
						&& StringUtils.isNotBlank(event.getSessionId())) {
					if (cache.containsKey(event.getSubject())) {
						logger.debug("SubscriptionEvent " + event.getSubject()
								+ " " + event.getSessionId());
						cache.get(event.getSubject()).add(event.getSessionId());
					}
				}
			}
		});

		messageBus.addUnsubscribeListener(new UnsubscribeListener() {
			@Override
			public void onUnsubscribe(SubscriptionEvent event) {
				if (cache.containsKey(event.getSubject())) {
					logger.debug("UnsubscriptionEvent " + event.getSubject()
							+ " " + event.getSessionId());
					if (cache.get(event.getSubject()).remove(
							event.getSessionId()))
						;
				}
			}
		});
	}

	/**
	 * Obtain next SessionId
	 * 
	 * @return String as session id
	 */
	public String next(String messageDrivenBeanName) {
		if (cache.containsKey(messageDrivenBeanName))
			return cache.get(messageDrivenBeanName).next();
		return "";
	}

	public void subscribe(String queue) {
		if (!cache.containsKey(queue))
			logger.debug("subscribe to queue: " +queue);
			cache.put(queue, new QueueClients());
	}
}
