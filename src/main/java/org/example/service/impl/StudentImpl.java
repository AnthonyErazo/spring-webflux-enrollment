package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Student;
import org.example.repo.IGenericRepo;
import org.example.repo.IStudentRepo;
import org.example.service.IStudentService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentImpl extends CRUDImpl<Student, String> implements IStudentService{

    private final IStudentRepo repo;

    @Override
    protected IGenericRepo<Student, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Student> findAllByOrderByAgeAsc() {
        return repo.findAllByOrderByAgeAsc();
    }

    @Override
    public Flux<Student> findAllByOrderByAgeDesc() {
        return repo.findAllByOrderByAgeDesc();
    }
}
