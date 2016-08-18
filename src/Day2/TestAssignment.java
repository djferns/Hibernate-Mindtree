package Day2;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class TestAssignment {

	SessionFactory sessionFactory;

	public void setup(){
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
		srBuilder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	public Student findStudent(Session session, String name){
		
		Query query = session.createQuery("from Student s where s.name = :name");
		query.setParameter("name", name);

		Student student = (Student) query.uniqueResult();
		
		return student;
	}

	public Student saveStudent(Session session, String name){
		
		Student student = findStudent(session, name);

		if(student == null){
			student = new Student();
			student.setName(name);
			session.save(student);
		}

		return student;
	}
	
	public Skill findSkill(Session session, String name){
		
		Query query = session.createQuery("from Student s where s.name = :name");
		query.setParameter("name", name);

		Skill skill = (Skill) query.uniqueResult();
		
		return skill;
	}

	public Skill saveSkill(Session session, String name){
		
		Skill skill = findSkill(session, name);

		if(skill == null){
			skill = new Skill();
			skill.setSkillName(name);
			session.save(skill);
		}

		return skill;
	}
	
	

	public static void main(String[] args) {
		TestAssignment testAssignment = new TestAssignment();
		testAssignment.setup();

		Session session = testAssignment.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Student student1 = testAssignment.saveStudent(session, "Dexter Fernandes");
		Student student2 = testAssignment.saveStudent(session, "Ujwal Ekka");

		Skill skill1 = testAssignment.saveSkill(session, "Hibernate");
		Skill skill2 = testAssignment.saveSkill(session, "Spring");
		
		Ranking ranking = new Ranking();
		ranking.setSubject(student1);
		ranking.setObserver(student2);
		ranking.setSkill(skill1);
		ranking.setRating(6);
		
		session.save(ranking);
		
		tx.commit();
		session.close();
	}

}
