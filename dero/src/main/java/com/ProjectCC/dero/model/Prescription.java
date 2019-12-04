package com.ProjectCC.dero.model;

import com.ProjectCC.dero.dto.ExaminationDTO;
import com.ProjectCC.dero.dto.MedicationDTO;
import com.ProjectCC.dero.dto.PrescriptionDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class Prescription {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "certified")
   private Boolean certified;

   @OneToOne(mappedBy = "prescription")
   public Examination examination;

   @ManyToMany
   @JoinTable(
           name = "prescriptions_medication",
           joinColumns = @JoinColumn(name = "medication_id", referencedColumnName = "id"),
           inverseJoinColumns = @JoinColumn(name = "prescription_id", referencedColumnName = "id")
   )
   public Set<Medication> medication;

   @OneToOne()
   @JoinColumn(name = "doctor_id", referencedColumnName = "id")
   public Doctor doctor;

   @OneToOne()
   @JoinColumn(name = "nurse_id", referencedColumnName = "id")
   public Nurse nurse;

   public Prescription() {
   }

   public Prescription(Boolean certified, Examination examination, Set<Medication> medication, Doctor doctor, Nurse nurse) {
      this.certified = certified;
      this.medication = medication;
      this.doctor = doctor;
      this.nurse = nurse;
   }

   public Prescription(PrescriptionDTO prescriptionDTO){
      this.certified = prescriptionDTO.isCertified();
      this.doctor = new Doctor(prescriptionDTO.getDoctor());
      this.nurse = new Nurse(prescriptionDTO.getNurse());
      for(MedicationDTO med: prescriptionDTO.getMedications()){
         this.medication.add(new Medication(med));
      }
   }
}