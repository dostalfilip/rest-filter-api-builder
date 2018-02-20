package dostal.api.filter.model;

import java.util.List;

/**
 * this is model of one elementary criteria
 */
public class SQLAttribute<T> {
    
    private final String propertyName;
    private final OperatorAttribute operator;
    private final List<T> value;
    
    /**
     * 
     * @param propertyName
     * @param operator
     * @param value
     */
    public SQLAttribute(final String propertyName, final OperatorAttribute operator, final List<T> value) {
        this.propertyName = propertyName;
        this.operator = operator;
        this.value = value;
    }

    public final List<T> getValue() {
        return value;
    }

    public final String getPropertyName() {
        return propertyName;
    }

    public final OperatorAttribute getOperator() {
        return operator;
    }    
}
