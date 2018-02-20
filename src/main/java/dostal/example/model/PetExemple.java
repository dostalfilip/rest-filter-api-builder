package dostal.example.model;

import javax.persistence.Entity;

@Entity
public class PetExemple {
    
    
    String name;
    int age;
    int weight;
    
    public PetExemple() {
        super();
    }

    public PetExemple(String name, int age, int weight) {
        super();
        this.name = name;
        this.age = age;
        this.weight = weight;
    }

    protected final String getName() {
        return name;
    }
    
    protected final void setName(String name) {
        this.name = name;
    }

    protected final int getAge() {
        return age;
    }

    protected final void setAge(int age) {
        this.age = age;
    }
    protected final int getWeight() {
        return weight;
    }
    protected final void setWeight(int weight) {
        this.weight = weight;
    }
    
}
