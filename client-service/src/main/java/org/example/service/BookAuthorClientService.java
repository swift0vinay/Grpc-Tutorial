package org.example.service;

import com.google.protobuf.Descriptors;
import com.myrpc.Author;
import com.myrpc.Book;
import com.myrpc.BookAuthorServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.TempDb;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class BookAuthorClientService {
    
    @GrpcClient("vj-service")
    BookAuthorServiceGrpc.BookAuthorServiceBlockingStub syncClient;
    
    @GrpcClient("vj-async-service")
    BookAuthorServiceGrpc.BookAuthorServiceStub asynClient;
    
    public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId) {
        Author authorReq = Author.newBuilder().setAuthorId(authorId).build();
        Author authorRes = syncClient.getAuthor(authorReq);
        return authorRes.getAllFields();
    }
    
    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthors(int authorId) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Author request = Author.newBuilder().setAuthorId(authorId).build();
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asynClient.getBooksByAuthor(request, new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }
            
            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }
            
            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyList();
    }
    
    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Map<String, Map<Descriptors.FieldDescriptor, Object>> response = new HashMap<>();
        
        StreamObserver<Book> responseObs = asynClient.getExpensiveBook(new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.put("Expensive book", book.getAllFields());
            }
            
            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }
            
            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
        
        TempDb.getBooksFromTempDb().forEach(responseObs::onNext);
        responseObs.onCompleted();
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyMap();
    }
    
    
    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthorGender(String gender) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        StreamObserver<Book> responseObserver = asynClient.getBookByGender(new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }
            
            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }
            
            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
        
        TempDb.getAuthorsFromTempDb()
                .stream()
                .filter(author -> author.getGender().equalsIgnoreCase(gender))
                .forEach(author -> responseObserver.onNext(Book.newBuilder().setAuthorId(author.getAuthorId()).build()));
        responseObserver.onCompleted();
        
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyList();
    }
    
}