package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Enrollment;
import org.example.repo.IEnrollmentRepo;
import org.example.repo.IGenericRepo;
import org.example.service.IEnrollmentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends CRUDImpl<Enrollment, String> implements IEnrollmentService {

    private final IEnrollmentRepo enrollmentRepo;

    @Override
    protected IGenericRepo<Enrollment, String> getRepo() {
        return enrollmentRepo;
    }
}
