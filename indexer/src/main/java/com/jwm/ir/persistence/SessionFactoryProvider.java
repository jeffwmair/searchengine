package com.jwm.ir.persistence;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Special thanks to Stackoverflow since the session factory from the hibernate documentation doesn't work!
 * http://stackoverflow.com/questions/7986750/create-session-factory-in-hibernate-4
 * @author Jeff
 */
public class SessionFactoryProvider {

	private static Logger log = LogManager.getLogger(SessionFactoryProvider.class);
	private final SessionFactory sessionFactory;
	private final static String ConfigFileProduction = "hibernate.cfg.xml";
	private final static String ConfigFileTest = "hibernate.test.cfg.xml";
	private static String configFile;
	public enum Mode { Production, Test }

	public SessionFactoryProvider(Mode mode) {
	    switch (mode) {
			case Production:
				configFile = ConfigFileProduction;
				break;
			case Test:
				configFile = ConfigFileTest;
				break;
			default:
			    throw new RuntimeException("Unknown mode:"+mode);
		}

		sessionFactory = buildSessionFactory();
	}

	private static SessionFactory buildSessionFactory() {
		try {
			Configuration configuration = new Configuration();
			log.debug("Using hibernate config file:"+configFile);
			configuration.configure(configFile);
			StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
			
			/* export schema to a file */
			/*
			SchemaExport schemaExport = new SchemaExport(configuration);
			schemaExport.setOutputFile("schema.sql");
			schemaExport.setFormat(true);
			// not sure about these flags: script, export, justDrop, justCreate
			schemaExport.execute(false, false, false, false);
			*/

			return configuration.buildSessionFactory(ssrb.build());
		} catch (Throwable ex) {
			log.error("Initial SessionFactory creation failed." + ex, ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
