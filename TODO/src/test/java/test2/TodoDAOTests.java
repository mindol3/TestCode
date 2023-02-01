package test2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.TodoDAO;

class TodoDAOTests {

	private TodoDAO todoDAO;
	
	@BeforeEach
	public void ready() {
		todoDAO = new TodoDAO();
	}
	
	@Test
	public void testTime() throws Exception {
		
		System.out.println(todoDAO.getTime());
	}
	
//	@Test
//	public void testTime2() throws Exception {
//		
//		System.out.println(todoDAO.getTime2());
//	}

}
