package de.htw_berlin.tpro.user_management.persistence.facade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ContextFacadeImplProducer {
	
	final Logger logger = LoggerFactory.getLogger(ContextFacadeImplProducer.class);
	private static ContextFacadeImpl contextFacade = new ContextFacadeImpl();

	@Produces @DefaultContextFacade 
	public ContextFacadeImpl getContextFacade() {
		logger.info("Produced ContextFacadeImplProducer instance !!!");
		return contextFacade;
	}
	
}
