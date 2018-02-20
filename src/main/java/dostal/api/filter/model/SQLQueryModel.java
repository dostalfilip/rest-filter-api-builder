package dostal.api.filter.model;
/**
 * This support genericity of the Query Manufactory
 */
public class SQLQueryModel {
    
    private final static String SPACE = " ";

    /* Example:
     * "SELECT NEW de.usu.si.toolkit.loader.job.rest.model.GlobalJob
     * (t.id, t.class, t.startTime, t.endTime, t.status, s.snapshotTag, s.transactionKey, t.configuration.name)"
     */
    private String querySelect;
    /* Example:
     * " FROM GlobalJob t LEFT JOIN t.snapshot s "
     */
    private String queryFrom;
    /* Example:
     * " ORDER BY t.scheduledTime ASC "
     */
    private String queryOrder;
    
    public SQLQueryModel(String querySelect, String queryFrom, String queryOrder) {
        this.querySelect = querySelect + SPACE;
        this.queryFrom = queryFrom + SPACE;
        this.queryOrder = queryOrder + SPACE;
    }
    
    public String getQuerySelect() {
        return querySelect;
    }
    
    public String getQueryFrom() {
        return queryFrom;
    }
    
    public String getQueryOrder() {
        return queryOrder;
    } 
}