package team.overfullow.tolonbgeub.report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReportUser is a Querydsl query type for ReportUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportUser extends EntityPathBase<ReportUser> {

    private static final long serialVersionUID = 829833434L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReportUser reportUser = new QReportUser("reportUser");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReportEntity report;

    public final team.overfullow.tolonbgeub.user.QUser user;

    public QReportUser(String variable) {
        this(ReportUser.class, forVariable(variable), INITS);
    }

    public QReportUser(Path<? extends ReportUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReportUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReportUser(PathMetadata metadata, PathInits inits) {
        this(ReportUser.class, metadata, inits);
    }

    public QReportUser(Class<? extends ReportUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.report = inits.isInitialized("report") ? new QReportEntity(forProperty("report")) : null;
        this.user = inits.isInitialized("user") ? new team.overfullow.tolonbgeub.user.QUser(forProperty("user")) : null;
    }

}

