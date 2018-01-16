package dao;

import models.Category;
import models.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;


public class Sql2oCategoryDaoTest {
    private Sql2oCategoryDao categoryDao;
    private Sql2oTaskDao taskDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception{
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        categoryDao = new Sql2oCategoryDao(sql2o);

        //keep connection open through entire test so it does not get erased
        conn = sql2o.open();

    }

    @After
    public void tearDowns() throws Exception {
        conn.close();
    }

    @Test
    public void addingCategorySetsId() throws Exception {
        Category category = new Category("work");
        int originalCategoryId = category.getId();
        categoryDao.add(category);
        assertNotEquals(originalCategoryId, category.getId());
    }

    @Test
    public void existingCategoriesCanBeFoundById() throws Exception {
        Category category = new Category("work");
        categoryDao.add(category);
        Category foundCategory = categoryDao.findById(category.getId());
        assertEquals(category, foundCategory); //should be the same
    }

    @Test
    public void getAll_findsNoTasksWhenNoneArePresent_true() throws Exception {
        assertEquals(0, categoryDao.getAll().size());
    }

    @Test
    public void getAll_returnsAllInstancesOfTask_true() throws Exception {
        Category category = new Category("work");
        Category otherCategory = new Category("school");
        categoryDao.add(category);
        categoryDao.add(otherCategory);
        assertEquals(2, categoryDao.getAll().size());
    }

    @Test
    public void updateTask() throws Exception {
        Category category = new Category("work");
        categoryDao.add(category);
        categoryDao.update(category.getId(), "test");
        Category updatedCategory = categoryDao.findById(category.getId());
        assertEquals("test", updatedCategory.getName());
    }

    @Test
    public void deleteById() throws Exception {
        Category category = new Category("work");
        categoryDao.add(category);
        categoryDao.deleteById(category.getId());
        assertEquals(0, categoryDao.getAll().size());

    }

    @Test
    public void clearAllCategories() throws Exception {
      Category category = new Category("work");
      Category otherCategory = new Category("school");
        categoryDao.add(category);
        categoryDao.add(otherCategory);
        categoryDao.clearAllCategories();
        assertEquals(0, categoryDao.getAll().size());
    }

}