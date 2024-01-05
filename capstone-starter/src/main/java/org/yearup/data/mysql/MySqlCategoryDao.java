package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> results = new ArrayList<>();

        // get all categories
        String sql = """
                select * from categories;
                """;

        // TODO - need a Connection, PreparedStatement, ResultSet

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while( rs.next()) {
                Category c = mapRow(rs);
                results.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    @Override
    public Category getById(int categoryId)
    {
        Category result = null;

        String sql = """
                select * from categories where category_id = ?;
                """;
        // get category by id
        try (Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,categoryId);
            ResultSet rs = ps.executeQuery();

            while( rs.next()) {
                result = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Category create(Category category)
    {
        Category result = category;
        String sql = """              
                insert into categories values (?, ?, ?);
                """;
        // create a new category
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, category.getCategoryId());
            ps.setString(2,category.getName());ps.setString(3, category.getDescription());
            ps.executeUpdate();
            // Get the generated id
            int newCategoryId;
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            newCategoryId = rs.getInt(1);
            result.setCategoryId(newCategoryId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        String sql = """
            delete from categories where category_id = ?;
            """;
        // delete category
        try (Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,categoryId);
            ps.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
