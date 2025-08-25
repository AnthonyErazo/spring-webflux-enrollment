package org.example.service;

import org.example.model.Student;
import reactor.core.publisher.Flux;

public interface IStudentService extends ICRUD<Student,String>{
    Flux<Student> findAllByOrderByAgeAsc();
    Flux<Student> findAllByOrderByAgeDesc();
}
