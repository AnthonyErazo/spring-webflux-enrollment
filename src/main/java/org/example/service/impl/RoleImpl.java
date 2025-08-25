package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Role;
import org.example.repo.IGenericRepo;
import org.example.repo.IRoleRepo;
import org.example.service.IRoleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleImpl extends CRUDImpl<Role, String> implements IRoleService {
    private final IRoleRepo roleRepo;

    @Override
    protected IGenericRepo<Role, String> getRepo() {
        return roleRepo;
    }
}
