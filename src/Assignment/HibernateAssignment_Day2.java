package Assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import Day2.Ranking;
import Day2.Skill;
import Day2.Student;

public class HibernateAssignment_Day2 {

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
		
		Query query = session.createQuery("from Skill s where s.skillName = :name");
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
	
	public void createData(Session session, String subjectName, String observerName, String skillName, int rank){
		
		Student subject = saveStudent(session, subjectName);
		Student observer = saveStudent(session, observerName);
		
		Skill skill = saveSkill(session, skillName);
		
		Ranking ranking = new Ranking();
		ranking.setSubject(subject);
		ranking.setObserver(observer);
		ranking.setSkill(skill);
		ranking.setRating(rank);
		
		session.save(ranking);
		
	}
	
	public void changeRank(Session session, String subjectName, String observerName, String skillName, int newRating){
		
		Query query = session.createQuery("from Ranking r"
				+ " where r.subject.name= :subject"
				+ " and r.observer.name= :observer"
				+ " and r.skill.skillName= :skill");
		query.setString("subject", subjectName);
		query.setString("observer", observerName);
		query.setString("skill", skillName);
		
		Ranking ranking = (Ranking) query.uniqueResult();
		if(ranking == null){
			System.out.println("Invalid Change Request");
		}
		
		ranking.setRating(newRating);
		
	}
	
	/* Use Case 1 - Implement Remove Ranking */
	public void removeRanking(Session session, String subjectName, String observerName, String skillName){
		
		Query query = session.createQuery("from Ranking r"
				+ " where r.subject.name= :subject"
				+ " and r.observer.name= :observer"
				+ " and r.skill.skillName= :skill");
		
		query.setString("subject", subjectName);
		query.setString("observer", observerName);
		query.setString("skill", skillName);
		
		Ranking ranking = (Ranking) query.uniqueResult();
		
		if(ranking != null){
			session.delete(ranking);
			System.out.println("\nRating for "+subjectName+" with the skill "+skillName+" has been deleted successfully\n");
		}else{
			System.out.println("Invalid inputs");
		}
		
	}
	
	/* Use Case 2 - Get Average for each student for each skill */
	public void findAverage(Session session){
		
		Query query = session.createQuery("select r.subject.name, r.skill.skillName, avg(r.rating)"
				+ " from Ranking r"
				+ " group by r.subject, r.skill");
		
		List studentsList = query.list();

		for(int i = 0; i < studentsList.size(); i++){
			Object[] row = (Object[]) studentsList.get(i);
			System.out.print("\nName: " + row[0] + "; Skill: " + row[1] + "; Average Rating: " + row[2]);
		}
		
	}
	
	/* Use Case 3 - Find Topper */
	public void findTopper(Session session){
		
		Query query = session.createQuery("select r.subject.name, r.skill.skillName, avg(r.rating) as Average"
				+ " from Ranking r"
				+ " group by r.subject, r.skill");
		
		double topperRating = 0, oldTopperRating = 0;
		List<Integer> topperList = null;
		
		List studentsList = query.list();

		for(int i = 0; i < studentsList.size(); i++){
			Object[] row = (Object[]) studentsList.get(i);
			topperRating = (Double)row[2];
			if(topperRating > oldTopperRating){
				topperList = new ArrayList<Integer>();
				topperList.add(i);
				oldTopperRating = topperRating;
			}else if(topperRating == oldTopperRating){
				topperList.add(i);
			}
		}
		
		for(int subjectId : topperList){
			Object[] row = (Object[]) studentsList.get(subjectId);
			System.out.print("\nName: " + row[0] + "; Skill: " + row[1] + "; Average Rating: " + row[2]);
		}
		
	}
	
	/* Use Case 4 - Sort students based on rank of certain skill */
	public void sortOnRankForSkill(Session session){
		
		Query query = session.createQuery("select r.subject.name, r.skill.skillName, avg(r.rating) as Average"
				+ " from Ranking r"
				+ " group by r.subject, r.skill"
				+ " order by r.skill, Average desc");
		
		List studentsList = query.list();

		for(int i = 0; i < studentsList.size(); i++){
			Object[] row = (Object[]) studentsList.get(i);
			System.out.print("\nName: " + row[0] + "; Skill: " + row[1] + "; Average Rating: " + row[2]);
		}
		
	}
	
	public static void main(String[] args) {
		
		HibernateAssignment_Day2 hibernateAssignment = new HibernateAssignment_Day2();
		hibernateAssignment.setup();

		Session session = hibernateAssignment.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		int input = 0;
		Scanner reader = new Scanner(System.in);
		
		System.out.println("1. Add Student\n2. Add Skill\n3. Add a rating\n4. Update a rating\n5. Remove a rating");
		System.out.println("6: Average of each student for each skill\n7: Find the topper\n8: Sort the students based on the rank of certain skill");
		System.out.println("Please choose the action: ");
		input = reader.nextInt();
		
		switch (input) {
		case 1 : hibernateAssignment.saveStudent(session, "Dexter Fernandes");
				 break;
				 
		case 2 : hibernateAssignment.saveSkill(session, "Hibernate");
				 break;
				 
		case 3 : hibernateAssignment.createData(session, "Amit Palekar", "Prasant Senapati", "Java", 5);
				 hibernateAssignment.createData(session, "Amit Palekar", "Robin Gonsalves", "MySQL", 8);
				 hibernateAssignment.createData(session, "Amit Palekar", "Robin Gonsalves", "Java", 7);
				 hibernateAssignment.createData(session, "Amit Palekar", "Prasant Senapati", "MySQL", 4);
				 hibernateAssignment.createData(session, "Robin Gonsalves", "Amit Palekar", "MySQL", 8);
				 hibernateAssignment.createData(session, "Robin Gonsalves", "Amit Palekar", "Java", 3);
				 hibernateAssignment.createData(session, "Robin Gonsalves", "Prasant Senapati", "MySQL", 8);
				 hibernateAssignment.createData(session, "Robin Gonsalves", "Prasant Senapati", "Java", 8);
				 break;
				 
		case 4 : hibernateAssignment.changeRank(session, "Binay Das", "Prasant Senapati", "Java", 4);
				 break;
				 
		case 5 : hibernateAssignment.removeRanking(session, "Robin Gonsalves", "Prasant Senapati", "MySQL");
				 break;
				 
		case 6 : hibernateAssignment.findAverage(session);
				 break;
				 
		case 7 : hibernateAssignment.findTopper(session);
		 		 break;
		 		 
		case 8 : hibernateAssignment.sortOnRankForSkill(session);
		 		 break;
		 		 
		default: System.out.println("Wrong option selected");
				 break;
				 
		}
		
		tx.commit();
		session.close();
		
	}

}
