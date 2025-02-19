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

CREATE TABLE report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debate_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    total INT NOT NULL,
    total_explanation TEXT NOT NULL,
    reasoning INT NOT NULL,
    reasoning_explanation TEXT NOT NULL,
    expression INT NOT NULL,
    expression_explanation TEXT NOT NULL,
    strategy INT NOT NULL,
    strategy_explanation TEXT NOT NULL,
    interaction INT NOT NULL,
    interaction_explanation TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE practice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- 고유 ID (자동 증가)
    problem_id VARCHAR(255) NOT NULL,      -- 문제 ID
    user_id BIGINT NOT NULL,               -- 사용자 ID
    input TEXT NOT NULL,                   -- 입력 데이터
    output TEXT NOT NULL,                  -- 출력 데이터
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 소프트 삭제 처리
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 생성 시각
);

CREATE TABLE basic_practice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유 ID
    topic VARCHAR(255) NOT NULL,  -- 토론 주제
    position VARCHAR(255) NOT NULL,  -- 토론 주제
    question VARCHAR(255) NOT NULL,  -- 질문 내용
    user_id BIGINT NOT NULL,  -- 사용자 ID
    statement TEXT NOT NULL,  -- 발언 내용
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 소프트 삭제 처리
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성 시각
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 수정 시각
);