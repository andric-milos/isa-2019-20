package com.ProjectCC.dero.service;

import com.ProjectCC.dero.dto.*;
import com.ProjectCC.dero.model.*;
import com.ProjectCC.dero.repository.ClinicRepository;
import com.ProjectCC.dero.repository.ExaminationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClinicService {

    private ModelMapper modelMapper;
    private ExaminationRepository examinationRepository;
    private ClinicRepository clinicRepository;

    @Autowired
    public ClinicService(ClinicRepository clinicRepository, ExaminationRepository examinationRepository, ModelMapper modelMapper) {
        this.clinicRepository = clinicRepository;
        this.examinationRepository = examinationRepository;
        this.modelMapper = modelMapper;
    }

    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }

    public Clinic save(Clinic clinic){
        
        Clinic clinic_find = clinicRepository.findByName(clinic.getName());
        if(clinic_find == null){
            return clinicRepository.save(clinic);
        }

        return null;
    }

    public void remove(Long id){
        clinicRepository.deleteById(id);
    }

    public Clinic findOne(Long id) {
        return clinicRepository.findById(id).orElseGet(null);
    }

    public ClinicDTO update(ClinicDTO clinicDTO) {
        Optional<Clinic> clinic_find = clinicRepository.findById(clinicDTO.getId());
        Clinic clinic = clinic_find.get();
        clinic.setName(clinicDTO.getName());
        clinic.setAddress(clinicDTO.getAddress());
        clinic.setDescription(clinicDTO.getDescription());

        if (clinic != null) {
            clinic = clinicRepository.save(clinic);
            return modelMapper.map(clinic, ClinicDTO.class);
        } else return null;
    }

    public Clinic findByName(String name) {
        return clinicRepository.findByName(name);
    }

    public ResponseEntity<ClinicDTO> findById(Long id) {

        Optional<Clinic> opt = clinicRepository.findById(id);
        Clinic clinic = opt.get();

//        return cinemaStore.findAllCinemas(pageable).map(cinema -> modelMapper.map(cinema, CinemaDto.class));

        List<MedicalStaffDTO> staff = new ArrayList<>();
        for (MedicalStaff s : clinic.getMedicalStaff()) {
            staff.add(modelMapper.map(s, MedicalStaffDTO.class));
//            staff.add(MedicalStaffDTO.builder()
//                    .firstName(s.getFirstName())
//                    .lastName(s.getLastName())
//                    .address(s.getAddress())
//                    .city(s.getCity())
//                    .country(s.getCountry())
//                    .email(s.getEmail())
//                    .id(s.getId())
//                    .jmbg(s.getJmbg())
//                    .telephone(s.getTelephone())
//                    .build());
        }
        List<RoomDTO> roomDTOS = new ArrayList<>();
        for (Room r : clinic.getRooms()) {
            roomDTOS.add(RoomDTO.builder()
            .name(r.getName())
            .number(r.getNumber())
            .id(r.getId())
            .build());
        }

        ClinicDTO dto = ClinicDTO.builder()
                .name(clinic.getName())
                .description(clinic.getDescription())
                .address(clinic.getAddress())
                .id(clinic.getId())
                .medicalStaff(staff)
                .rooms(roomDTOS)
                .build();

        if (clinic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public ResponseEntity<ClinicDTO> businessReport(Long id) {
        Optional<Clinic> opt = this.clinicRepository.findById(id);
        Clinic clinic = opt.get();
        ArrayList<MedicalStaffDTO> docs = new ArrayList<MedicalStaffDTO>();
        for (MedicalStaff d : clinic.getMedicalStaff()) {
            if (d instanceof Doctor) {
                docs.add(DoctorDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .grade(((Doctor) d).getGrade()).build());
            }
        }

        Optional<List<Examination>> opts = Optional.of(this.examinationRepository.findAll());
        List<Examination> examinations = opts.get();
        ArrayList<ExaminationDTO> exams = new ArrayList<>();
        for (Examination e : examinations) {
            if (e.getClinic().equals(clinic)) {
                exams.add(ExaminationDTO.builder()
                        .date(e.getDate())
                        .build());
            }
        }
        // Get examinations
        ClinicDTO clinicDTO = ClinicDTO.builder()
                .grade(clinic.getGrade())
                .medicalStaff(docs)
                .income(clinic.getIncome())
                .examinations(exams)
                .build();
        return new ResponseEntity<>(clinicDTO, HttpStatus.OK);
    }
}
