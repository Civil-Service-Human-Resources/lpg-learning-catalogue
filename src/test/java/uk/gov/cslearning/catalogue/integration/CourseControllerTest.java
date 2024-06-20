package uk.gov.cslearning.catalogue.integration;

import org.junit.Test;

public class CourseControllerTest extends BaseIntegrationTest {

    @Test
    public void testGetCourses() {
        webTestClient.get()
                .uri("/courses")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.totalResults").isEqualTo(2)
                .jsonPath("$.results[0].title").isEqualTo("Course 1")
                .jsonPath("$.results[0].shortDescription").isEqualTo("Course 1 short description")
                .jsonPath("$.results[0].description").isEqualTo("Course 1 long description")
                .jsonPath("$.results[0].status").isEqualTo("Published")
                .jsonPath("$.results[1].title").isEqualTo("Course 2")
                .jsonPath("$.results[1].shortDescription").isEqualTo("Course 2 short description")
                .jsonPath("$.results[1].description").isEqualTo("Course 2 long description")
                .jsonPath("$.results[1].status").isEqualTo("Published")
                .jsonPath("$.results[1].modules[0].title").isEqualTo("ELearning module")
                .jsonPath("$.results[1].modules[0].description").isEqualTo("An ELearning module")
                .jsonPath("$.results[1].modules[0].cost").isEqualTo(100)
                .jsonPath("$.results[1].modules[0].duration").isEqualTo(100)
                .jsonPath("$.results[1].modules[0].optional").isEqualTo(false);
    }

    @Test
    public void testGetCoursesWithPagination() {
        webTestClient.get()
                .uri("/courses?size=1&page=0")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.totalResults").isEqualTo(2)
                .jsonPath("$.results[0].title").isEqualTo("Course 1")
                .jsonPath("$.results[0].shortDescription").isEqualTo("Course 1 short description")
                .jsonPath("$.results[0].description").isEqualTo("Course 1 long description")
                .jsonPath("$.results[0].status").isEqualTo("Published");

        webTestClient.get()
                .uri("/courses?size=1&page=1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.results[0].title").isEqualTo("Course 2")
                .jsonPath("$.results[0].shortDescription").isEqualTo("Course 2 short description")
                .jsonPath("$.results[0].description").isEqualTo("Course 2 long description")
                .jsonPath("$.results[0].status").isEqualTo("Published")
                .jsonPath("$.results[0].modules[0].title").isEqualTo("ELearning module")
                .jsonPath("$.results[0].modules[0].description").isEqualTo("An ELearning module")
                .jsonPath("$.results[0].modules[0].cost").isEqualTo(100)
                .jsonPath("$.results[0].modules[0].duration").isEqualTo(100)
                .jsonPath("$.results[0].modules[0].optional").isEqualTo(false);
    }

}
