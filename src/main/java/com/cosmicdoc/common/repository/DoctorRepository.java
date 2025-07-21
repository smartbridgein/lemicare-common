package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Doctor;
import java.util.Optional;

public interface DoctorRepository extends BaseRepository<Doctor, String> {
    Optional<Doctor> findByEmail(String email);
}
