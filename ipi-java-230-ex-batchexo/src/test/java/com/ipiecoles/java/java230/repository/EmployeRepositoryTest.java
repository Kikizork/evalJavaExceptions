package com.ipiecoles.java.java230.repository;

import com.ipiecoles.java.java230.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;

public class EmployeRepositoryTest {

    @Test
    public void exo201testCrudRepository() throws Exception {
        // 2.1. Créer l'interface EmployeRepository et la faire implémenter CrudRepository
        TestUtils.checkImplementInterface("EmployeRepository", "CrudRepository");
    }

    @Test
    public void exo411testRequete() throws Exception {
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            TestUtils.checkMethod("BaseEmployeRepository", "findByMatricule", "Employe", TestUtils.STRING);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findByMatricule", "Employe", TestUtils.STRING);
        }
    }

    @Test
    public void exo412testRequete() throws Exception {
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            TestUtils.checkMethod("BaseEmployeRepository", "findByNomAndPrenom", TestUtils.LIST, TestUtils.STRING, TestUtils.STRING);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findByNomAndPrenom", TestUtils.LIST, TestUtils.STRING, TestUtils.STRING);
        }
    }

    @Test
    public void exo413testRequete() throws Exception {
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            TestUtils.checkMethod("BaseEmployeRepository", "findByNomIgnoreCase", TestUtils.LIST, TestUtils.STRING);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findByNomIgnoreCase", TestUtils.LIST, TestUtils.STRING);
        }
    }

    @Test
    public void exo414testRequete() throws Exception {
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            TestUtils.checkMethod("BaseEmployeRepository", "findByDateEmbaucheBefore", TestUtils.LIST, TestUtils.LOCAL_DATE);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findByDateEmbaucheBefore", TestUtils.LIST, TestUtils.LOCAL_DATE);
        }
    }

    @Test
    public void exo415testRequete() throws Exception {
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            TestUtils.checkMethod("BaseEmployeRepository", "findByDateEmbaucheAfter", TestUtils.LIST, TestUtils.LOCAL_DATE);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findByDateEmbaucheAfter", TestUtils.LIST, TestUtils.LOCAL_DATE);
        }
    }

    @Test
    public void exo416testRequete() throws Exception {
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            TestUtils.checkMethod("BaseEmployeRepository", "findBySalaireGreaterThanOrderBySalaireDesc", TestUtils.LIST, TestUtils.DOUBLE);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findBySalaireGreaterThanOrderBySalaireDesc", TestUtils.LIST, TestUtils.DOUBLE);
        }
    }

    @Test
    public void exo402testPaging() throws Exception {
        TestUtils.checkImplementInterface("EmployeRepository", "PagingAndSortingRepository");
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            //TestUtils.checkMethod("BaseEmployeRepository", "findByNomIgnoreCase", TestUtils.PAGE, TestUtils.STRING, TestUtils.PAGEABLE);
        } catch (ClassNotFoundException e){
            TestUtils.checkMethod("EmployeRepository", "findByNomIgnoreCase", TestUtils.PAGE, TestUtils.STRING, TestUtils.PAGEABLE);
        }
    }

    @Test
    public void exo403testJpql() throws Exception{
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            Method m = TestUtils.checkMethod("BaseEmployeRepository", "findByNomOrPrenomAllIgnoreCase", TestUtils.LIST, TestUtils.STRING);
            Assertions.assertThat(m.isAnnotationPresent(Query.class)).isTrue();
            Assertions.assertThat(m.getAnnotation(Query.class).value()).isEqualToIgnoringCase("select e from #{#entityName} e where lower(e.prenom) = lower(:nomOuPrenom) or lower(e.nom) = lower(:nomOuPrenom)");
        } catch (ClassNotFoundException e){
            Method m = TestUtils.checkMethod("EmployeRepository", "findByNomOrPrenomAllIgnoreCase", TestUtils.LIST, TestUtils.STRING);
            Assertions.assertThat(m.isAnnotationPresent(Query.class)).isTrue();
            Assertions.assertThat(m.getAnnotation(Query.class).value()).isEqualToIgnoringCase("select e from Employe e where lower(e.prenom) = lower(:nomOuPrenom) or lower(e.nom) = lower(:nomOuPrenom)");
        }
    }

    @Test
    public void exo404testNatif() throws Exception{
        try {
            TestUtils.getClasse("BaseEmployeRepository");
            Method m = TestUtils.checkMethod("BaseEmployeRepository", "findEmployePlusRiches", TestUtils.LIST);
            Assertions.assertThat(m.isAnnotationPresent(Query.class)).isTrue();
            Assertions.assertThat(m.getAnnotation(Query.class).value()).isEqualToIgnoringCase("SELECT * FROM Employe WHERE salaire > (SELECT avg(e2.salaire) FROM Employe e2)");
            Assertions.assertThat(m.getAnnotation(Query.class).nativeQuery()).isTrue();
        } catch (ClassNotFoundException e){
            Method m = TestUtils.checkMethod("EmployeRepository", "findByNomOrPrenomAllIgnoreCase", TestUtils.LIST);
            Assertions.assertThat(m.isAnnotationPresent(Query.class)).isTrue();
            Assertions.assertThat(m.getAnnotation(Query.class).value()).isEqualToIgnoringCase("SELECT * FROM Employe WHERE salaire > (SELECT avg(e2.salaire) FROM Employe e2)");
            Assertions.assertThat(m.getAnnotation(Query.class).nativeQuery()).isTrue();
        }
    }

}
