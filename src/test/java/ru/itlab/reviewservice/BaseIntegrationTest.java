package ru.itlab.reviewservice;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BaseIntegrationTest extends BaseTest {

    @Autowired
    private EntityManager em;

    protected Long getReviewIdByMenuId(Long menuId) {
        return em.createQuery("select r.id from ru.itlab.reviewservice.storage.models.Review r where r.menuId= ?1", Long.class)
                .setParameter(1, menuId)
                .getSingleResult();
    }
}
