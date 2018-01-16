package dao;

import models.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o; //must be sql2o class conn

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oTaskDaoTest {
    private Sql2oTaskDao taskDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception{
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        taskDao = new Sql2oTaskDao(sql2o);

        //keep connection open through entire test so it does not get erased
        conn = sql2o.open();

    }

    @After
    public void tearDowns() throws Exception {
        conn.close();
    }

    @Test
    public void addingCourseSetsId() throws Exception {
        Task task = new Task("mow the lawn");
        int originalTaskId = task.getId();
        taskDao.add(task);
        assertNotEquals(originalTaskId, task.getId());
    }

    @Test
    public void existingTAsksCanBeFoundById() throws Exception {
        Task task = new Task("mow the law");
        taskDao.add(task); //add to dao (takes care of saving)
        Task foundTask = taskDao.findById(task.getId()); //retrieve
        assertEquals(task, foundTask); //should be the same
    }

    @Test
    public void getAll_findsNoTasksWhenNoneArePresent_true() throws Exception {
        assertEquals(0, taskDao.getAll().size());
    }

    @Test
    public void getAll_returnsAllInstancesOfTask_true() throws Exception {
        Task task = new Task("chore");
        Task otherTask = new Task("another chore");
        taskDao.add(task);
        taskDao.add(otherTask);
        assertEquals(2, taskDao.getAll().size());
    }
}
