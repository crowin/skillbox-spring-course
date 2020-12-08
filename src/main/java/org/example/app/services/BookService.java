package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.example.web.dto.ShelfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final ProjectRepository<Book> bookRepo;
    private final Logger logger = Logger.getLogger(BookRepository.class);

    @Autowired
    public BookService(ProjectRepository<Book> bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getAllBooks() {
        return bookRepo.retreiveAll();
    }

    public void saveBook(Book book) {
        bookRepo.store(book);
    }

    public boolean removeByRequest(ShelfFilter filter) {
        if (filter == null) {
            return false;
        }else if (filter.getId() != null) {
            return bookRepo.removeItemById(filter.getId());
        } else {
            return bookRepo.remove(filter);
        }
    }

    public List<Book> getBooksByFilter(ShelfFilter filter) {
        if (!checkFilter(filter)) return bookRepo.retreiveAll();
        return bookRepo.findBy(filter);
    }

    private boolean checkFilter(ShelfFilter filter) {
        if (filter == null) return false;

        if (filter.getTitle().isEmpty() && filter.getAuthor().isEmpty() &&
                (filter.getSize() == null || filter.getSize() == 0) &&
                (filter.getId() == null)) return false;
        return true;
    }

    private void defaultInit() {
        logger.info("default provider Init in book service bean");

    }

    private void defaultDestroy() {
        logger.info("default provider Destroy in book service bean");
    }
}
