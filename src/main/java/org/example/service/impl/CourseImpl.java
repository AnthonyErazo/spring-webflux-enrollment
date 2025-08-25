package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Course;
import org.example.repo.ICourseRepo;
import org.example.repo.IGenericRepo;
import org.example.service.ICourseService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseImpl extends CRUDImpl<Course, String> implements ICourseService {

    private final ICourseRepo repo;

    @Override
    protected IGenericRepo<Course, String> getRepo() {
        return repo;
    }
}
