package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.example.web.dto.ShelfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookRepository implements ProjectRepository<Book> {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private ApplicationContext context;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(ApplicationContext context, NamedParameterJdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> retreiveAll() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", getBookRowMapper());
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTemplate.update("INSERT INTO books(author, title, size) VALUES(:author, :title, :size)", parameterSource);
        logger.info("store new book: " + book);
        //repo.add(book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", bookIdToRemove);
        int count = jdbcTemplate.update("DELETE FROM books WHERE id =:id", parameterSource);
        return count != 0;
    }

    @Override
    public List<Book> findBy(ShelfFilter filter) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", filter.getAuthor());
        parameterSource.addValue("title", filter.getTitle());
        parameterSource.addValue("size", filter.getSize());
        String selectQueue = buildQueueBySelect(filter, "SELECT *");

        if (selectQueue == null) return retreiveAll();

        List<Book> books = jdbcTemplate.query(selectQueue, parameterSource, getBookRowMapper());
        return new ArrayList<>(books);
    }

    @Override
    public boolean remove(ShelfFilter filter) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", filter.getAuthor());
        parameterSource.addValue("title", filter.getTitle());
        parameterSource.addValue("size", filter.getSize());
        String removeQueue = buildQueueBySelect(filter, "DELETE");

        if (removeQueue == null) return false;
        return jdbcTemplate.update(removeQueue, parameterSource) != 0;
    }

    private String buildQueueBySelect(ShelfFilter filter, String type) {
        String queue = null;
        if (!filter.getAuthor().isEmpty() && !filter.getAuthor().isEmpty() && filter.getSize() != null) {
            queue = "%s FROM books WHERE author =:author AND title =:title AND size =:size";
        } else if (!filter.getTitle().isEmpty() && !filter.getAuthor().isEmpty() && filter.getSize() == null) {
            queue =  "%s FROM books WHERE author =:author AND title =:title";
        } else if (!filter.getAuthor().isEmpty() && filter.getAuthor().isEmpty() && filter.getSize() != null) {
            queue =  "%s FROM books WHERE size =:size AND title =:title";
        } else if (filter.getAuthor().isEmpty() && !filter.getAuthor().isEmpty() && filter.getSize() != null) {
            queue =  "%s FROM books WHERE size =:size AND author =:author";
        } else if (!filter.getAuthor().isEmpty()) {
            queue =  "%s FROM books WHERE author =:author";
        } else if (filter.getSize() != null) {
            queue =  "%s FROM books WHERE size =:size";
        } else if (!filter.getTitle().isEmpty()) {
            queue =  "%s FROM books WHERE title =:title";
        }
        if (queue == null) return null;
        else return String.format(queue, type.toUpperCase());
    }

    private RowMapper<Book> getBookRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        };
    }

    private void defaultInit() {
        logger.info("default provider Init in book repo bean");
    }

    private void defaultDestroy() {
        logger.info("default provider Destroy in book repo bean");
    }
}
