package org.tuxdevelop.spring.batch.lightmin.server.admin;

import org.junit.runner.RunWith;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tuxdevelop.spring.batch.lightmin.client.api.LightminClientApplication;
import org.tuxdevelop.spring.batch.lightmin.client.configuration.LightminClientProperties;
import org.tuxdevelop.spring.batch.lightmin.server.ITConfigurationApplication;
import org.tuxdevelop.spring.batch.lightmin.server.support.RegistrationBean;
import org.tuxdevelop.test.configuration.ITConfigurationEmbedded;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ITConfigurationApplication.class, ITConfigurationEmbedded.class})
public class EmbeddedAdminServerServiceIT extends AdminServerServiceIT {

    @Autowired
    private AdminServerService adminServerService;
    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private LightminClientProperties lightminClientProperties;
    @Autowired
    private RegistrationBean registrationBean;


    @Override
    public AdminServerService getAdminServerService() {
        assertThat(adminServerService instanceof EmbeddedAdminServerService).isTrue();
        return adminServerService;
    }

    @Override
    public LightminClientApplication createLightminClientApplication() {
        final LightminClientApplication lightminClientApplication = LightminClientApplication
                .createApplication(new LinkedList<>(jobRegistry.getJobNames()), lightminClientProperties);
        registrationBean.register(lightminClientApplication);
        return lightminClientApplication;
    }
}
