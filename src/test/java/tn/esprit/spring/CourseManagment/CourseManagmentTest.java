package tn.esprit.spring.CourseManagment;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.GestionStationSkiApplication;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.services.CourseServicesImpl;
import tn.esprit.spring.services.ICourseServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CourseManagmentTest {

    @Mock
    ICourseRepository courseRepository;

    @Mock
    IRegistrationRepository registrationRepository;

    @InjectMocks
    CourseServicesImpl courseServices;

    Course course1 = new Course(12344555L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 100.0f, 2, new HashSet<Registration>());
    List<Course> courseList = new ArrayList<Course>(){
        {
            add(new Course(12L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 100.0f, 2, new HashSet<Registration>()));
            add(new Course(13L, 2, TypeCourse.COLLECTIVE_CHILDREN, Support.SNOWBOARD, 200.0f, 3, new HashSet<Registration>()));
        }
    };

    @Test
    void retrieveAllCourses() {
        System.out.println("In the function");
        // Mock the behavior of the repository
        when(courseRepository.findAll()).thenReturn(courseList);

        // Call the method being tested
        System.out.println("Before calling retrieveActivitySector()");
        List<Course> prods = courseServices.retrieveAllCourses();

        // Assert the result
        assertNotNull(prods);
    }

    @Test
    void updateCourse() {

        Course course =new Course(12L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 100.0f, 2, new HashSet<Registration>());
        course.setPrice(20.0f);
        Mockito.when(courseRepository.save(Mockito.any())).thenReturn(course);

        Course updatedCourse = courseServices.updateCourse(course);
        Assertions.assertEquals(20.0f, updatedCourse.getPrice());
    }

    @SpringBootTest(classes = {GestionStationSkiApplication.class})
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @ExtendWith(SpringExtension.class)
    class StockServiceImplJunitTest {

        @Autowired
        ICourseServices cr;

        Course course1 = new Course(12344555L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 100.0f, 2, new HashSet<Registration>());


        @Test
        @Order(1)
        void testAddCourse() {
            Course course1 = cr.addCourse(new Course(12344555L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 100.0f, 2, new HashSet<Registration>()));
            Assertions.assertEquals(3, course1.getNumCourse());
        }

        @Test
        @Order(2)
        void retrieveCourse() {
            Course course = cr.retrieveCourse(12344555L);
            Assertions.assertNotNull(course);
        }
    }

}
