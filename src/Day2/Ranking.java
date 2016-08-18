package Day2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Ranking {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int rankId;
	
	@ManyToOne
	private Student subject;
	
	@ManyToOne
	private Student observer;
	
	@ManyToOne
	private Skill skill;
	
	@Column
	private int rating;
	
	public Ranking() {
		super();
	}

	public int getRankId() {
		return rankId;
	}

	public void setRankId(int rankId) {
		this.rankId = rankId;
	}

	public Student getSubject() {
		return subject;
	}

	public void setSubject(Student subject) {
		this.subject = subject;
	}

	public Student getObserver() {
		return observer;
	}

	public void setObserver(Student observer) {
		this.observer = observer;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
}
