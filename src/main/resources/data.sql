-- 기술 수준 코드 - ID: 1부터 시작
INSERT INTO system_code (id, type, value, display_name) VALUES
                                                           (1, 'LEVEL', 'LV0', '입문 (Beginner)'),
                                                           (2, 'LEVEL', 'LV1', '초급 (Junior)'),
                                                           (3, 'LEVEL', 'LV2', '중급 (Intermediate)'),
                                                           (4, 'LEVEL', 'LV3', '고급 (Senior)'),
                                                           (5, 'LEVEL', 'LV4', '전문가 (Expert)');

-- 직무 분야 코드 - ID: 10부터 시작
INSERT INTO system_code (id, type, value, display_name) VALUES
                                                           (10, 'FIELD', 'FRONT', '프론트엔드'),
                                                           (11, 'FIELD', 'BACK', '백엔드'),
                                                           (12, 'FIELD', 'MOBILE', '모바일 개발'),
                                                           (13, 'FIELD', 'DATA', '데이터 분석'),
                                                           (14, 'FIELD', 'DEVOPS', '데브옵스/인프라');