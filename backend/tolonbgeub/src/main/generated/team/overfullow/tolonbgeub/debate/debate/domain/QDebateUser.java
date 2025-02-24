package team.overfullow.tolonbgeub.debate.debate.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDebateUser is a Querydsl query type for DebateUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDebateUser extends EntityPathBase<DebateUser> {

    private static final long serialVersionUID = -713087129L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDebateUser debateUser = new QDebateUser("debateUser");

    public final QDebate debate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath position = createString("position");

    public final NumberPath<Integer> positionOrder = createNumber("positionOrder", Integer.class);

    public final NumberPath<Integer> speechOrder = createNumber("speechOrder", Integer.class);

    public final team.overfullow.tolonbgeub.user.QUser user;

    public QDebateUser(String variable) {
        this(DebateUser.class, forVariable(variable), INITS);
    }

    public QDebateUser(Path<? extends DebateUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDebateUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDebateUser(PathMetadata metadata, PathInits inits) {
        this(DebateUser.class, metadata, inits);
    }

    public QDebateUser(Class<? extends DebateUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.debate = inits.isInitialized("debate") ? new QDebate(forProperty("debate"), inits.get("debate")) : null;
        this.user = inits.isInitialized("user") ? new team.overfullow.tolonbgeub.user.QUser(forProperty("user")) : null;
    }

}

