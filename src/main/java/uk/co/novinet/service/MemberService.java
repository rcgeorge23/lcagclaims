package uk.co.novinet.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static uk.co.novinet.service.PersistenceUtils.*;

@Service
public class MemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void update(Claim claim) {
        Member existingMember = findMemberByClaimToken(claim.getClaimToken());

        if (existingMember == null) {
            throw new RuntimeException("Member with claim token " + claim.getClaimToken() + " not found");
        }

        Long nextAvailableId = findNextAvailableId("id", claimTableName());

        String claimSql = "insert into " + claimTableName() + " (" +
                "`id`, " +
                "`user_id`, " +
                "`title`, " +
                "`first_name`, " +
                "`last_name`, " +
                "`email_address`, " +
                "`address_line_1`, " +
                "`address_line_2`, " +
                "`city`, " +
                "`postcode`, " +
                "`country`, " +
                "`phone_number`, " +
                "`can_supply_written_evidence`, " +
                "`scheme_details`, " +
                "`names_and_contact_details_of_scheme_advisors`, " +
                "`any_other_information`" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LOGGER.info("Going to execute insert claim sql: {}", claimSql);

        int claimInsertionResult = jdbcTemplate.update(claimSql,
                nextAvailableId,
                existingMember.getId(),
                claim.getTitle(),
                claim.getFirstName(),
                claim.getLastName(),
                claim.getEmailAddress(),
                claim.getAddressLine1(),
                claim.getAddressLine2(),
                claim.getCity(),
                claim.getPostcode(),
                claim.getCountry(),
                claim.getPhoneNumber(),
                claim.getCanShowWrittenEvidence(),
                claim.getSchemeDetails(),
                claim.getSchemeAdvisorDetails(),
                claim.getAdditionalInformation()
        );

        LOGGER.info("Claim insertion result: {}", claimInsertionResult);

        if (claimInsertionResult != 1) {
            throw new RuntimeException("Unable to insert claim: " + claim);
        }

        LOGGER.info("Going to update member: {}", existingMember);

        String memberSql = "update " + usersTableName() + " u " +
                "set u.has_completed_claim_participant_form = ? " +
                "where u.claim_token = ?";

        LOGGER.info("Created memberSql: {}", memberSql);

        int result = jdbcTemplate.update(
                memberSql,
                true,
                existingMember.getClaimToken()
        );

        LOGGER.info("Update result: {}", result);

        if (result != 1) {
            throw new RuntimeException("Unable to update member: " + existingMember);
        }
    }

    public Member findMemberByClaimToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        List<Member> members = jdbcTemplate.query("select * from " + usersTableName() + " u where lower(u.claim_token) = ?", new Object[] { token.toLowerCase() }, (rs, rowNum) -> buildMember(rs));

        if (members == null || members.size() == 0) {
            return null;
        }

        if (members.size() > 1) {
            LOGGER.error("More than one member found for token: {}, members: ", token, members);
            throw new RuntimeException("More than one member found for token: " + token);
        }

        return members.get(0);
    }

    private Member buildMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getLong("uid"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("mp_name"),
                rs.getString("schemes"),
                rs.getBoolean("mp_engaged"),
                rs.getBoolean("mp_sympathetic"),
                rs.getString("mp_constituency"),
                rs.getString("mp_party"),
                rs.getString("industry"),
                rs.getString("token"),
                rs.getBoolean("has_completed_membership_form"),
                rs.getBoolean("member_of_big_group"),
                rs.getString("how_did_you_hear_about_lcag"),
                rs.getString("big_group_username"),
                rs.getBoolean("hmrc_letter_checked"),
                rs.getBoolean("identification_checked"),
                rs.getBoolean("document_upload_error"),
                rs.getString("claim_token"),
                rs.getBoolean("has_completed_claim_participant_form"),
                rs.getBoolean("has_been_sent_claim_confirmation_email")
        );
    }

    public void markHasBeenSentClaimConfirmationEmail(Member existingMember) {
        String memberSql = "update " + usersTableName() + " u " +
                "set u.has_been_sent_claim_confirmation_email = ? " +
                "where u.uid = ?";

        LOGGER.info("Created memberSql: {}", memberSql);

        int result = jdbcTemplate.update(
                memberSql,
                true,
                existingMember.getId()
        );

        LOGGER.info("Update result: {}", result);

        if (result != 1) {
            throw new RuntimeException("Unable to update member: " + existingMember);
        }
    }
}
