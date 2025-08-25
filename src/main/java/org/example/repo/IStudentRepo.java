package org.example.repo;

import org.example.model.Student;
import reactor.core.publisher.Flux;

public interface IStudentRepo extends IGenericRepo<Student,String>{
    Flux<Student> findAllByOrderByAgeAsc();
    Flux<Student> findAllByOrderByAgeDesc();
}
