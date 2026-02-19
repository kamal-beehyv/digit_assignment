package digit.academy.tutorial.repository.querybuilder;

import digit.academy.tutorial.web.models.AdvocateSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class AdvocateQueryBuilder {

    private static final String BASE_QUERY = " SELECT id, tenantid, applicationnumber, barregistrationnumber, advocatetype, organisationid, individualid, isactive, createdby, lastmodifiedby, createdtime, lastmodifiedtime FROM eg_advocate ";
    private static final String ORDER_BY = " ORDER BY createdtime DESC ";

    public String getAdvocateSearchQuery(AdvocateSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }
        if (!ObjectUtils.isEmpty(criteria.getApplicationNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" applicationnumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }
        if (!ObjectUtils.isEmpty(criteria.getBarRegistrationNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" barregistrationnumber = ? ");
            preparedStmtList.add(criteria.getBarRegistrationNumber());
        }
        if (!ObjectUtils.isEmpty(criteria.getId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" id = ? ");
            preparedStmtList.add(criteria.getId());
        }
        if (!ObjectUtils.isEmpty(criteria.getAdvocateType())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" advocatetype = ? ");
            preparedStmtList.add(criteria.getAdvocateType());
        }

        query.append(ORDER_BY);
        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }
}
