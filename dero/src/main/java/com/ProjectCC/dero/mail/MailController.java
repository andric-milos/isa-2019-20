package com.ProjectCC.dero.mail;


import com.ProjectCC.dero.model.VacationRequest;
import com.ProjectCC.dero.repository.VacationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Optional;

@RestController
@RequestMapping(value="/api/mail")
public class MailController {

    private VacationRequestRepository vacationRequestRepository;
    private SmtpMailSender smtpMailSender;

    @Autowired
    public MailController(VacationRequestRepository vacationRequestRepository, SmtpMailSender smtpMailSender) {
        this.vacationRequestRepository = vacationRequestRepository;
        this.smtpMailSender = smtpMailSender;
    }

    @GetMapping(value = "/accept/{email}/{id}")
    public void accept(@PathVariable String email, @PathVariable String id) throws MessagingException {
        smtpMailSender.send(email, "Registration", "You have been successfully registered." +
                                                            "Please activate your account, clicking on the link below. " +
                                                             "<a href='http://localhost:8080/api/patient/add/"+id+"'>Activate</a>");
    }

    @GetMapping(value = "/refuse/{email}/{description}")
    public void decline(@PathVariable String email, @PathVariable String description) throws MessagingException {
        smtpMailSender.send(email, "Registration", description);
    }

    @GetMapping(value = "/accept-vacation/{email}/{id}")
    public void acceptVacationRequest(@PathVariable String email, @PathVariable Long id) throws MessagingException {
        Optional<VacationRequest> opt = this.vacationRequestRepository.findById(id);
        VacationRequest vac = opt.get();
        vac.setAccepted(true);
        this.vacationRequestRepository.save(vac);
        smtpMailSender.send(email, "Vacation request", "Your vacation request has been accepted.");
    }

    @GetMapping(value = "/refuse-vacation/{email}/{id}/{description}")
    public void refuseVacationRequest(@PathVariable String email, @PathVariable Long id, @PathVariable String description) throws MessagingException {
        this.vacationRequestRepository.deleteById(id);
        smtpMailSender.send(email, "Vacation Request", description);
    }
}