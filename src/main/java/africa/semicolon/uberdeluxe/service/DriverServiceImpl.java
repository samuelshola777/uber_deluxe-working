package africa.semicolon.uberdeluxe.service;

import africa.semicolon.uberdeluxe.cloud.CloudService;
import africa.semicolon.uberdeluxe.data.dto.request.EmailNotificationRequest;
import africa.semicolon.uberdeluxe.data.dto.request.Recipient;
import africa.semicolon.uberdeluxe.data.dto.request.RegisterDriverRequest;
import africa.semicolon.uberdeluxe.data.dto.response.RegisterResponse;
import africa.semicolon.uberdeluxe.data.models.AppUser;
import africa.semicolon.uberdeluxe.data.models.Driver;
import africa.semicolon.uberdeluxe.data.repositories.DriverRepository;
import africa.semicolon.uberdeluxe.exception.ImageUploadException;
import africa.semicolon.uberdeluxe.notification.MailService;
import africa.semicolon.uberdeluxe.util.AppUtilities;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService{

    private final DriverRepository driverRepository;
    private final CloudService cloudService;
    private final ModelMapper modelMapper;

    private final MailService mailService;

    @Override
    public RegisterResponse register(RegisterDriverRequest request) {
        AppUser driverDetails = modelMapper.map(request, AppUser.class);
        driverDetails.setCreatedAt(LocalDateTime.now().toString());
        //steps
        //1. upload drivers license image
        var imageUrl = cloudService.upload(request.getLicenseImage());
        if (imageUrl==null)
            throw new ImageUploadException("Driver Registration failed");
        //2. create driver object
        Driver driver = Driver.builder()
                .userDetails(driverDetails)
                .licenseImage(imageUrl)
                .build();
        //3. save driver
        Driver savedDriver = driverRepository.save(driver);
        //4. send verification mail to driver
        EmailNotificationRequest emailRequest = buildNotificationRequest(savedDriver.getUserDetails().getEmail(), savedDriver.getUserDetails().getName(), driver.getId());
        String response = mailService.sendHtmlMail(emailRequest);
        if (response==null) return getRegisterFailureResponse();
        return RegisterResponse.builder()
                .code(HttpStatus.CREATED.value())
                .id(savedDriver.getId())
                .isSuccess(true)
                .message("Driver Registration Successful")
                .build();
    }

    private static RegisterResponse getRegisterFailureResponse() {
        return RegisterResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .id(-1L)
                .isSuccess(false)
                .message("Driver Registration Failed")
                .build();
    }

    private EmailNotificationRequest buildNotificationRequest(String email, String name, Long userId) {
        EmailNotificationRequest request = new EmailNotificationRequest();
        request.getTo().add(new Recipient(name, email));
        String template = AppUtilities.getMailTemplate();
        String content = String.format(template, name, AppUtilities.generateVerificationLink(userId));
        request.setHtmlContent(content);
        return request;
    }

    @Override
    public Optional<Driver> getDriverBy(Long driverId) {
        return driverRepository.findById(driverId);
    }

    @Override
    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }
}
