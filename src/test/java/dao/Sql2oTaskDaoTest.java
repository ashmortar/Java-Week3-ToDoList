package dao;

import models.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o; //must be sql2o class conn

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
        Task task = new Task("mow the lawn", 1);
        int originalTaskId = task.getId();
        taskDao.add(task);
        assertNotEquals(originalTaskId, task.getId());
    }

    @Test
    public void existingTasksCanBeFoundById() throws Exception {
        Task task = new Task("mow the lawn", 1);
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
        Task task = new Task("chore", 1);
        Task otherTask = new Task("another chore", 1);
        taskDao.add(task);
        taskDao.add(otherTask);
        assertEquals(2, taskDao.getAll().size());
    }

    @Test
    public void updateTask() throws Exception {
        Task task = new Task("some chore", 1);
        taskDao.add(task);
        taskDao.update(task.getId(), "test");
        Task updatedTask = taskDao.findById(task.getId());
        assertEquals("test", updatedTask.getDescription());
    }

    @Test
    public void deleteById() throws Exception {
        Task task = new Task("test1", 1);
        taskDao.add(task);
        taskDao.deleteById(task.getId());
        assertEquals(0, taskDao.getAll().size());

    }

    @Test
    public void clearAllTasks() throws Exception {
        Task task = new Task("do a thing", 1);
        Task otherTask = new Task("do another thing", 1);
        taskDao.add(task);
        taskDao.add(otherTask);
        taskDao.clearAllTasks();
        assertEquals(0, taskDao.getAll().size());
    }

    @Test
    public void categoryIdISREtrunedCorreclty() throws Exception {
        Task task = new Task("mop the floor", 1);
        int origininalCatId = task.getCategoryId();
        taskDao.add(task);
        assertEquals(origininalCatId, taskDao.findById(task.getId()).getCategoryId());
    }
}
