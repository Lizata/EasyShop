package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.springframework.security.access.annotation.Secured;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
// http://localhost:8080/categories
//@RequestMapping("categories")
// add annotation to allow cross site origin requests
@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // add the appropriate annotation for a get action
    @RequestMapping(path = "/categories", method = RequestMethod.GET)
    public List<Category> getAll() {
        // find and return all categories
        List<Category> categories = categoryDao.getAllCategories();
        return categories;
    }


    // add the appropriate annotation for a get action
    @RequestMapping(path = "/categories/{categoryId}", method = RequestMethod.GET)
    public Category getById(@PathVariable(name = "categoryId") int id, HttpServletResponse response) {
        // get the category by id
        Category category = categoryDao.getById(id);
        if (category == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return category;

    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products

    @RequestMapping(path = "/categories/{categoryId}/products", method = RequestMethod.GET)
    public List<Product> getProductsById(@PathVariable(name = "categoryId") int categoryId, HttpServletResponse response) {
        // get a list of product by categoryId
        List<Product> p = productDao.listByCategoryId(categoryId);

        if (p == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return p;
    }

    // add annotation to call this method for a POST action

    // add annotation to ensure that only an ADMIN can call this function
    @Secured("ROLE_ADMIN")
    @RequestMapping(path = "/categories", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category) {
        // insert the category
        System.out.println("Incoming category: " + category);

        Category c = categoryDao.create(category);
        System.out.println("Returned category: " + c);

        return c;
    }


    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("/categories/{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @Secured("ROLE_ADMIN")
    public void updateCategory(@PathVariable(name="categoryId") int id, @RequestBody Category category) {
        // update the category by id
        categoryDao.update(id, category);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    @DeleteMapping("/categories/{categoryId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    // add annotation to ensure that only an ADMIN can call this function
    @Secured("ROLE_ADMIN")
    public void deleteCategory(@PathVariable(name="categoryId") int id) {
        // delete the category by id
        categoryDao.delete(id);
    }
}

