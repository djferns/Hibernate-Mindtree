package Day1;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class TestPerson {

	SessionFactory sessionFactory;
	
	public void setup(){
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
		srBuilder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	public static void main(String[] args) {
		
		TestPerson testPerson = new TestPerson();
		testPerson.setup();
		
		Session session = testPerson.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Employee employee = new Employee("Dexter");
		
		session.save(employee);
		tx.commit();
		session.close();		
		
	}

}
