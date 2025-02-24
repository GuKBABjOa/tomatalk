package team.overfullow.tolonbgeub.debate.debate.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDebate is a Querydsl query type for Debate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDebate extends EntityPathBase<Debate> {

    private static final long serialVersionUID = 2046682492L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDebate debate = new QDebate("debate");

    public final team.overfullow.tolonbgeub.core.auditing.QBaseTimeEntity _super = new team.overfullow.tolonbgeub.core.auditing.QBaseTimeEntity(this);

    public final EnumPath<team.overfullow.tolonbgeub.debate.Category> category = createEnum("category", team.overfullow.tolonbgeub.debate.Category.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ListPath<DebateUser, QDebateUser> debateUsers = this.<DebateUser, QDebateUser>createList("debateUsers", DebateUser.class, QDebateUser.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedAt = _super.lastModifiedAt;

    public final EnumPath<DebateStatus> status = createEnum("status", DebateStatus.class);

    public final team.overfullow.tolonbgeub.debate.subject.QSubject subject;

    public QDebate(String variable) {
        this(Debate.class, forVariable(variable), INITS);
    }

    public QDebate(Path<? extends Debate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDebate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDebate(PathMetadata metadata, PathInits inits) {
        this(Debate.class, metadata, inits);
    }

    public QDebate(Class<? extends Debate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.subject = inits.isInitialized("subject") ? new team.overfullow.tolonbgeub.debate.subject.QSubject(forProperty("subject")) : null;
    }

}

