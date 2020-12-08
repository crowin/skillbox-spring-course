package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.helpers.FilesHelper;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.ShelfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping(value = "/books")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;
    private FilesHelper filesHelper;

    @Autowired
    public BookShelfController(BookService bookService, FilesHelper filesHelper) {
        this.bookService = bookService;
        this.filesHelper = filesHelper;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info("got book shelf");
        formModelWithBooks(model);
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book != null ? book : new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            model.addAttribute("shelfFilter", new ShelfFilter());

            return "book_shelf";
        } else {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid ShelfFilter shelfFilter, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        }

        if (bookService.removeByRequest(shelfFilter)) {
            return "redirect:/books/shelf";
        } else {
            bindingResult.addError(new ObjectError("bookNotFound",
                    "Book wasn't found by author name and/or book title"));
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());

            return "book_shelf";
        }
    }

    @GetMapping("/filter")
    public String filterBook(Model model, @RequestParam("author") String author, @RequestParam("title") String title,
                             @RequestParam("size") Integer size) {
        ShelfFilter filter = new ShelfFilter();
        filter.setTitle(title);
        filter.setAuthor(author);
        filter.setSize(size);

        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getBooksByFilter(filter));
        model.addAttribute("shelfFilter", new ShelfFilter());

        return "book_shelf";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            formModelWithBooks(model);
            model.addAttribute("nullFileUpload", true);
            return "book_shelf";
        } else model.asMap().remove("nullFileUpload");

        String name = file.getOriginalFilename();
        byte[] bytes = file.getBytes();

        //create dir
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "external_upload");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //create file
        File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();
        filesHelper.addNewImagePath(serverFile.getAbsolutePath());

        logger.info("new file saved at: " + serverFile.getAbsolutePath());

        return "redirect:/books/shelf";
    }

    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam(value = "file") String fileName, HttpServletResponse response) throws IOException {
        String rootPath = System.getProperty("catalina.home");
        Path filePath = Paths.get(rootPath + File.separator + "external_upload" + File.separator + fileName);
        if (!fileName.isEmpty() && filesHelper.getImagesPaths().contains(filePath.toString())) {
            response.setContentType("image/" + StringUtils.getFilenameExtension(filePath.toString()));
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } else {
            response.sendRedirect("./shelf");
            logger.error("File didn't find or user didn't fill file's name");
        }

    }

    private void formModelWithBooks(Model model) {
        model.addAttribute("book", new Book());
        if (model.getAttribute("bookList") == null) {
            model.addAttribute("bookList", bookService.getAllBooks());
        }
        model.addAttribute("shelfFilter", new ShelfFilter());
    }
}
