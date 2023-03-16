package africa.semicolon.uberdeluxe.cloud;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CloudinaryCloudServiceImplTest {

    @Autowired
    private CloudService cloudService;

    private MockMultipartFile file;

    @BeforeEach
    void setUp() throws IOException {
        file =
                new MockMultipartFile("puppy",
                        new FileInputStream("/home/semicolon/Documents/spring-projects/uberdeluxe/src/test/resources/puppy.jpeg"));
    }

    @Test
    void uploadTest() {
        var cloudinaryImageUrl = cloudService.upload(file);
        assertThat(cloudinaryImageUrl).isNotNull();
    }
}