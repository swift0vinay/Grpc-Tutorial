package org.example.controller;

import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import org.example.service.BookAuthorClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class BookAuthorController {
    
    BookAuthorClientService bookAuthorClientService;
    
    @GetMapping("/author/{id}")
    public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable("id") String authorId) {
        return bookAuthorClientService.getAuthor(Integer.parseInt(authorId));
    }
    
    @GetMapping("/books/{authorId}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(@PathVariable("authorId") String authorId) throws InterruptedException {
        return bookAuthorClientService.getBooksByAuthors(Integer.parseInt(authorId));
    }
    
    @GetMapping("/books/expensive")
    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBooks() throws InterruptedException {
        return bookAuthorClientService.getExpensiveBook();
    }
    
    @GetMapping("/books/author/{gender}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getExpensiveBooks(@PathVariable("gender") String gender) throws InterruptedException {
        return bookAuthorClientService.getBooksByAuthorGender(gender);
    }
    
}