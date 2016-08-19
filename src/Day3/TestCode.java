package Day3;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class TestCode {
	
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
		
		TestCode testCode = new TestCode();
		testCode.setup();
		
		Session session = testCode.sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		
		Email email = new Email("First Email");
		Message message = new Message("First Message");
		//email.setMessage(message);
		message.setEmail(email);
		
		session.save(email);
		session.save(message);
		
		transaction.commit();
		session.close();
		
	}

}
