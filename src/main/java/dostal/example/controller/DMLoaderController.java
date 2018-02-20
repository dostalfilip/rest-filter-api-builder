package dostal.example.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dostal.api.filter.AttributeBuilder;
import dostal.api.filter.QueryManufacture;
import dostal.api.filter.model.SQLAttribute;
import dostal.api.filter.model.SQLQueryModel;
import dostal.example.model.PetExemple;
import dostal.example.repository.PetExempleJPARepository;

@RestController
@RequestMapping("/api/controller")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DMLoaderController {

  
    @Autowired
    EntityManager entityManager;
    
    @Autowired
    PetExempleJPARepository repository;

    /**
     * This method use criteria API and it also can be paginate. Global Jobs in
     * monitor can be filter be this !
     * 
     * @param page
     * @param page_size
     * @param id_
     * @param class_
     * @param status_id_
     * @param configuration_id_
     * @return
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.GET, value = "global_jobs", produces = "application/json")
    public List<PetExemple> findGlobalJobsGlobalJobs(
            @RequestParam(value = "id", required = false, defaultValue = "") String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "age", required = false, defaultValue = "") String age,
            @RequestParam(value = "weight", required = false, defaultValue = "") String weight
            ) throws IllegalAccessException, InstantiationException {
        
        final int totalCount = (int) repository.count();

        if (totalCount == 0) {
            return new ArrayList<PetExemple>();
        }

        /*
         *Add all possible Attributes 
         */
        ArrayList<SQLAttribute> list = new ArrayList<SQLAttribute>();
        list.add( new AttributeBuilder<Long>(Long.class).setKey("p.id").setValue(id).create());
        list.add( new AttributeBuilder<String>(String.class).setKey("p.name").setValue(name).create());
        list.add( new AttributeBuilder<Long>(Long.class).setKey("t.age").setValue(age).create());
        list.add( new AttributeBuilder<Long>(Long.class).setKey("t.weight").setValue(weight).create());
        
        
        String querySelect = "SELECT p.id, p.name, p.age, p.weight";
        String queryFrom = "FROM PetExample p";
        String queryOrder = "ORDER BY p.id ASC";
        SQLQueryModel model = new SQLQueryModel(querySelect, queryFrom, queryOrder); 
        
        @SuppressWarnings("unchecked")
        TypedQuery<PetExemple> typedQuery = new QueryManufacture(PetExemple.class)
                .setModel(model)
                .buildTypedQuery(entityManager, list);
        
                return typedQuery.getResultList();
    }

}