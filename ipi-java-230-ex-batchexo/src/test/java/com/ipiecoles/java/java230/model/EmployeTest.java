package com.ipiecoles.java.java230.model;

import com.ipiecoles.java.java230.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.persistence.*;
import java.lang.reflect.Field;

public class EmployeTest {
    @Test
    public void exo101testEntity() throws Exception {
        //Annoter la classe Employe en tant qu'entité pour qu'elle puisse récupérer les données de la table Employe
        TestUtils.checkAnnotation("Employe", Entity.class);
    }

    @Test
    public void exo102testEntity() throws Exception {
        //Ajouter un champ id de type Long et l'annoter de manière a ce qu'il puisse gérer les identifiants générés automatiquement par MySQL
        Field field = TestUtils.checkPrivateField("Employe", "id", TestUtils.LONG);
        Assertions.assertThat(field.isAnnotationPresent(Id.class)).isTrue();
        Assertions.assertThat(field.isAnnotationPresent(GeneratedValue.class)).isTrue();
        Assertions.assertThat(field.getAnnotation(GeneratedValue.class).strategy()).isEqualTo(GenerationType.AUTO);
    }

    @Test
    public void exo501testEntity() throws Exception {
        //Annoter la classe Employe en tant qu'entité pour qu'elle puisse récupérer les données de la table Employe
        TestUtils.checkAnnotation("Employe", Entity.class);
        TestUtils.checkAnnotation("Employe", Inheritance.class);
        Assertions.assertThat(TestUtils.getClasse("Employe").getAnnotation(Inheritance.class).strategy()).isEqualTo(InheritanceType.JOINED);
    }
}
