package org.example.service;

import com.google.protobuf.Descriptors;
import com.myrpc.Author;
import com.myrpc.Book;
import com.myrpc.BookAuthorServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
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
    
}