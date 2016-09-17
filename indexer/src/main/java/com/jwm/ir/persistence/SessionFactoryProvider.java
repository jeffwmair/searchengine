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
	private final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			Configuration configuration = new Configuration();
			configuration.configure("hibernate.cfg.xml");
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
