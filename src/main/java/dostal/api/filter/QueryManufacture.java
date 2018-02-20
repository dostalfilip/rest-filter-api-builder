package dostal.api.filter;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import dostal.api.filter.model.OperatorAttribute;
import dostal.api.filter.model.SQLAttribute;
import dostal.api.filter.model.SQLQueryModel;

/**
 * Take all attributes from list and generate the finalized query with
 * substitutions.
 */
public class QueryManufacture<T> {

    private final static String SPACE = " ";
    private final static String WHERE = "WHERE";
    private final static String COLON = ":";
    private final static String FINISH = SPACE + "AND";
    private final static String KEY = "KEY";

    private final static String IN = SPACE + "IN" + SPACE;
    private final static String NOT_IN = SPACE + "NOT" + SPACE + "IN" + SPACE;

    private final static String LEFT_BRACKET = SPACE + "(";
    private final static String RIGHT_BRACKET = ")";

    private final static String MORE = SPACE + ">=" + SPACE;
    private final static String LESS = SPACE + "<=" + SPACE;

    private final static String AND = SPACE + "AND" + SPACE;
    private final static String OR = SPACE + "OR" + SPACE;
    private final static String FIRST = "1";
    private final static String SECOND = "2";

    private final static String IS_NULL = SPACE + "IS" + SPACE + "NULL" ;
    private final static String IS_NOT_NULL = SPACE + "IS" + SPACE + "NOT" + SPACE + "NULL" ;

    private SQLQueryModel model;

    private Class<T> type;
    
    public QueryManufacture(Class<T> type) {
        this.type = type;
    }

    public QueryManufacture<T> setModel(SQLQueryModel model) {
        this.model = model;
        return this;
    }

    /**
     * This build raw query with (key,:param) substitution
     * 
     * @param listAttribute
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected String modify(ArrayList<SQLAttribute> listAttribute) {
        if (listAttribute.isEmpty())
            return "";

        String finalQuery = "";

        for (SQLAttribute curr : listAttribute) {

            if (curr.getValue() == null
                    && (curr.getOperator() != OperatorAttribute.NULL && curr.getOperator() != OperatorAttribute.NOT_NULL)) {
                continue;
            }

            switch (curr.getOperator()) {
            case NOT_IN:
                finalQuery += SPACE + curr.getPropertyName();
                finalQuery += NOT_IN;
                finalQuery += COLON + generateKey(curr.getPropertyName()) + FINISH;
                break;
            case IN:
                finalQuery += SPACE + curr.getPropertyName();
                finalQuery += IN;
                finalQuery += COLON + generateKey(curr.getPropertyName()) + FINISH;
                break;
            case NOT_BETWEEN:
                finalQuery += LEFT_BRACKET + curr.getPropertyName() + LESS + COLON + generateKey(curr.getPropertyName() + FIRST);
                finalQuery += OR;
                finalQuery += curr.getPropertyName() + MORE + COLON + generateKey(curr.getPropertyName() + SECOND)
                        + RIGHT_BRACKET;
                finalQuery += FINISH;
                break;
            case BETWEEN:
                finalQuery += LEFT_BRACKET + curr.getPropertyName() + MORE + COLON + generateKey(curr.getPropertyName() + FIRST);
                finalQuery += AND;
                finalQuery += curr.getPropertyName() + LESS + COLON + generateKey(curr.getPropertyName() + SECOND)
                        + RIGHT_BRACKET;
                finalQuery += FINISH;
                break;
            case NOT_NULL:
            case INFINITY_TO_INFINITY:
                finalQuery += SPACE + curr.getPropertyName();
                finalQuery += IS_NOT_NULL;
                finalQuery += FINISH;
                break;
            case NULL:
                finalQuery += SPACE + curr.getPropertyName();
                finalQuery += IS_NULL;
                finalQuery += FINISH;
                break;
            case LESS_THAN_INFINITY:
                finalQuery += SPACE + curr.getPropertyName();
                finalQuery += MORE;
                finalQuery += COLON + generateKey(curr.getPropertyName()) + FINISH;
                break;
            case MORE_THAN_INFINITY:
                finalQuery += SPACE + curr.getPropertyName();
                finalQuery += LESS;
                finalQuery += COLON + generateKey(curr.getPropertyName()) + FINISH;
                break;
            }
        }

        if (finalQuery.isEmpty())
            return finalQuery;

        finalQuery = finalQuery.substring(0, finalQuery.length() - KEY.length());

        return WHERE + finalQuery;
    }

    /**
     * This substitute all parameters in query.
     * 
     * @param entityManager
     * @param listAttribute
     * @return Typed query with all substitutions.
     */
    @SuppressWarnings("rawtypes")
    public TypedQuery<T> buildTypedQuery(EntityManager entityManager,
            ArrayList<SQLAttribute> listAttribute) {

        TypedQuery<T> typedQuery = entityManager.createQuery(
                model.getQuerySelect() + model.getQueryFrom() + modify(listAttribute) + model.getQueryOrder(), type);

        for (SQLAttribute curr : listAttribute) {
            if (curr.getValue() == null)
                continue;

            if (curr.getOperator() == OperatorAttribute.BETWEEN || curr.getOperator() == OperatorAttribute.NOT_BETWEEN) {
                typedQuery.setParameter(generateKey(curr.getPropertyName() + FIRST), curr.getValue().get(0));
                typedQuery.setParameter(generateKey(curr.getPropertyName() + SECOND), curr.getValue().get(1));
            } else if (curr.getOperator() != OperatorAttribute.NOT_NULL && curr.getOperator() != OperatorAttribute.NULL) {
                typedQuery.setParameter(generateKey(curr.getPropertyName()), curr.getValue());
            }
        }

        return typedQuery;
    }

    /**
     * Without this execution of query will fails
     * 
     * @param propertyName
     * @return
     */
    private String generateKey(String propertyName) {
        return KEY + propertyName.replace(".", "_");
    }
}