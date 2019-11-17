package com.ProjectCC.dero.service;

import com.ProjectCC.dero.dto.ClinicDTO;
import com.ProjectCC.dero.model.Clinic;
import com.ProjectCC.dero.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {


    private ClinicRepository clinicRepository;

    @Autowired
    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }

    public Clinic save(Clinic clinic){
        return clinicRepository.save(clinic);
    }

    public void remove(Long id){
        clinicRepository.deleteById(id);
    }

    public Clinic findOne(Long id) {
        return clinicRepository.findById(id).orElseGet(null);
    }

    public void update(ClinicDTO clinic){
       clinicRepository.update(clinic.getName(), clinic.getAddress(), clinic.getDescription(), clinic.getId());
    }
}