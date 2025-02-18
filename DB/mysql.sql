CREATE DATABASE IF NOT EXISTS debate;
USE debate;

CREATE TABLE statement (
    id bigint AUTO_INCREMENT PRIMARY KEY,      -- 고유 ID
    debate_id VARCHAR(255) NOT NULL,        -- ROOM ID
    position VARCHAR(1000) NOT NULL,        -- ROOM ID
    user_id bigint NOT NULL,   				-- 사용자 ID
    nickname VARCHAR(255) NOT NULL,   			-- 사용자 ID
    round integer NOT NULL DEFAULT 0,   	-- 현재 라운드
    statement text NOT NULL,                -- 발언 내용 
    is_deleted BOOLEAN DEFAULT FALSE, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성 시각
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 수정 시각
);

CREATE TABLE summation (
    id bigint AUTO_INCREMENT PRIMARY KEY,      -- 고유 ID
    debate_id VARCHAR(255) NOT NULL,        -- ROOM ID
    position VARCHAR(1000) NOT NULL,        -- ROOM ID
    user_id bigint NOT NULL,   				-- 사용자 ID
    nickname VARCHAR(255) NOT NULL,   			-- 사용자 ID
    round integer NOT NULL DEFAULT 0,   	-- 현재 라운드
    summation text NOT NULL,                -- 발언 내용 
    is_deleted BOOLEAN DEFAULT FALSE, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성 시각
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 수정 시각
);

CREATE TABLE debate_column (
    id bigint AUTO_INCREMENT PRIMARY KEY,  -- 고유 ID
    title VARCHAR(255) NOT NULL,  -- 제목
    subtitle VARCHAR(255) NOT NULL,  -- 부제목
    category varchar(255) not null, 
    discussion_overview TEXT NOT NULL,  -- 토론 개요
    
    participant_1_name VARCHAR(255) NOT NULL,  -- 참가자 1 이름
    participant_1_statement TEXT NOT NULL,  -- 참가자 1 발언 내용
    participant_2_name VARCHAR(255) NOT NULL,  -- 참가자 2 이름
    participant_2_statement TEXT NOT NULL,  -- 참가자 2 발언 내용
    participant_3_name VARCHAR(255) NOT NULL,  -- 참가자 3 이름
    participant_3_statement TEXT NOT NULL,  -- 참가자 3 발언 내용
    participant_4_name VARCHAR(255) NOT NULL,  -- 참가자 4 이름
    participant_4_statement TEXT NOT NULL,  -- 참가자 4 발언 내용

    conclusion TEXT NOT NULL,  -- 토론 결론
    author_opinion TEXT NOT NULL,  -- 칼럼 작성자의 개인 의견
    like_count bigint default 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성 시간
    is_deleted BOOLEAN DEFAULT FALSE  -- 삭제 여부
);

CREATE TABLE likes (
    id INT AUTO_INCREMENT PRIMARY KEY,   -- 고유 ID
    user_id BIGINT NOT NULL,             -- 좋아요를 누른 사용자 ID
    debate_column_id INT NOT NULL,       -- 좋아요한 칼럼 ID
    is_deleted boolean default false, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 좋아요 누른 시간
    UNIQUE KEY (user_id, debate_column_id),  -- 한 유저가 같은 칼럼에 한 번만 좋아요 가능
    FOREIGN KEY (debate_column_id) REFERENCES debate_column(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

