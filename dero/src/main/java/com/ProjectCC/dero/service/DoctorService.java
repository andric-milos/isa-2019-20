package com.ProjectCC.dero.service;

import com.ProjectCC.dero.dto.ClinicDTO;
import com.ProjectCC.dero.dto.DoctorDTO;
import com.ProjectCC.dero.model.Authority;
import com.ProjectCC.dero.model.Clinic;
import com.ProjectCC.dero.model.Doctor;
import com.ProjectCC.dero.model.TypeOfExamination;
import com.ProjectCC.dero.repository.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private ClinicService clinicService;
    private TypeOfExaminationService typeOfExaminationService;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private AuthorityService authorityService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, ClinicService clinicService,
                         TypeOfExaminationService typeOfExaminationService, ModelMapper modelMapper,
                         PasswordEncoder passwordEncoder, AuthorityService authorityService) {
        this.doctorRepository = doctorRepository;
        this.clinicService = clinicService;
        this.typeOfExaminationService = typeOfExaminationService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
    }

    public ResponseEntity<DoctorDTO> save(DoctorDTO doctorDTO, String type) {
        TypeOfExamination typeOfExamination = this.typeOfExaminationService.findByName(type);
        Doctor doctor = modelMapper.map(doctorDTO, Doctor.class);
        doctor.setSpecialisedType(typeOfExamination);

        doctor.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        List<Authority> authorities = authorityService.findByName("ROLE_DOCTOR");
        doctor.setAuthorities(authorities);

        Clinic clinic = clinicService.findOne((long) 1);
        doctor.setClinic(clinic);

        doctor = doctorRepository.save(doctor);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<Void> delete(Long id) {
        doctorRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<DoctorDTO>> findAll() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorDTO> doctorsDTO = new ArrayList<>();

        for (Doctor d : doctors) {
            doctorsDTO.add(DoctorDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .jmbg(d.getJmbg())
                .country(d.getCountry())
                .city(d.getCity())
                .address(d.getAddress())
                .telephone(d.getTelephone())
                .clinic(ClinicDTO.builder().name(d.getClinic().getName()).build())
                .build());
        }

        return new ResponseEntity<>(doctorsDTO, HttpStatus.OK);
    }

    public ResponseEntity<List<DoctorDTO>> searchDoctors(String firstName,
                                                         String lastName,
                                                         String email,
                                                         String city,
                                                         String country,
                                                         String clinic){
        firstName = "%" + firstName + "%";
        lastName = "%" + lastName + "%";
        email = "%" + email + "%";
        city = "%" + city + "%";
        country = "%" + country + "%";
        clinic = "%" + clinic + "%";

        List<Doctor> doctors = doctorRepository.searchDoctors(firstName, lastName, email, city, country, clinic);
        List<DoctorDTO> doctorsDTO = new ArrayList<>();

        for (Doctor d : doctors) {
            doctorsDTO.add(DoctorDTO.builder()
                    .firstName(d.getFirstName())
                    .lastName(d.getLastName())
                    .email(d.getEmail())
                    .city(d.getCity())
                    .country(d.getCountry())
                    .clinic(ClinicDTO.builder().name(d.getClinic().getName()).build())
                    .build());
        }

        return new ResponseEntity<>(doctorsDTO, HttpStatus.OK);
    }
}
