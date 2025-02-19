SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS testdata
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE testdata;
-- 2. 테이블 생성

-- statement 테이블 생성
CREATE TABLE IF NOT EXISTS statement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    debate_id VARCHAR(50) NOT NULL,
    position VARCHAR(100) NOT NULL,
    user_id INT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    round INT NOT NULL,
    statement TEXT,
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- summation 테이블 생성
CREATE TABLE IF NOT EXISTS summation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    debate_id VARCHAR(50) NOT NULL,
    position VARCHAR(100) NOT NULL,
    user_id INT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    round INT NOT NULL,
    summation TEXT,
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 3. 데이터 삽입

-- debate_001 관련 INSERT (statement 테이블)
INSERT INTO statement (debate_id, position, user_id, nickname, round, statement, is_deleted)
VALUES
    ('debate_001', '낙태를 찬성한다.', 101, 'A', 1, '낙태는 여성의 자기결정권과 건강권의 문제입니다. 임신과 출산은 여성의 삶에 큰 영향을 미치며, 본인의 의지와 상관없이 출산을 강요하는 것은 인권 침해입니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 102, 'B', 2, '현실적으로 미혼모, 경제적 어려움, 건강 문제 등으로 인해 임신을 유지하기 어려운 여성들이 많습니다. 충분한 복지 없이 출산을 강요하는 것은 비윤리적입니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 103, 'C', 3, '하지만 태아 역시 독립적인 생명입니다. 여성의 권리도 중요하지만, 태아의 생명권이 경시되어서는 안 됩니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 104, 'D', 4, '낙태를 허용하면 생명 경시 풍조가 퍼질 위험이 있습니다. 생명의 소중함을 강조해야 하며, 낙태는 신중하게 다뤄야 합니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 101, 'A', 5, '태아가 생명이라는 것은 인정하지만, 여성의 삶도 중요합니다. 강제 출산은 여성의 건강과 삶을 위협합니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 102, 'B', 6, '모든 태아를 출산하게 한다고 해서 그 아이들이 행복하게 자랄 수 있는 환경이 보장되나요? 현실적인 지원이 부족한 상황에서 출산을 강요하는 것은 비현실적입니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 103, 'C', 7, '출산을 장려하고 양육을 지원하는 정책을 강화하는 것이 우선 아닐까요? 복지 제도를 개선하는 것이 더 나은 해결책일 것입니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 104, 'D', 8, '낙태를 쉽게 허용하면 생명에 대한 존중이 약화될 수 있습니다. 사회적 책임과 윤리적 기준을 신중히 고려해야 합니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 101, 'A', 9, '결국 이 문제는 여성의 자기결정권을 어디까지 인정할 것인지의 문제입니다. 강제 출산은 여성의 삶에 심각한 영향을 미칩니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 103, 'C', 10, '반대로 태아의 생명을 보호하는 것이야말로 우리 사회가 나아가야 할 방향입니다. 태아는 보호받아야 할 존재입니다.', FALSE);

-- debate_001 관련 INSERT (summation 테이블)
INSERT INTO summation (debate_id, position, user_id, nickname, round, summation, is_deleted)
VALUES
    ('debate_001', '낙태를 찬성한다.', 101, 'A', 1, '낙태는 여성의 자기결정권과 건강권의 문제입니다. 임신과 출산은 여성의 삶에 큰 영향을 미치며, 본인의 의지와 상관없이 출산을 강요하는 것은 인권 침해입니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 102, 'B', 2, '현실적으로 미혼모, 경제적 어려움, 건강 문제 등으로 인해 임신을 유지하기 어려운 여성들이 많습니다. 충분한 복지 없이 출산을 강요하는 것은 비윤리적입니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 103, 'C', 3, '하지만 태아 역시 독립적인 생명입니다. 여성의 권리도 중요하지만, 태아의 생명권이 경시되어서는 안 됩니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 104, 'D', 4, '낙태를 허용하면 생명 경시 풍조가 퍼질 위험이 있습니다. 생명의 소중함을 강조해야 하며, 낙태는 신중하게 다뤄야 합니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 101, 'A', 5, '태아가 생명이라는 것은 인정하지만, 여성의 삶도 중요합니다. 강제 출산은 여성의 건강과 삶을 위협합니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 102, 'B', 6, '모든 태아를 출산하게 한다고 해서 그 아이들이 행복하게 자랄 수 있는 환경이 보장되나요? 현실적인 지원이 부족한 상황에서 출산을 강요하는 것은 비현실적입니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 103, 'C', 7, '출산을 장려하고 양육을 지원하는 정책을 강화하는 것이 우선 아닐까요? 복지 제도를 개선하는 것이 더 나은 해결책일 것입니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 104, 'D', 8, '낙태를 쉽게 허용하면 생명에 대한 존중이 약화될 수 있습니다. 사회적 책임과 윤리적 기준을 신중히 고려해야 합니다.', FALSE),
    ('debate_001', '낙태를 찬성한다.', 101, 'A', 9, '결국 이 문제는 여성의 자기결정권을 어디까지 인정할 것인지의 문제입니다. 강제 출산은 여성의 삶에 심각한 영향을 미칩니다.', FALSE),
    ('debate_001', '낙태를 반대한다.', 103, 'C', 10, '반대로 태아의 생명을 보호하는 것이야말로 우리 사회가 나아가야 할 방향입니다. 태아는 보호받아야 할 존재입니다.', FALSE);

-- euthanasia_001 관련 INSERT (statement 테이블)
INSERT INTO statement (debate_id, position, user_id, nickname, round, statement, is_deleted)
VALUES
    ('euthanasia_001', '찬성', 101, 'A', 1, '안락사는 극심한 고통을 겪고 있는 환자들에게 존엄한 죽음을 선택할 권리를 주는 것입니다. 모든 생명은 소중하지만, 본인의 고통을 덜기 위한 선택권 역시 존중받아야 합니다.', FALSE),
    ('euthanasia_001', '반대', 102, 'B', 1, '생명은 인간이 함부로 결정할 수 있는 것이 아닙니다. 어떤 상황에서도 의료적 돌봄과 사회적 지원을 통해 생명을 유지하는 것이 우선되어야 합니다.', FALSE),
    ('euthanasia_001', '찬성', 103, 'C', 1, '말기 환자가 회복 불가능한 고통 속에서 살아가는 것은 인간다운 삶이 아닙니다. 본인의 의지에 따라 생을 마감하는 것이야말로 진정한 존엄입니다.', FALSE),
    ('euthanasia_001', '반대', 104, 'D', 1, '안락사를 허용하면 생명을 보호해야 하는 의료인의 기본 윤리를 위반하는 것이며, 악용될 가능성도 존재합니다.', FALSE),
    ('euthanasia_001', '찬성', 101, 'A', 2, '고통 속에서 살아가는 것이 과연 삶일까요? 죽음은 피할 수 없는 것이며, 불필요한 고통을 연장하는 것이 오히려 비인도적입니다.', FALSE),
    ('euthanasia_001', '반대', 102, 'B', 2, '현대 의료 기술이 발전하고 있으며, 고통을 줄일 수 있는 방법이 존재합니다. 안락사가 아니라 완화 치료에 집중해야 합니다.', FALSE),
    ('euthanasia_001', '찬성', 103, 'C', 2, '안락사가 허용되지 않으면, 환자들은 불법적이고 위험한 방법을 선택할 수도 있습니다. 차라리 법적 보호 아래 안전한 절차를 보장하는 것이 낫습니다.', FALSE),
    ('euthanasia_001', '반대', 104, 'D', 2, '사회적 약자가 본인의 의지가 아니라 경제적 이유로 안락사를 선택해야 하는 압박을 받을 수도 있습니다. 이는 매우 위험한 사회적 분위기를 조성할 수 있습니다.', FALSE);

-- euthanasia_001 관련 INSERT (summation 테이블)
INSERT INTO summation (debate_id, position, user_id, nickname, round, summation, is_deleted)
VALUES
    ('euthanasia_001', '찬성', 101, 'A', 1, '안락사는 극심한 고통을 겪고 있는 환자들에게 존엄한 죽음을 선택할 권리를 주는 것입니다. 모든 생명은 소중하지만, 본인의 고통을 덜기 위한 선택권 역시 존중받아야 합니다.', FALSE),
    ('euthanasia_001', '반대', 102, 'B', 1, '생명은 인간이 함부로 결정할 수 있는 것이 아닙니다. 어떤 상황에서도 의료적 돌봄과 사회적 지원을 통해 생명을 유지하는 것이 우선되어야 합니다.', FALSE),
    ('euthanasia_001', '찬성', 103, 'C', 1, '말기 환자가 회복 불가능한 고통 속에서 살아가는 것은 인간다운 삶이 아닙니다. 본인의 의지에 따라 생을 마감하는 것이야말로 진정한 존엄입니다.', FALSE),
    ('euthanasia_001', '반대', 104, 'D', 1, '안락사를 허용하면 생명을 보호해야 하는 의료인의 기본 윤리를 위반하는 것이며, 악용될 가능성도 존재합니다.', FALSE),
    ('euthanasia_001', '찬성', 101, 'A', 2, '고통 속에서 살아가는 것이 과연 삶일까요? 죽음은 피할 수 없는 것이며, 불필요한 고통을 연장하는 것이 오히려 비인도적입니다.', FALSE),
    ('euthanasia_001', '반대', 102, 'B', 2, '현대 의료 기술이 발전하고 있으며, 고통을 줄일 수 있는 방법이 존재합니다. 안락사가 아니라 완화 치료에 집중해야 합니다.', FALSE),
    ('euthanasia_001', '찬성', 103, 'C', 2, '안락사가 허용되지 않으면, 환자들은 불법적이고 위험한 방법을 선택할 수도 있습니다. 차라리 법적 보호 아래 안전한 절차를 보장하는 것이 낫습니다.', FALSE),
    ('euthanasia_001', '반대', 104, 'D', 2, '사회적 약자가 본인의 의지가 아니라 경제적 이유로 안락사를 선택해야 하는 압박을 받을 수도 있습니다. 이는 매우 위험한 사회적 분위기를 조성할 수 있습니다.', FALSE);
