package digit.academy.tutorial.repository.rowmapper;

import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AuditDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AdvocateRowMapper implements RowMapper<Advocate> {

    @Override
    public Advocate mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long lastModifiedTime = rs.getLong("lastmodifiedtime");
        if (rs.wasNull()) lastModifiedTime = null;

        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(rs.getString("createdby"))
                .lastModifiedBy(rs.getString("lastmodifiedby"))
                .createdTime(rs.getLong("createdtime"))
                .lastModifiedTime(lastModifiedTime)
                .build();

        return Advocate.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .applicationNumber(rs.getString("applicationnumber"))
                .barRegistrationNumber(rs.getString("barregistrationnumber"))
                .advocateType(rs.getString("advocatetype"))
                .organisationID(rs.getString("organisationid"))
                .individualId(rs.getString("individualid"))
                .isActive(rs.getBoolean("isactive"))
                .auditDetails(auditDetails)
                .build();
    }
}
