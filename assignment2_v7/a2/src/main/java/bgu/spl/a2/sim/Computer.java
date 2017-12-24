package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {
	public SuspendingMutex compMutex;
	String computerType;
	long failSig;
	long successSig;

	public Computer(String computerType) {
		this.computerType = computerType;
		this.compMutex = new SuspendingMutex(this);
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		for (String courseName: courses){
			if(coursesGrades.get(courseName) <56)
				return getFailSig();
		}
		return getSuccessSig();
	}

	public long getFailSig() {
		return failSig;
	}

	public long getSuccessSig() {
		return successSig;
	}

	public void setFailSig(long failSig){
		this.failSig = failSig;
	}

	public void setSuccessSig(long successSig){
		this.successSig = successSig;
	}

	public String getComputerType() {
		return computerType;
	}

	public SuspendingMutex getCompMutex() {
		return compMutex;
	}
}


