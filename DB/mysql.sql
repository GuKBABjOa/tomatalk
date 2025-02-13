CREATE DATABASE IF NOT EXISTS debate;
USE debate;

CREATE TABLE statement (
    id INT AUTO_INCREMENT PRIMARY KEY,      -- 고유 ID
    room_id VARCHAR(255) NOT NULL,          -- ROOM ID
    user_id varchar(255) NOT NULL,   		-- 사용자 ID 
    round integer NOT NULL DEFAULT 0,   	-- 현재 라운드
    statement text NOT NULL,                -- 발언 내용 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성 시각
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 수정 시각
);