/*
 Navicat Premium Data Transfer

 Source Server         : wms
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : courseplatform

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 24/12/2024 15:58:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for classroom
-- ----------------------------
DROP TABLE IF EXISTS `classroom`;
CREATE TABLE `classroom`  (
  `classroom_id` bigint NOT NULL AUTO_INCREMENT COMMENT '教室ID',
  `building_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '逸夫楼' COMMENT '教学楼名称',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教室编号',
  `capacity` int NOT NULL COMMENT '教室容量',
  `row_count` int NOT NULL COMMENT '座位行数',
  `column_count` int NOT NULL COMMENT '座位列数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '教室状态 0-维护中 1-可用',
  PRIMARY KEY (`classroom_id`) USING BTREE,
  UNIQUE INDEX `uk_room_number`(`building_name` ASC, `room_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教室信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classroom
-- ----------------------------
INSERT INTO `classroom` VALUES (1, '逸夫楼', '101', 40, 5, 8, 1);
INSERT INTO `classroom` VALUES (2, '逸夫楼', '102', 60, 4, 15, 1);

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `likes_count` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, '22301012', '没事了', 0, '2024-11-18 10:40:02', '2024-11-18 10:40:02');
INSERT INTO `comments` VALUES (2, 1, '10111011', '你好呀', 0, '2024-11-20 11:38:13', '2024-11-20 11:38:13');

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_message` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bot_message` varchar(15000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversation
-- ----------------------------
INSERT INTO `conversation` VALUES (1, '李四', '你好', '你好！作为一个人工智能助手，我随时准备帮助你解答问题或提供帮助。请告诉我你的问题，我会尽力提供帮助。', '2024-12-13 11:28:04');
INSERT INTO `conversation` VALUES (2, '李四', '你好', '你好！作为一个人工智能助手，我准备回答你提出的问题。请开始提问，我会尽力提供智能的信息问答。', '2024-12-13 11:41:34');

-- ----------------------------
-- Table structure for course_homework
-- ----------------------------
DROP TABLE IF EXISTS `course_homework`;
CREATE TABLE `course_homework`  (
  `homework_id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业描述',
  `attachment_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件路径',
  `start_time` timestamp NOT NULL COMMENT '开始时间',
  `deadline` timestamp NOT NULL COMMENT '截止时间',
  `total_score` int NOT NULL DEFAULT 100 COMMENT '总分',
  `publisher_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布教师ID',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '作业状态 0-已关闭 1-进行中',
  PRIMARY KEY (`homework_id`) USING BTREE,
  INDEX `idx_course_homework`(`course_id` ASC) USING BTREE,
  INDEX `idx_publisher`(`publisher_id` ASC) USING BTREE,
  CONSTRAINT `fk_homework_course` FOREIGN KEY (`course_id`) REFERENCES `course_information` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_homework_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程作业表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_homework
-- ----------------------------
INSERT INTO `course_homework` VALUES (1, 1, '1111', '111', '', '2024-11-29 11:46:29', '2024-12-06 00:00:00', 100, '10111011', NULL, 0);
INSERT INTO `course_homework` VALUES (2, 2, '222', '222', '', '2024-11-29 11:55:24', '2024-11-30 00:00:00', 100, '10111011', NULL, 0);
INSERT INTO `course_homework` VALUES (3, 4, '333', '33333', '', '2024-11-29 12:44:19', '2024-11-30 00:00:00', 100, '10111011', NULL, 0);
INSERT INTO `course_homework` VALUES (6, 1, '第八次作业', '您好', 'F:/CourseUploads/uploads/homework72d3d48f-b6c4-4e97-9eb1-416ce73b00ed.pdf', '2024-11-29 14:00:02', '2024-11-30 00:00:00', 100, '10111011', NULL, 0);
INSERT INTO `course_homework` VALUES (7, 1, '实训作业', '取得', 'F:/CourseUploads/uploads/homework/3024efef-b386-4a36-9a9f-7002646cfcf0.pdf', '2024-11-29 14:23:25', '2024-11-21 00:00:00', 100, '10111011', NULL, 0);
INSERT INTO `course_homework` VALUES (8, 2, '第三次作业', '21111', 'F:/CourseUploads/uploads/homework/4af14fed-9cf3-4bb9-aa9c-8702ad6039fc.pptx', '2024-11-29 14:55:04', '2024-11-30 00:00:00', 100, '10111011', NULL, 0);

-- ----------------------------
-- Table structure for course_information
-- ----------------------------
DROP TABLE IF EXISTS `course_information`;
CREATE TABLE `course_information`  (
  `course_id` bigint NOT NULL AUTO_INCREMENT,
  `calendar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `course_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `syllabus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`course_id`) USING BTREE,
  UNIQUE INDEX `idx_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course_information
-- ----------------------------
INSERT INTO `course_information` VALUES (1, '每周周三 16：20-18：10', '这个课也特别不好', '软件分析与设计', 'dbnsjd使得', '10111011');
INSERT INTO `course_information` VALUES (2, '每周周四 16：20-18：10', '这个课比较难', '算法设计', 'bfjbc', '10111011');
INSERT INTO `course_information` VALUES (3, '没有', '没有', '微积分', 'njs', '10111012');
INSERT INTO `course_information` VALUES (4, '没事的', '没有人', '微积分', '产生的', '10111011');

-- ----------------------------
-- Table structure for course_notice
-- ----------------------------
DROP TABLE IF EXISTS `course_notice`;
CREATE TABLE `course_notice`  (
  `notice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `course_id` bigint NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `publisher_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布者ID',
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_publisher`(`publisher_id` ASC) USING BTREE,
  CONSTRAINT `fk_notice_course` FOREIGN KEY (`course_id`) REFERENCES `course_information` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_notice_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_notice
-- ----------------------------
INSERT INTO `course_notice` VALUES (4, 1, '早点写作业', '没有时间了', '2024-12-20 13:19:19', '10111011');
INSERT INTO `course_notice` VALUES (5, 2, '算法考试很难', '做好心理准备', '2024-12-20 13:19:55', '10111011');

-- ----------------------------
-- Table structure for course_qa_record
-- ----------------------------
DROP TABLE IF EXISTS `course_qa_record`;
CREATE TABLE `course_qa_record`  (
  `qa_id` bigint NOT NULL AUTO_INCREMENT COMMENT '问答ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生ID',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'AI回答内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`qa_id`) USING BTREE,
  INDEX `idx_course_qa`(`course_id` ASC) USING BTREE,
  INDEX `idx_student_qa`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_qa_course` FOREIGN KEY (`course_id`) REFERENCES `course_information` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_qa_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI问答记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_qa_record
-- ----------------------------
INSERT INTO `course_qa_record` VALUES (1, 1, '22301012', '您好', '您好👋！我是人工智能助手智谱清言（ChatGLM），很高兴见到您，欢迎问我任何问题。', '2024-12-19 13:40:15');
INSERT INTO `course_qa_record` VALUES (2, 1, '22301012', '请告诉我如何画用例图\n', '用例图（Use Case Diagram）是UML（统一建模语言）中的一种图，用于描述系统的功能和用户（即参与者）与这些功能的交互。以下是绘制用例图的基本步骤：\n\n### 1. 确定系统边界\n- **定义系统**：明确你要建模的系统是什么。\n- **确定边界**：决定哪些功能属于系统内部，哪些是外部参与者。\n\n### 2. 识别参与者（Actors）\n- **定义参与者**：参与者是与系统交互的外部实体，可以是用户、其他系统或硬件设备。\n- **命名参与者**：给每个参与者一个清晰的名字，如“用户”、“管理员”、“支付系统”等。\n\n### 3. 识别用例（Use Cases）\n- **定义用例**：用例是系统提供的具体功能或服务。\n- **命名用例**：用简洁、描述性的动词短语命名，如“登录”、“下单”、“支付”等。\n\n### 4. 确定参与者和用例之间的关系\n- **关联关系**：参与者与用例之间的基本关系，表示参与者使用用例。\n- **包含关系**：一个用例包含另一个用例的行为。\n- **扩展关系**：一个用例在特定条件下扩展另一个用例的行为。\n- **泛化关系**：一个用例是另一个用例的特化。\n\n### 5. 绘制用例图\n- **工具选择**：可以使用各种UML建模工具，如Visio、Lucidchart、StarUML等。\n- **绘制参与者**：用一个小人图标表示参与者，旁边标注名字。\n- **绘制用例**：用椭圆表示用例，内部标注名字。\n- **连接关系**：用线条连接参与者和用例，表示它们之间的关系。\n\n### 6. 添加注释和细节\n- **注释**：对图中的元素进行解释，使其更易理解。\n- **细节**：可以在用例图中添加更多的细节，如前置条件、后置条件等。\n\n### 示例步骤\n1. **确定系统**：例如，一个在线购物系统。\n2. **识别参与者**：顾客、管理员、支付系统。\n3. **识别用例**：浏览商品、添加到购物车、结账、管理商品。\n4. **确定关系**：\n   - 顾客 → 浏览商品、添加到购物车、结账\n   - 管理员 → 管理商品\n   - 支付系统 → 结账（关联关系）\n5. **绘制图形**：\n   - 绘制三个参与者图标，并标注名字。\n   - 绘制四个用例椭圆，并标注名字。\n   - 用线条连接参与者和相应的用例。\n\n### 工具使用示例（以Lucidchart为例）\n1. **打开Lucidchart**，创建一个新的UML用例图。\n2. **拖拽参与者图标**到画布上，并命名。\n3. **拖拽用例椭圆**到画布上，并命名。\n4. **使用连接工具**将参与者和用例连接起来。\n5. **添加注释**，保存并导出图表。\n\n通过以上步骤，你可以创建一个清晰、有用的用例图，帮助团队成员理解系统的功能和用户交互。', '2024-12-19 13:42:03');
INSERT INTO `course_qa_record` VALUES (3, 1, '22301012', '我有点累', '听到你说有点累，我建议你先停下来休息一下。疲劳可能是身体和心理需要恢复的信号。以下是一些放松和恢复的小建议：\n\n1. **短暂休息**：闭上眼睛，深呼吸几次，给自己几分钟的静思时间。\n2. **适量运动**：轻度运动如散步可以帮助缓解紧张。\n3. **充足睡眠**：确保你有足够的睡眠，这对恢复精力至关重要。\n4. **健康饮食**：均衡的饮食有助于身体获得必要的能量。\n5. **水分补充**：多喝水，保持身体水分充足。\n6. **放松活动**：听音乐、阅读或进行其他放松身心的活动。\n\n如果感到压力过大或疲劳持续，也可以考虑与朋友或家人交流，或者寻求专业的帮助。\n\n记得，照顾好自己是非常重要的。希望这些建议能帮到你，愿你早日恢复元气！🌟', '2024-12-19 13:51:10');
INSERT INTO `course_qa_record` VALUES (4, 1, '22301012', '谢谢你', '不客气！如果您有任何问题或需要帮助，请随时告诉我。祝您有一个美好的一天！🌟', '2024-12-19 13:51:30');
INSERT INTO `course_qa_record` VALUES (5, 2, '22301012', '您好\n', '您好！很高兴能帮助您解答算法设计课程中的问题。无论您是对某个算法的具体实现有疑问，还是对算法设计的基本原理和策略需要进一步的了解，我都很乐意为您提供指导和帮助。请随时提出您的问题，我们一起来探讨和解决。', '2024-12-20 12:53:24');
INSERT INTO `course_qa_record` VALUES (6, 1, '22301012', '您好', '您好！很高兴能在这里帮助您解答软件分析与设计课程中的疑问。无论您是遇到了理论学习上的困惑，还是实践操作中的难题，都可以随时向我提问。我会尽我所能，结合多年的教学经验和专业知识，为您提供详尽的解答和建议。那么，您具体想了解哪方面的内容呢？', '2024-12-20 15:00:08');

-- ----------------------------
-- Table structure for course_resource
-- ----------------------------
DROP TABLE IF EXISTS `course_resource`;
CREATE TABLE `course_resource`  (
  `resource_id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `upload_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `course_resource_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course_information` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course_resource
-- ----------------------------
INSERT INTO `course_resource` VALUES (9, 1, '1.pdf', '4d23b3b3-a49d-474f-8ed3-4b65084da19c.pdf', 'pdf', NULL);
INSERT INTO `course_resource` VALUES (10, 1, '04-Instruction system 4.1&4.2-中文.pdf', '037511cc-d877-40c5-b36c-2e71b687e0a2.pdf', 'pdf', NULL);
INSERT INTO `course_resource` VALUES (11, 4, '机组作业5.pdf', 'cf4f1b55-61e3-4d95-bafd-0d6921f25628.pdf', 'document', NULL);

-- ----------------------------
-- Table structure for course_selection
-- ----------------------------
DROP TABLE IF EXISTS `course_selection`;
CREATE TABLE `course_selection`  (
  `selection_id` bigint NOT NULL AUTO_INCREMENT COMMENT '选课ID',
  `course_id` bigint NOT NULL,
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生ID',
  `select_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '选课状态 0-退选 1-正常',
  PRIMARY KEY (`selection_id`) USING BTREE,
  UNIQUE INDEX `uk_course_student`(`course_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_selection_course` FOREIGN KEY (`course_id`) REFERENCES `course_information` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_selection_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '选课关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_selection
-- ----------------------------
INSERT INTO `course_selection` VALUES (1, 1, '22301012', '2024-11-20 14:55:07', 1);
INSERT INTO `course_selection` VALUES (2, 2, '22301012', '2024-11-20 15:29:08', 1);

-- ----------------------------
-- Table structure for exam_arrangement
-- ----------------------------
DROP TABLE IF EXISTS `exam_arrangement`;
CREATE TABLE `exam_arrangement`  (
  `arrangement_id` bigint NOT NULL AUTO_INCREMENT COMMENT '安排ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `classroom_id` bigint NOT NULL COMMENT '教室ID',
  `is_random_seating` tinyint NOT NULL DEFAULT 0 COMMENT '是否随机座位 0-顺序 1-随机',
  `invigilator_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '监考教师ID',
  PRIMARY KEY (`arrangement_id`) USING BTREE,
  INDEX `idx_exam_arrangement`(`exam_id` ASC) USING BTREE,
  INDEX `idx_classroom_arrangement`(`classroom_id` ASC) USING BTREE,
  INDEX `fk_arrangement_invigilator`(`invigilator_id` ASC) USING BTREE,
  CONSTRAINT `fk_arrangement_classroom` FOREIGN KEY (`classroom_id`) REFERENCES `classroom` (`classroom_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_arrangement_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_information` (`exam_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_arrangement_invigilator` FOREIGN KEY (`invigilator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考场安排表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_arrangement
-- ----------------------------
INSERT INTO `exam_arrangement` VALUES (3, 1, 1, 1, '10111011');
INSERT INTO `exam_arrangement` VALUES (4, 1, 2, 0, '10111011');
INSERT INTO `exam_arrangement` VALUES (5, 3, 2, 0, '10111011');

-- ----------------------------
-- Table structure for exam_information
-- ----------------------------
DROP TABLE IF EXISTS `exam_information`;
CREATE TABLE `exam_information`  (
  `exam_id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '考试说明',
  `exam_date` date NOT NULL COMMENT '考试日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `total_score` int NOT NULL DEFAULT 100 COMMENT '总分',
  `teacher_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '出题教师ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '考试状态 0-未开始 1-进行中 2-已结束',
  PRIMARY KEY (`exam_id`) USING BTREE,
  INDEX `idx_course_exam`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_exam`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_exam_course` FOREIGN KEY (`course_id`) REFERENCES `course_information` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_exam_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_information
-- ----------------------------
INSERT INTO `exam_information` VALUES (1, 1, '期末考试', '开卷', '2024-12-27', '14:16:16', '18:28:21', 100, '10111011', NULL, 0);
INSERT INTO `exam_information` VALUES (2, 2, '小测', 'vbjwa`1', '2024-01-19', '14:51:42', '15:51:55', 100, '10111011', NULL, 0);
INSERT INTO `exam_information` VALUES (3, 2, '期中考试', '没有', '2025-01-16', '14:00:00', '16:00:00', 100, '10111011', NULL, 0);

-- ----------------------------
-- Table structure for exam_paper
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper`;
CREATE TABLE `exam_paper`  (
  `paper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `paper_template_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷模板文件路径',
  `answer_template_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '答题卡模板文件路径',
  `ocr_config` json NULL COMMENT 'OCR识别配置JSON',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`paper_id`) USING BTREE,
  INDEX `idx_exam_paper`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `fk_paper_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_information` (`exam_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_paper
-- ----------------------------

-- ----------------------------
-- Table structure for exam_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_question`;
CREATE TABLE `exam_question`  (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '试题ID',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `question_type` tinyint NOT NULL COMMENT '题型 1-单选 2-多选 3-判断 4-填空 5-主观',
  `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `standard_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标准答案',
  `score` decimal(5, 2) NOT NULL COMMENT '分值',
  `question_order` int NOT NULL COMMENT '题目顺序',
  `options_json` json NULL COMMENT '选项JSON(选择题用)',
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `idx_paper_question`(`paper_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_paper` FOREIGN KEY (`paper_id`) REFERENCES `exam_paper` (`paper_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question
-- ----------------------------

-- ----------------------------
-- Table structure for exam_seating
-- ----------------------------
DROP TABLE IF EXISTS `exam_seating`;
CREATE TABLE `exam_seating`  (
  `seating_id` bigint NOT NULL AUTO_INCREMENT COMMENT '座位安排ID',
  `arrangement_id` bigint NOT NULL COMMENT '考场安排ID',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生ID',
  `seat_row` int NOT NULL COMMENT '座位行号',
  `seat_column` int NOT NULL COMMENT '座位列号',
  `question_version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '试题版本号(用于选择题打乱)',
  PRIMARY KEY (`seating_id`) USING BTREE,
  UNIQUE INDEX `uk_arrangement_seat`(`arrangement_id` ASC, `seat_row` ASC, `seat_column` ASC) USING BTREE,
  INDEX `idx_student_seating`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_seating_arrangement` FOREIGN KEY (`arrangement_id`) REFERENCES `exam_arrangement` (`arrangement_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_seating_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考生座位安排表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_seating
-- ----------------------------
INSERT INTO `exam_seating` VALUES (7, 5, '22301012', 1, 1, '0ea15046');
INSERT INTO `exam_seating` VALUES (8, 3, '22301012', 1, 1, '33d79e3b');
INSERT INTO `exam_seating` VALUES (10, 4, '22301012', 1, 1, '4248eb23');

-- ----------------------------
-- Table structure for homework_submission
-- ----------------------------
DROP TABLE IF EXISTS `homework_submission`;
CREATE TABLE `homework_submission`  (
  `submission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '作业内容',
  `attachment_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件路径',
  `submit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教师反馈',
  `grade_time` timestamp NULL DEFAULT NULL COMMENT '批改时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-未批改 1-已批改',
  PRIMARY KEY (`submission_id`) USING BTREE,
  UNIQUE INDEX `uk_homework_student`(`homework_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_student_submission`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_submission_homework` FOREIGN KEY (`homework_id`) REFERENCES `course_homework` (`homework_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_submission_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业提交表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of homework_submission
-- ----------------------------
INSERT INTO `homework_submission` VALUES (11, 1, '22301012', '写完了', '', NULL, 100, '很好', '2024-11-29 14:08:34', 1);
INSERT INTO `homework_submission` VALUES (12, 8, '22301012', '好滴', '', NULL, 98, '挺好的', '2024-11-29 14:56:20', 1);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` int NOT NULL,
  `menuCode` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单编码',
  `menuName` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单名字',
  `menuLevel` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单级别',
  `menuParentCode` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单的父code',
  `menuClick` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '点击触发的函数',
  `menuRight` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限 0超级管理员，1表示管理员，2表示普通用户，可以用逗号组合使用',
  `menuComponent` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `menuIcon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '001', '我的课程', '1', NULL, 'SCourse', '0', 'student/SCourse', 'el-icon-s-management');
INSERT INTO `menu` VALUES (2, '002', '课程管理', '1', NULL, 'TCourse', '1', 'teacher/TCourse', 'el-icon-s-management');
INSERT INTO `menu` VALUES (3, '003', '通知中心', '1', NULL, 'SNotification', '0', 'student/SNotification', 'el-icon-s-claim');
INSERT INTO `menu` VALUES (4, '004', '通知管理（老师）', '1', NULL, 'TNotification', '2', 'teacher/TNotification', 'el-icon-s-claim');
INSERT INTO `menu` VALUES (5, '005', '我的作业', '1', NULL, 'SHomework', '0', 'student/SHomework', 'el-icon-edit');
INSERT INTO `menu` VALUES (6, '006', '作业管理', '1', NULL, 'THomework', '1', 'teacher/THomework', 'el-icon-edit');
INSERT INTO `menu` VALUES (7, '007', '我的考试', '1', NULL, 'SExam', '0', 'student/SExam', 'el-icon-document-copy');
INSERT INTO `menu` VALUES (8, '008', '考试管理', '1', NULL, 'TExam', '1', 'teacher/TExam', 'el-icon-document-copy');
INSERT INTO `menu` VALUES (9, '009', 'AI助手', '1', NULL, 'Helper', '0', 'Helper', 'el-icon-user-solid');
INSERT INTO `menu` VALUES (10, '010', '答疑讨论区', '1', NULL, 'STalk', '0', 'student/STalk', 'el-icon-s-comment');
INSERT INTO `menu` VALUES (11, '011', '答疑讨论区', '1', NULL, 'TTalk', '1', 'teacher/TTalk', 'el-icon-s-comment');
INSERT INTO `menu` VALUES (12, '012', '我的收藏夹', '1', NULL, 'Bookmark', '0,1', 'Bookmark', 'el-icon-s-opportunity\r\nel-icon-s-opportunity\r\nel-icon-s-opportunity');

-- ----------------------------
-- Table structure for post_bookmarks
-- ----------------------------
DROP TABLE IF EXISTS `post_bookmarks`;
CREATE TABLE `post_bookmarks`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_post_bookmark`(`post_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `post_bookmarks_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_bookmarks_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_bookmarks
-- ----------------------------
INSERT INTO `post_bookmarks` VALUES (4, 1, '10111011', '2024-11-19 20:56:55');
INSERT INTO `post_bookmarks` VALUES (7, 1, '22301012', '2024-11-20 18:02:47');

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `likes_count` int NULL DEFAULT 0,
  `bookmarks_count` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of posts
-- ----------------------------
INSERT INTO `posts` VALUES (1, '22301012', '天哪，软件分析设计好难学', NULL, 8, 2, '2024-11-14 22:20:33', '2024-12-20 14:50:53');

-- ----------------------------
-- Table structure for question_score
-- ----------------------------
DROP TABLE IF EXISTS `question_score`;
CREATE TABLE `question_score`  (
  `score_id` bigint NOT NULL AUTO_INCREMENT COMMENT '得分ID',
  `answer_id` bigint NOT NULL COMMENT '答卷ID',
  `question_id` bigint NOT NULL COMMENT '试题ID',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `is_correct` tinyint NULL DEFAULT NULL COMMENT '是否正确 0-错误 1-正确',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评语',
  PRIMARY KEY (`score_id`) USING BTREE,
  INDEX `idx_answer_score`(`answer_id` ASC) USING BTREE,
  INDEX `idx_question_score`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_score_answer` FOREIGN KEY (`answer_id`) REFERENCES `student_answer` (`answer_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_score_question` FOREIGN KEY (`question_id`) REFERENCES `exam_question` (`question_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目得分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_score
-- ----------------------------

-- ----------------------------
-- Table structure for student_answer
-- ----------------------------
DROP TABLE IF EXISTS `student_answer`;
CREATE TABLE `student_answer`  (
  `answer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '答卷ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生ID',
  `answer_paper_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '答题卡扫描文件路径',
  `upload_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总得分',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0-待批改 1-批改中 2-已批改',
  PRIMARY KEY (`answer_id`) USING BTREE,
  UNIQUE INDEX `uk_exam_student`(`exam_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_student_answer`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_answer_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_information` (`exam_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_answer_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生答卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answer
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名字',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `role_id` int NULL DEFAULT NULL COMMENT '0学生，1老师',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('10111011', '李八八', 'fcea920f7412b5da7be0cf42b8c93759', '13824643786', 1);
INSERT INTO `user` VALUES ('22301012', '李四', 'e10adc3949ba59abbe56e057f20f883e', '13836788938', 0);

SET FOREIGN_KEY_CHECKS = 1;
