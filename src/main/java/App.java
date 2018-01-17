import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Sql2oCategoryDao;
import dao.Sql2oTaskDao;
import dao.TaskDao;
import models.Category;
import models.Task;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {


    public static void main(String[] args) {
        staticFileLocation("/public");
        String connectionString = "jdbc:h2:~/todolist.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oTaskDao taskDao = new Sql2oTaskDao(sql2o);
        Sql2oCategoryDao categoryDao = new Sql2oCategoryDao(sql2o);
        if (categoryDao.getAll().size() == 0){
            categoryDao.add(new Category("default"));
        }

        //get: delete all tasks

        get("/tasks/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            taskDao.clearAllTasks();
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());


        //get: show new task form
        get("/tasks/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Category> categories = categoryDao.getAll();
            model.put("categories", categories);

            return new ModelAndView(model, "task-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process new task form
        post("/tasks/new", (request, response) -> { //URL to make new task on POST route
            Map<String, Object> model = new HashMap<>();
            String description = request.queryParams("description");
            int categoryId = Integer.parseInt(request.queryParams("categoryId"));
            Task newTask = new Task(description, categoryId);
            taskDao.add(newTask);
            model.put("task", newTask);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show all tasks
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Task> tasks = taskDao.getAll();
            model.put("tasks", tasks);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show an individual task
        get("/tasks/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToFind = Integer.parseInt(req.params("id")); //pull id - must match route segment
            Task foundTask = taskDao.findById(idOfTaskToFind); //use it to find task
            model.put("task", foundTask); //add it to model for template to display
            return new ModelAndView(model, "task-detail.hbs"); //individual task page.
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a task
        get("/tasks/:id/update", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToEdit = Integer.parseInt(req.params("id"));
            Task editTask = taskDao.findById(idOfTaskToEdit);
            model.put("editTask", editTask);
            return new ModelAndView(model, "task-form.hbs");
        }, new HandlebarsTemplateEngine());

        //task: process a form to update a task
        post("/tasks/:id/update", (req, res) -> { //URL to make new task on POST route
            Map<String, Object> model = new HashMap<>();
            String newContent = req.queryParams("description");
            int idOfTaskToEdit = Integer.parseInt(req.params("id"));
            Task editTask = taskDao.findById(idOfTaskToEdit);
            taskDao.update(idOfTaskToEdit, newContent);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: delete an individual task
        get("/tasks/:id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfTaskToDelete = Integer.parseInt(req.params("id")); //pull id - must match route segment
            Task deleteTask = taskDao.findById(idOfTaskToDelete); //use it to find task
            taskDao.deleteById(idOfTaskToDelete);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: page with a list of categories
        get("/categories", (req, res) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            List<Category> categories = categoryDao.getAll();
            model.put("categories", categories);
            return new ModelAndView(model, "categories.hbs");
        }, new HandlebarsTemplateEngine());


        get("/categories/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();

            return new ModelAndView(model, "category-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/categories/new", (req, res) -> {
            Map<String, Object> model = new HashMap<String, Object>();

            String name = req.queryParams("categoryName");
            Category category = new Category(name);
            categoryDao.add(category);

            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show all tasks in a category
        get("/categories/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idofCategoryToFind = Integer.parseInt(req.params("id"));
            List<Task> tasksInCategory = categoryDao.getAllTasksByCategory(idofCategoryToFind);
            Category category = categoryDao.findById(idofCategoryToFind);
            model.put("category", category);
            model.put("tasksInCategory", tasksInCategory);
            return new ModelAndView(model, "category-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get update a category
        get("/categories/:id/update", (req, res)-> {
            Map<String, Object> model = new HashMap<>();
            int idOfCategorytoEdit = Integer.parseInt(req.params("id"));
            Category editCategory = categoryDao.findById(idOfCategorytoEdit);
            model.put("editCategory", editCategory);
            return new ModelAndView(model, "category-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process update category form
        post("/categories/:id/update", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String newName = req.queryParams("categoryName");
            int idOfCategorytoEdit = Integer.parseInt(req.params("id"));
            categoryDao.update(idOfCategorytoEdit, newName);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());
    }
}
