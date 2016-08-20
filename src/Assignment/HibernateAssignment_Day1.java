package Assignment;

import java.util.ArrayList;
import java.util.Iterator;
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

public class HibernateAssignment_Day1 {

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

	/* Determine the actual ranking for each person */
	public void getActualRating(Session session){

		Query query = session.createQuery("select r.subject.name, avg(r.rating)"
				+ " from Ranking r"
				+ " group by r.subject");

		List studentsList = query.list();

		for(int i = 0; i < studentsList.size(); i++){
			Object[] row = (Object[]) studentsList.get(i);
			System.out.print("\nName: " + row[0] + "; Average Rating: " + row[1]);
		}

	}

	public void getAllSkills(Session session){

		Query query = session.createQuery("from Skill");
		List<Skill> skills = query.list();

		for (Skill skill : skills) {
			System.out.print("Skill Id: " + skill.getSkillId() + "; Skill Name: " + skill.getSkillName() + "\n");
		}

	}

	/* Student ranked “the best” for a given skill */
	public void findToppersPerSkill(Session session, int skillId){

		Query query = session.createQuery("select r.subject.name, r.skill.skillName, avg(r.rating)"
				+ " from Ranking r"
				+ " where r.skill.skillId = :skill"
				+ " group by r.subject");
		query.setParameter("skill", skillId);

		double topperRating = 0, oldTopperRating = 0;
		List<Integer> topperList = null;

		List studentsList = query.list();

		if(studentsList != null){
			if(studentsList.size() > 0){
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
			} else {
				System.out.println("No students have been rated for the particular skill / Invalid input");
			}
		} else {
			System.out.println("Invalid input");
		}

	}

	public static void main(String[] args) {

		HibernateAssignment_Day1 hibernateAssignment = new HibernateAssignment_Day1();
		hibernateAssignment.setup();

		Session session = hibernateAssignment.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		int input = 0;
		Scanner reader = new Scanner(System.in);

		System.out.println("1. Add Student\n2. Add Skill\n3. Add a rating\n4. Update a rating");
		System.out.println("5: Overall Rating for all\n6: Find the topper in a particular skill");
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

		case 5 : hibernateAssignment.getActualRating(session);
		break;

		case 6 : hibernateAssignment.getAllSkills(session);
		System.out.println("Enter the skill id for which you need to find the topper for: ");
		input = reader.nextInt();
		hibernateAssignment.findToppersPerSkill(session, input);
		break;

		default: System.out.println("Wrong option selected");
		break;

		}

		tx.commit();
		session.close();

	}

}
