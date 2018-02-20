package dostal.api.filter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import dostal.api.filter.exception.InvalidOperationException;
import dostal.api.filter.model.OperatorAttribute;
import dostal.api.filter.model.SQLAttribute;

/**
 * This is more-less parser which produces SQLAttribute instance.
 * 
 * @param <T>
 */
public class AttributeBuilder<T> {

    enum Splitter {
        INTERVAL, MULTIPLE;
        @Override
        public String toString() {
            switch (this) {
            case INTERVAL:
                return "--";
            case MULTIPLE:
                return ",";
            }
            return null;
        }
    }

    private final static String NEGATION = "!";
    private final static String INTERVAL = "--";
    private final static String NULL = "_NULL_";
    private final static String INFINITY = "-1";

    private Class<T> type;

    private String propertyName;
    private OperatorAttribute operator;
    private List<?> value;

    /*
     * Constructor is need for purpose type comparison
     */
    public AttributeBuilder(Class<T> type) {
        this.type = type;
        this.value = null;
    }

    /**
     * This mapped property in where clause to property in select
     * @param propertyName
     * @return (this) instance of AttributeBuilder
     */
    public AttributeBuilder<?> setKey(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    /**
     * This set a value for particular key (also set the specific operator)
     * @param value
     * @return (this) instance of AttributeBuilder
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public AttributeBuilder<T> setValue(String value) throws IllegalAccessException, InstantiationException {
        
        if (value.equals("")) {
            this.operator = OperatorAttribute.NOT_IN;
            return this;
        }

        Splitter splitter = value.contains("--") ? Splitter.INTERVAL : Splitter.MULTIPLE;

        List<String> temp = Arrays.asList(setOperator(value).split(splitter.toString()));

        if (this.operator != OperatorAttribute.NULL && this.operator != OperatorAttribute.NOT_NULL && this.operator != OperatorAttribute.INFINITY_TO_INFINITY) {
          if (Long.class == type) {
                if(this.operator == OperatorAttribute.LESS_THAN_INFINITY || this.operator == OperatorAttribute.MORE_THAN_INFINITY) {
                    this.value = temp.stream().filter(n -> !n.equals(INFINITY)).map(n -> Long.valueOf(n)).collect(Collectors.toList());
                }else {
                    this.value = temp.stream().map(n -> Long.valueOf(n)).collect(Collectors.toList());
                }
            } else if (String.class == type) {
                this.value = (List<String>) temp;
            } else if (Date.class == type) {
                if(this.operator == OperatorAttribute.LESS_THAN_INFINITY || this.operator == OperatorAttribute.MORE_THAN_INFINITY) {
                    this.value = temp.stream().filter(n -> !n.equals(INFINITY)).map(n -> new Date(Long.valueOf(n))).collect(Collectors.toList());
                }else {
                    this.value = temp.stream().map(n -> new Date(Long.valueOf(n))).collect(Collectors.toList());
                }
            }
        }
        
        return this;
    }

    @SuppressWarnings("unchecked")
    public SQLAttribute<T> create() {
        return new SQLAttribute<T>(propertyName, operator, (List<T>) value);
    }

    private String setOperator(String value) {
        
        if (value.startsWith(NEGATION)) {
            if (value.contains(INTERVAL)) {
                if(value.startsWith(NEGATION + INFINITY) || value.endsWith(INTERVAL + INFINITY)) {
                    throw new InvalidOperationException("Builder does not support NEGATION of INFINITY INTERVAL. Wrong value: " + value);
                }
                this.operator = OperatorAttribute.NOT_BETWEEN;
            } else if (value.contains(NULL)) {
                this.operator = OperatorAttribute.NOT_NULL;
            } else {
                this.operator = OperatorAttribute.NOT_IN;
            }
            // need to remove first char before it will be parsed "!"
            return value.substring(1);

        } else {
            if (value.equals(INFINITY + INTERVAL + INFINITY)) {
                this.operator = OperatorAttribute.INFINITY_TO_INFINITY;
            }else if(value.startsWith(INFINITY + INTERVAL)) {
                this.operator = OperatorAttribute.MORE_THAN_INFINITY;
            }else if(value.endsWith(INTERVAL + INFINITY)) {
                this.operator = OperatorAttribute.LESS_THAN_INFINITY;
            } else if (value.contains(INTERVAL)) {
                this.operator = OperatorAttribute.BETWEEN;
            } else if (value.contains(NULL)) {
                this.operator = OperatorAttribute.NULL;
            } else {
                this.operator = OperatorAttribute.IN;
            }
        }
        return value;
    }
}
